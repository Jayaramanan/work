package com.ni3.ag.navigator.server.graphXXL;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.domain.*;
import com.ni3.ag.navigator.server.services.SchemaLoaderService;
import org.apache.log4j.Logger;

public class SalesForceLazyGraph extends LazyGraph{
	private static final Logger log = Logger.getLogger(SalesForceLazyGraph.class);

	public SalesForceLazyGraph(int schemaId){
		super(schemaId);
	}

	@Override
	public void syncGraphWithDB(boolean background){
		if(!background)
			return;
		List<CisObject> updatedObjects = getUpdatedObjects();
		if (updatedObjects.isEmpty()){
			lastUpdateTime = new Date();
			return;
		}

		final SchemaLoaderService service = NSpringFactory.getInstance().getSchemaLoaderService();
		List<Schema> schemas = service.getAllSchemas();
		Map<Integer, ObjectDefinition> entities = new HashMap<Integer, ObjectDefinition>();
		for (Schema sch : schemas)
			for (ObjectDefinition e : sch.getDefinitions())
				entities.put(e.getId(), e);
		log.info("Synchronizing graph with SalesForce");
		if (log.isDebugEnabled())
			log.debug("Last sync was " + lastUpdateTime);

		for (CisObject co : updatedObjects){
			ObjectDefinition entity = entities.get(co.getTypeId());
			if (log.isDebugEnabled())
				log.debug("Object: " + co.getId() + " changed since last sync");
			switch (co.getStatus()){
				case Normal:
				case Locked:{
					processUpdatedObject(co, entity, entities);
				}break;
				case Deleted:
				case Merged:{
					if(entity.isNode()){
						safeDeleteNode(co);
					}else{
						safeDeleteEdge(co);
					}
				}break;
			}
		}
		lastUpdateTime = new Date();

		touchUpdatedObjects();
	}

	private void safeDeleteEdge(CisObject co){
		if(!edgeMap.containsKey(co.getId()))
			return;
		deleteEdge(co.getId());
	}

	private void safeDeleteNode(CisObject co){
		if(!nodeMap.containsKey(co.getId()))
			return;
		Node n = nodeMap.get(co.getId());
		deleteEdges(n.getInEdges(), n.getOutEdges());
		deleteNode(co.getId());
	}

	//forcing reload of object with all it's connections
	private void processUpdatedObject(CisObject co, ObjectDefinition entity, Map<Integer, ObjectDefinition> entities){
		Node n = getNode(co.getId());
		Edge[] outEdges = n.getOutEdges().toArray(new Edge[n.getOutEdges().size()]);
		Edge[] inEdges = n.getInEdges().toArray(new Edge[n.getInEdges().size()]);
		Map<Integer, ObjectDefinition> deletedNodes = new HashMap<Integer, ObjectDefinition>();
		//deleting from graph object, all it's edges and all connected nodes
		for (Edge e : outEdges){
			deleteEdge(e.getID());
			deleteEdges(e.getToNode().getInEdges(), e.getToNode().getOutEdges());
			deleteNode(e.getToNode().getID());
			deletedNodes.put(e.getToNode().getID(), entities.get(co.getTypeId()));
		}
		for (Edge e : inEdges){
			deleteEdge(e.getID());
			deleteEdges(e.getFromNode().getInEdges(), e.getFromNode().getOutEdges());
			deleteNode(e.getFromNode().getID());
			deletedNodes.put(e.getFromNode().getID(), entities.get(co.getTypeId()));
		}
		deleteNode(co.getId());
		//forcing graph to load this object from salesForce
		newNode(co.getId(), entity);
		for(Integer id : deletedNodes.keySet())
			newNode(id, deletedNodes.get(id));
	}

	private void deleteEdges(List<Edge> inEdgesList, List<Edge> outEdgesList){
		Edge[] outEdges = outEdgesList.toArray(new Edge[outEdgesList.size()]);
		Edge[] inEdges = inEdgesList.toArray(new Edge[inEdgesList.size()]);
		for (Edge e : outEdges){
			deleteEdge(e.getID());
			deleteEdges(e.getToNode().getInEdges(), e.getToNode().getOutEdges());
			deleteNode(e.getToNode().getID());
		}
		for (Edge e : inEdges){
			deleteEdge(e.getID());
			deleteEdges(e.getFromNode().getInEdges(), e.getFromNode().getOutEdges());
			deleteNode(e.getFromNode().getID());
		}
	}
}
