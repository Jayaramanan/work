/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gateway;

import java.util.*;
import javax.swing.*;

import com.ni3.ag.navigator.client.domain.*;
import com.ni3.ag.navigator.client.domain.Attribute.EDynamicAttributeScope;
import com.ni3.ag.navigator.client.domain.query.Query;
import com.ni3.ag.navigator.client.gateway.impl.HttpDynamicAttributesGatewayImpl;
import com.ni3.ag.navigator.client.gateway.impl.HttpGraphGatewayImpl;
import com.ni3.ag.navigator.client.gateway.impl.HttpSearchGatewayImpl;
import com.ni3.ag.navigator.client.gui.Ni3;
import com.ni3.ag.navigator.client.gui.graph.GraphObject;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.model.GraphCollection;
import com.ni3.ag.navigator.client.model.Ni3Document;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import com.ni3.ag.navigator.client.util.Utility;
import com.ni3.ag.navigator.shared.domain.DynamicAttributeDescriptor;
import org.apache.log4j.Logger;

public class DataProvider{
	private static final Logger log = Logger.getLogger(DataProvider.class);
	private int maximumNodeCount;
	private int maxPathLength;
	private int pathLengthOverrun;
	private int minKeywordSearchLen;

	public Schema schema;
	public Map<Integer, DBObject> cache;
	public String status;
	private DataFilter dataFilter;
	public Palette paletteInUse;

	public DataProvider(int SchemaID, int LanguageID){
		paletteInUse = new Palette(1);
		Utility.PaletteInUse = paletteInUse;

		maximumNodeCount = Integer.valueOf(UserSettings.getProperty("Applet", "MaximumNodeNumber", "1500"));
		maxPathLength = Integer.valueOf(UserSettings.getProperty("Applet", "MaxPathLength", "10"));
		pathLengthOverrun = Integer.valueOf(UserSettings.getProperty("Applet", "PathLengthOverrun", "1"));
		minKeywordSearchLen = Integer.valueOf(UserSettings.getProperty("Applet", "MinKeywordSearchLen", "3"));

		dataFilter = new DataFilter();
		cache = new HashMap<Integer, DBObject>();
		schema = new Schema(SystemGlobals.GroupID, SchemaID, LanguageID);
	}

	public DataFilter getDataFilter(){
		return dataFilter;
	}

	public void setDataFilter(DataFilter dataFilter){
		this.dataFilter = dataFilter;
	}

	public DBObject get(int ID){
		return cache.get(ID);
	}

	public int getMaximumNodeCount(){
		return maximumNodeCount;
	}

	public int getMaxPathLength(){
		return maxPathLength;
	}

	public void setMaxPathLength(int maxPathLength){
		this.maxPathLength = maxPathLength;
	}

	public int getPathLengthOverrun(){
		return pathLengthOverrun;
	}

	public void setPathLengthOverrun(int pathLengthOverrun){
		this.pathLengthOverrun = pathLengthOverrun;
	}

	public int getMinKeywordSearchLen(){
		return minKeywordSearchLen;
	}

	public List<DBObject> search(String terms, Ni3Document doc){
		List<DBObject> result = new ArrayList<DBObject>();
		try{
			DataFilter filter = new DataFilter(doc.SYSGroupPrefilter);
			filter.addFilter(dataFilter);
			SearchGateway searchGateway = new HttpSearchGatewayImpl();
			log.debug("Run search");
			List<com.ni3.ag.navigator.shared.domain.DBObject> searchResult = searchGateway.simpleSearch(schema.ID, terms,
					filter);
			log.debug("Search results: " + searchResult.size());
			for (com.ni3.ag.navigator.shared.domain.DBObject dbo : searchResult){
				DBObject obj = new DBObject(schema.getEntity(dbo.getEntityId()));
				obj.setData(dbo);

				if (doc.SYSGroupPrefilter != null && doc.SYSGroupPrefilter.isObjectFilteredOut(obj))
					continue;

				if (dataFilter != null && dataFilter.isObjectFilteredOut(obj))
					continue;

				DBObject cached = get(obj.getId());
				if (cached != null)
					obj = cached;

				result.add(obj);
				cache.put(obj.getId(), obj);
			}
		} catch (java.lang.OutOfMemoryError error){
			Ni3.showClientError(Thread.currentThread(), error);
		}
		return result;
	}

