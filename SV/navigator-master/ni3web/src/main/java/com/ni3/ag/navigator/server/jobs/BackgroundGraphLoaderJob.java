package com.ni3.ag.navigator.server.jobs;

import java.util.*;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.cache.GraphNi3Engine;
import com.ni3.ag.navigator.server.dao.FavoriteDAO;
import com.ni3.ag.navigator.server.domain.*;
import com.ni3.ag.navigator.server.services.CISObjectProviderService;
import com.ni3.ag.navigator.server.services.SchemaLoaderService;
import com.ni3.ag.navigator.server.servlets.GraphServlet;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXML;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXMLAttribute;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

public class BackgroundGraphLoaderJob implements StatefulJob{
	private static final Logger log = Logger.getLogger(BackgroundGraphLoaderJob.class);
	private int MAX_LOAD_COUNT_PER_RUN = 500;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException{
		try{
			log.info("[START] background loader started");
			Integer count = jobExecutionContext.getJobDetail().getJobDataMap().getIntegerFromString("graph.loader.loadCountPerRun");
			log.debug("count: " + count);
			if(count != null){
				log.info("Setting MAX_LOAD_COUNT_PER_RUN to: " + count);
				MAX_LOAD_COUNT_PER_RUN = count;
			}
			doJob();
		}finally{
			log.info("[FINISH] background loader started");
		}
	}

	private void doJob(){
		SchemaLoaderService schemaLoaderService = NSpringFactory.getInstance().getSchemaLoaderService();
		List<Schema> schemas = schemaLoaderService.getAllSchemas();
		int loadedCount = 0;
		for(Schema sch : schemas){
			loadedCount += processSchema(sch, loadedCount);
		}
	}

	private int processSchema(Schema sch, int loadedCount){
		log.debug("processing schema: " + sch.getName());
		GraphNi3Engine graph = GraphServlet.getGraph(sch.getId());
		log.debug("Got graph for schema: " + graph);
		FavoriteDAO favoriteDAO = NSpringFactory.getInstance().getFavoritesDao();
		List<Favorite> favorites = favoriteDAO.getBySchema(sch.getId());
		Collection<Integer> favoriteNodeIds = getNodeIdsFromFavorites(favorites);
		loadedCount = loadNodes(favoriteNodeIds, sch, 0);
		if(loadedCount >= MAX_LOAD_COUNT_PER_RUN){
			log.debug("Load count exceeds max allowed: loaded:" + loadedCount + " maxAllowed:" + MAX_LOAD_COUNT_PER_RUN);
			return loadedCount;
		}
		log.debug("Loaded from favorites: " + loadedCount);
		List<Integer> nodeIds = getNodeIdsForSchema(sch);
		loadedCount += loadNodes(nodeIds, sch, loadedCount);
		log.debug("Loaded count: " + loadedCount);
		return loadedCount;
	}

	private int loadNodes(Collection<Integer> nodeIds, Schema sch, int loadedCount){
		int counter = loadedCount;
		GraphNi3Engine graph = GraphServlet.getGraph(sch.getId());
		log.debug("Got graph " + graph + " for schema " + sch.getName());
		for (Integer id : nodeIds){
			if (graph.containsNode(id))
				continue;
			Node n = graph.getNode(id, 1, new DataFilter(sch, new ArrayList<Integer>()));
			if(n != null)
				n.getInEdges();//if node is lazy - forces loading of data of this node
			counter++;
			if (counter >= MAX_LOAD_COUNT_PER_RUN)
				break;
			if (counter % 100 == 0)
				log.debug("Nodes loaded: " + (counter - loadedCount));
		}
		return counter - loadedCount;
	}

	private List<Integer> getNodeIdsForSchema(Schema sch){
		CISObjectProviderService objectProviderService = NSpringFactory.getInstance().getCISObjectProviderService();
		List<Integer> ids = new ArrayList<Integer>();
		for(ObjectDefinition od : sch.getDefinitions()){
			log.debug("Getting ids for entity: " + od.getName());
			ids.addAll(objectProviderService.getNodeIds(od));
		}
		return ids;
	}

	private Collection<Integer> getNodeIdsFromFavorites(List<Favorite> favorites){
		Set<Integer> ids = new HashSet<Integer>();
		for(Favorite f : favorites)
			ids.addAll(getIdsFromFavorite(f));
		return ids;
	}

	private Collection<Integer> getIdsFromFavorite(Favorite f){
		List<Integer> ids = new ArrayList<Integer>();
		NanoXML xml = new NanoXML(f.getData(), 0, f.getData().length());
		if(!xml.Tag.Name.equals("NI3")){
			log.error("Cannot parse favorite: " + f.getId() + " No `NI3` tag found");
			log.error("DATA: " + f.getData());
			return ids;
		}
		NanoXML graphTag = getElement(xml, "Graph");
		if(graphTag == null){
			log.error("Cannot parse favorite: " + f.getId() + " No `Graph` tag found");
			log.error("DATA: " + f.getData());
			return ids;
		}
		NanoXML nodeListTag = getElement(graphTag, "Nodes");
		if(nodeListTag == null){
			log.error("Cannot parse favorite: " + f.getId() + " No `Nodes` tag found");
			log.error("DATA: " + f.getData());
			return ids;
		}
		NanoXMLAttribute nodeListAttribute = nodeListTag.Tag.getAttribute("List");
		if(nodeListAttribute == null){
			log.error("Cannot parse favorite: " + f.getId() + " No `List` attribute found");
			log.error("DATA: " + f.getData());
			return ids;
		}
		String nodeList = nodeListAttribute.getValue();
		log.debug("Nodes: " + nodeList);
		String[] idArray = nodeList.split(",");
		for(String s : idArray){
			if(s.isEmpty())
				continue;
			try{
				int id = Integer.parseInt(s);
				ids.add(id);
			}catch (NumberFormatException ex){
				log.error("Error parsing id: `" + s + "`");
			}
		}
		return ids;
	}

	private NanoXML getElement(NanoXML xml, String name){
		NanoXML tag;
		while((tag = xml.getNextElement()) != null){
			if(tag.getName().equals(name))
				return tag;
		}
		return null;
	}
}