	public List<DBObject> combineSearchNodes(Query query, Ni3Document doc){
		List<DBObject> res = new ArrayList<DBObject>();

		try{
			DataFilter filter = new DataFilter(doc.SYSGroupPrefilter);
			filter.addFilter(dataFilter);

			SearchGateway searchGateway = new HttpSearchGatewayImpl();
			List<com.ni3.ag.navigator.shared.domain.DBObject> searchResult = searchGateway.advancedSearch(schema.ID, query,
					filter);

			for (com.ni3.ag.navigator.shared.domain.DBObject domainObject : searchResult){
				int ID = domainObject.getId();
				DBObject obj = get(ID);

				if (obj == null)
					obj = new DBObject(schema.getEntity(domainObject.getEntityId()));

				obj.setData(domainObject);

				if (!obj.getEntity().CanRead)// can even be?
					continue;

				if (doc != null && doc.SYSGroupPrefilter != null && doc.SYSGroupPrefilter.isObjectFilteredOut(obj))
					continue;

				if (dataFilter != null && dataFilter.isObjectFilteredOut(obj))
					continue;
				res.add(obj);
				cache.put(obj.getId(), obj);
			}
			status = res.size() + " found";
		} catch (java.lang.OutOfMemoryError error){
			Runtime.getRuntime().gc();
			JOptionPane.showMessageDialog(null, "Java VM is out of memory. Ni3 will terminate.");
			log.error("*******************************************************");
			log.error("****Java VM is out of memory. Ni3 will terminate.******");
			log.error("*******************************************************");
			Runtime.getRuntime().exit(1);
		}

		return res;
	}

	// TODO loads only ids then requests data -> should request full data
	public List<Integer> combineSearchNetwork(Query query, Ni3Document doc){
		List<Integer> res = new ArrayList<Integer>();
		DataFilter filter = new DataFilter(doc.SYSGroupPrefilter);
		filter.addFilter(dataFilter);

		try{
			SearchGateway searchGateway = new HttpSearchGatewayImpl();
			List<com.ni3.ag.navigator.shared.domain.DBObject> searchResult = searchGateway.advancedSearch(schema.ID, query,
					filter);

			for (com.ni3.ag.navigator.shared.domain.DBObject domainObject : searchResult){
				res.add(domainObject.getId());
			}
			status = res.size() + " found";
		} catch (java.lang.OutOfMemoryError error){
			Runtime.getRuntime().gc();

			JOptionPane.showMessageDialog(null, "Java VM is out of memory. Ni3 will terminate.");
			System.out
					.println("*******************************************************\nJava VM is out of memory. Ni3 will terminate.*******************************************************\n");
			Runtime.getRuntime().exit(1);
		}

		return res;
	}

	public synchronized List<DBObject> prepareSubgraph(GraphCollection graph, boolean mark){
		List<DBObject> ret = getSubgraphData(graph);

		if (mark)
			graph.MarkDegree();

		return ret;
	}

	public synchronized List<DBObject> getSubgraphData(GraphCollection graph){
		List<DBObject> res = new ArrayList<DBObject>();
		Map<Integer, Collection<Integer>> missing = makeMissingObjectMap(graph, res);

		if (!missing.isEmpty()){
			SearchGateway searchGateway = new HttpSearchGatewayImpl();
			List<com.ni3.ag.navigator.shared.domain.DBObject> searchResult = searchGateway.getList(schema.ID, missing);

			for (com.ni3.ag.navigator.shared.domain.DBObject domainObject : searchResult){
				missing.get(domainObject.getEntityId()).remove((Integer)domainObject.getId());

				DBObject obj = new DBObject(schema.getEntity(domainObject.getEntityId()));
				obj.setData(domainObject);
				cache.put(obj.getId(), obj);
				res.add(obj);

				GraphObject go = graph.findGraphObject(obj.getId());
				go.Obj = obj;
			}
		}

		List<Node> toDelete = new ArrayList<Node>();

		for (Node nd : graph.getNodes()){
			if (nd.Obj == null){
				toDelete.add(nd);
			}
		}

		for (Node nd : toDelete)
			graph.simpleRemoveNode(nd);

		refreshDynamicAttributes(graph);

		return res;
	}

	private Map<Integer, Collection<Integer>> makeMissingObjectMap(GraphCollection graph, List<DBObject> res){
		Map<Integer, Collection<Integer>> missing = new HashMap<Integer, Collection<Integer>>();
		for (GraphObject n : graph.getObjects()){
			if (n.Obj != null){
				res.add(n.Obj);
				continue;
			}

			n.Obj = cache.get(n.ID);
			if (n.Obj == null){
				if (!missing.containsKey(n.Type))
					missing.put(n.Type, new HashSet<Integer>());
				Collection<Integer> list = missing.get(n.Type);
				list.add(n.ID);
			} else
				res.add(n.Obj);
		}
		return missing;
	}

	public DBObject getObject(int ID, boolean putToCache, boolean withDeleted){
		SearchGateway searchGateway = new HttpSearchGatewayImpl();
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(ID);
		List<com.ni3.ag.navigator.shared.domain.DBObject> searchResult = searchGateway.searchUnknown(schema.ID, ids,
				withDeleted);
		if (searchResult.isEmpty())
			return null;
		com.ni3.ag.navigator.shared.domain.DBObject domainObject = searchResult.get(0);
		final Entity entity = schema.getEntity(domainObject.getEntityId());
		DBObject obj = null;
		if (entity != null){
			obj = new DBObject(entity);
			obj.setData(domainObject);
			if (putToCache){
				cache.put(obj.getId(), obj);
			}
		} else{
			log.warn("Cannot reload object: " + ID + ", entity is not accessible: " + domainObject.getEntityId());
		}
		return obj;
	}

	public void getFavoritesContextData(int favoriteID, GraphObject o){
		if (favoriteID <= 0)
			return;

		Context c = o.Obj.getEntity().getContext("Favorites");
		if (c != null){
			List<DBObject> set = new ArrayList<DBObject>();
			set.add(o.Obj);
			getObjectContext(set, c, Integer.toString(favoriteID));
		}
	}

	public void getObjectContext(List<DBObject> set, Context c, String key){
		for (DBObject o : set){
			for (final Attribute a : c.getAttributes()){
				o.setValue(a.ID, null);
			}
		}

		SearchGateway searchGateway = new HttpSearchGatewayImpl();
		List<Integer> ids = new ArrayList<Integer>();
		for (DBObject dbo : set)
			ids.add(dbo.getId());
		List<com.ni3.ag.navigator.shared.domain.DBObject> searchResult = searchGateway.getListContext(schema.ID, c.ent.ID,
				ids, c.ID, key);
		for (com.ni3.ag.navigator.shared.domain.DBObject domainObject : searchResult){
			int ID = domainObject.getId();
			DBObject obj = new DBObject(ID);
			if (set.contains(obj)){
				obj = set.get(set.indexOf(obj)); // getting the real object from the list
				Map<Integer, String> data = domainObject.getData();
				for (Attribute a : c.getAttributes()){
					if (data.containsKey(a.ID))
						obj.assignValue(data.get(a.ID), a);
					else
						obj.setValue(a.ID, null);
				}
			}
		}
	}

	public void reloadObject(DBObject obj){
		SearchGateway searchGateway = new HttpSearchGatewayImpl();
		Map<Integer, Collection<Integer>> missing = new HashMap<Integer, Collection<Integer>>();
		missing.put(obj.getEntity().ID, new ArrayList<Integer>());
		missing.get(obj.getEntity().ID).add(obj.getId());
		List<com.ni3.ag.navigator.shared.domain.DBObject> searchResult = searchGateway.getList(obj.getEntity().getSchema().ID,
				missing);
		if (searchResult.isEmpty()){
			log.error("Error get object with id: " + obj.getId());
			return;
		}
		com.ni3.ag.navigator.shared.domain.DBObject domainObject = searchResult.get(0);
		obj.setData(domainObject);
		cache.put(obj.getId(), obj);
	}

	public void reloadNode(Node node, GraphCollection graph){
		GraphGateway graphGateway = new HttpGraphGatewayImpl();
		// TODO why sysFilter is sent from client?
		Node newNode = graphGateway.reloadNode(node.ID, schema.ID, dataFilter);
		newNode = graph.addOrUpdateNode(newNode);

		reloadObject(newNode.Obj);
	}

	public void clearContext(){
		for (Object o : cache.values()){
			final DBObject dbObject = (DBObject) o;
			for (final Context c : dbObject.getEntity().context){
				for (final Attribute a : c.getAttributes()){
					dbObject.setValue(a.ID, null);
				}
			}
		}
	}

	public void loadDynamicAttributes(GraphCollection subgraph){
		Map<Integer, DynamicAttributeDescriptor> dynamicAttributeDescriptors = getDynamicDatabaseAttributeDescriptors(subgraph);
		DynamicAttributesGateway dynamicAttributesGateway = new HttpDynamicAttributesGatewayImpl();
		List<com.ni3.ag.navigator.shared.domain.DBObject> serverResults = dynamicAttributesGateway
				.getDynamicAttributeValues(dynamicAttributeDescriptors);
		for (com.ni3.ag.navigator.shared.domain.DBObject domainObject : serverResults){
			Map<Integer, String> data = domainObject.getData();
			for (Integer attrId : data.keySet()){
				Node node = subgraph.findNode(domainObject.getId());
				double val = Double.valueOf(data.get(attrId));
				DynamicAttributeDescriptor descriptor = dynamicAttributeDescriptors.get(attrId);
				Attribute a = (Attribute) descriptor.getAttribute();
				descriptor.getIds().remove(new Integer(node.ID));
				node.Obj.setValue(a.ID, val);
			}
		}

		// if descriptors contains any ids - we don't got anything for them from server (null values)
		for (DynamicAttributeDescriptor descriptor : dynamicAttributeDescriptors.values()){
			for (Integer id : descriptor.getIds()){
				Attribute a = (Attribute) descriptor.getAttribute();
				Node node = subgraph.findNode(id);
				node.Obj.setValue(a.ID, null);
			}
		}
	}

	private Map<Integer, DynamicAttributeDescriptor> getDynamicDatabaseAttributeDescriptors(GraphCollection subgraph){
		Map<Integer, DynamicAttributeDescriptor> result = new HashMap<Integer, DynamicAttributeDescriptor>();

		for (Entity ent : schema.definitions){
			if (!ent.hasDynamicAttributes())
				continue;

			List<Integer> ids = new ArrayList<Integer>();
			for (Node n : subgraph.getNodes())
				if (n.Obj != null && n.Obj.getEntity().ID == ent.ID)
					ids.add(n.ID);

			if (ids.isEmpty())
				continue;

			for (Attribute a : ent.getAllAttributes()){
				if (!a.isDynamic())
					continue;

				if (a.getDynamicScope() != EDynamicAttributeScope.Database)
					continue;

				DynamicAttributeDescriptor descriptor = new DynamicAttributeDescriptor();
				descriptor.setFromEntity(a.getDynamicFromEntity().ID);
				descriptor.setFromAttribute(a.getDynamicFromAttribute().ID);
				descriptor.setAttribute(a);
				descriptor.setOperation(a.getDynamicOperation().toString());
				descriptor.setIds(ids);
				descriptor.setSchema(schema.ID);
				result.put(descriptor.getFakeAttributeId(), descriptor);
			}
		}
		return result;
	}

	public void refreshDynamicAttributes(GraphCollection subgraph){
		loadDynamicAttributes(subgraph);
		subgraph.recalculateDynamicValues();
	}

	public List<GraphObject> findPath(int fromNodeId, int toNodeId, GraphCollection graph){
		GraphGateway graphGateway = new HttpGraphGatewayImpl();
		List<GraphObject> pathObjects = graphGateway.findPathObjects(fromNodeId, toNodeId, schema.ID, dataFilter,
				maxPathLength, pathLengthOverrun);
		List<GraphObject> resObjects = graph.addResultToGraph(pathObjects);

		prepareSubgraph(graph, false);
		return resObjects;
	}
}
