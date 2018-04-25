package com.ni3.ag.navigator.server.services.impl.salesforce;

import java.util.*;

import com.ni3.ag.navigator.server.cache.SrcIdToFakeIdCacheImpl;
import com.ni3.ag.navigator.server.cache.SrcIdToIdCache;
import com.ni3.ag.navigator.server.dao.ObjectConnectionDAO;
import com.ni3.ag.navigator.server.domain.*;
import com.ni3.ag.navigator.server.services.CISObjectProviderService;
import com.ni3.ag.navigator.server.services.SalesforceConnectionProvider;
import com.ni3.ag.navigator.server.services.SchemaLoaderService;
import com.ni3.ag.navigator.shared.constants.ObjectStatus;
import com.sforce.soap.partner.GetDeletedResult;
import com.sforce.soap.partner.GetUpdatedResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.fault.ExceptionCode;
import com.sforce.soap.partner.fault.UnexpectedErrorFault;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import org.apache.log4j.Logger;

public class SalesForceCISObjectProviderService implements CISObjectProviderService{
	private static final Logger log = Logger.getLogger(SalesForceCISObjectProviderService.class);
	private SrcIdToFakeIdCacheImpl srcIdToIdCacheImpl;
	private ObjectConnectionDAO objectConnectionDAO;
	private SalesforceConnectionProvider salesforceConnectionProvider;
	private SchemaLoaderService schemaLoaderService;
	private Map<ObjectDefinition, Calendar> latestUpdateCovered;
	private Map<ObjectDefinition, Calendar> latestDeleteCovered;
	private Date latestUpdateDone;

	public SalesForceCISObjectProviderService(){
		latestDeleteCovered = new HashMap<ObjectDefinition, Calendar>();
		latestUpdateCovered = new HashMap<ObjectDefinition, Calendar>();
		latestUpdateDone = new Date();
	}

	public void setSchemaLoaderService(SchemaLoaderService schemaLoaderService){
		this.schemaLoaderService = schemaLoaderService;
	}

	public void setSalesforceConnectionProvider(SalesforceConnectionProvider salesforceConnectionProvider){
		this.salesforceConnectionProvider = salesforceConnectionProvider;
	}

	public void setObjectConnectionDAO(ObjectConnectionDAO objectConnectionDAO){
		this.objectConnectionDAO = objectConnectionDAO;
	}

	public void setSrcIdToIdCache(SrcIdToIdCache srcIdToIdCache){
		srcIdToIdCacheImpl = (SrcIdToFakeIdCacheImpl) srcIdToIdCache;
	}

	@Override
	public void init(){
		List<Schema> schemas = schemaLoaderService.getAllSchemas();
		srcIdToIdCacheImpl.init(schemas);
	}

	@Override
	public Node getNode(int id){
		Node n = new Node();
		n.setID(id);
		n.setType(srcIdToIdCacheImpl.getEntityIdById(id));
		n.setStatus(0);
		n.setCreatorUser(0);
		n.setCreatorGroup(0);
		return n;
	}

	@Override
	public List<Edge> getNodeInEdges(int id){
		List<ObjectConnection> connectionTypes = objectConnectionDAO.getConnectionForToType(srcIdToIdCacheImpl.getEntityIdById(id));
		if (connectionTypes.isEmpty())
			return new ArrayList<Edge>();
		List<Edge> result = new ArrayList<Edge>();
		for (ObjectConnection connectionType : connectionTypes){
			String query = generateQuery(connectionType, null, id);
			result.addAll(getConnectionsToNodeByType(connectionType, query));
		}
		return result;
	}

	@Override
	public List<Edge> getNodeOutEdges(int id){
		List<ObjectConnection> connectionTypes = objectConnectionDAO.getConnectionForFromType(srcIdToIdCacheImpl.getEntityIdById(id));
		if (connectionTypes.isEmpty())
			return new ArrayList<Edge>();
		List<Edge> result = new ArrayList<Edge>();
		for (ObjectConnection connectionType : connectionTypes){
			String query = generateQuery(connectionType, id, null);
			result.addAll(getConnectionsToNodeByType(connectionType, query));
		}
		return result;
	}

	private List<Edge> getConnectionsToNodeByType(ObjectConnection connectionType, String query){
		ObjectDefinition fromObject = connectionType.getFromObject();
		log.debug(query);
		List<Edge> edges = new ArrayList<Edge>();
		try{
			final PartnerConnection connection = salesforceConnectionProvider.getConnection();
			QueryResult qr = connection.query(query);
			boolean done = false;
			while (!done){
				for (SObject obj : qr.getRecords()){
					final String toIdStr = obj.getId();
					final int toId = srcIdToIdCacheImpl.getId(toIdStr, connectionType.getToObject());
					final String fromIdStr = (String) obj.getField(fromObject.getName() + "Id");
					final int fromId = srcIdToIdCacheImpl.getId(fromIdStr, connectionType.getFromObject());

					if (toIdStr == null || fromIdStr == null)
						continue;
					final Node from = new Node(fromId);
					final Node to = new Node(toId);

					final Edge newEdge = new Edge();
					newEdge.setFromNode(from);
					newEdge.setToNode(to);
					newEdge.setID(srcIdToIdCacheImpl.getId(fromIdStr + "_" + toIdStr, connectionType.getConnectionObject()));
					newEdge.setType(connectionType.getConnectionObject().getId());
					newEdge.setConnectionType(connectionType.getConnectionType());
					newEdge.setStatus(ObjectStatus.Normal.toInt());
					edges.add(newEdge);
				}
				if (qr.isDone()){
					done = true;
				} else{
					qr = connection.queryMore(qr.getQueryLocator());
				}
			}
		} catch (UnexpectedErrorFault e){
			if (e.getExceptionCode() == ExceptionCode.INVALID_SESSION_ID){
				salesforceConnectionProvider.recreateConnection();
				getConnectionsToNodeByType(connectionType, query);
			}
		} catch (ConnectionException e){
			log.error(e);
		}
		return edges;
	}

	private String generateQuery(ObjectConnection connectionType, Integer fromId, Integer toId){
		ObjectDefinition fromObject = connectionType.getFromObject();
		ObjectDefinition toObject = connectionType.getToObject();
		final StringBuilder query = new StringBuilder("SELECT Id, ");
		query.append(fromObject.getName()).append("Id FROM ").append(toObject.getName());
		query.append(" WHERE ");
		if (toId != null)
			query.append("Id = '").append(srcIdToIdCacheImpl.getSrcId(toId)).append("'").append(" and ");
		else
			query.append("Id != null and ");
		if (fromId != null)
			query.append(fromObject.getName()).append("Id = '").append(srcIdToIdCacheImpl.getSrcId(fromId)).append("'");
		else
			query.append(fromObject.getName()).append("Id != null");
		return query.toString();
	}

	@Override
	public Node getFromNode(int id){
		String srcId = srcIdToIdCacheImpl.getSrcId(id);
		String[] sObjectIds = srcId.split("_");
		int fromId = srcIdToIdCacheImpl.getId(sObjectIds[0], null);
		return getNode(fromId);
	}

	@Override
	public Node getToNode(int id){
		String srcId = srcIdToIdCacheImpl.getSrcId(id);
		String[] sObjectIds = srcId.split("_");
		int toId = srcIdToIdCacheImpl.getId(sObjectIds[1], null);
		return getNode(toId);
	}

	@Override
	public Edge getEdge(int id){
		String srcId = srcIdToIdCacheImpl.getSrcId(id);
		String[] sObjectIds = srcId.split("_");
		int fromId = srcIdToIdCacheImpl.getId(sObjectIds[0], null);
		int toId = srcIdToIdCacheImpl.getId(sObjectIds[1], null);
		int fromType = srcIdToIdCacheImpl.getEntityIdById(fromId);
		int toType = srcIdToIdCacheImpl.getEntityIdById(toId);
		ObjectConnection oc = objectConnectionDAO.getConnectionForFromToTypes(fromType, toType, srcIdToIdCacheImpl.getEntityIdById(id));
		Edge e = new Edge();
		e.setID(id);
		e.setType(srcIdToIdCacheImpl.getEntityIdById(id));
		e.setStatus(ObjectStatus.Normal.toInt());
		e.setConnectionType(oc.getConnectionType());
		e.setStrength(1);
		e.setInPath(1);
		return e;
	}

	@Override
	public List<Integer> getEdgeList(List<Integer> fromIds, List<Integer> toIds, List<Integer> edgeIds, int limit){
		return null;
	}

	@Override
	public List<Integer> getEdgeListByFavorite(int favoriteId){
		return null;
	}

	@Override
	public List<Integer> getEdgeList(List<Integer> nodeIds, List<Integer> edgeIds, int limit){
		return null;
	}

	@Override
	public List<Integer> getConnectedNodesForNode(Integer id){
		return null;
	}

	@Override
	public Map<Integer, Integer> getEdgesWithTypesForNode(int objectId){
		log.debug("get edges with types for node " + objectId);
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		List<Edge> edges = getNodeInEdges(objectId);
		for (Edge e : edges)
			result.put(e.getID(), e.getType());
		edges = getNodeOutEdges(objectId);
		for (Edge e : edges)
			result.put(e.getID(), e.getType());
		log.debug("Edge size: " + edges.size());
		return result;
	}

	@Override
	public void fillLastModified(Date lastUpdateTime, List<ObjectDefinition> types){
	}

	@Override
	//TODO performance issue
	public List<CisObject> getUpdatedObjects(Date lastUpdateTime, List<ObjectDefinition> types){
		Calendar latestUpdateDoneCalendar = new GregorianCalendar();
		latestUpdateDoneCalendar.setTime(latestUpdateDone);
		latestUpdateDoneCalendar.add(Calendar.MINUTE, 10);
		if (latestUpdateDoneCalendar.getTime().after(new Date())){
			log.warn("Too often replication for salesforce, next avalible in " + latestUpdateDoneCalendar.getTime());
			return Collections.emptyList();
		}
		latestUpdateDone = new Date();
		PartnerConnection connection = salesforceConnectionProvider.getConnection();

		List<CisObject> result = new ArrayList<CisObject>();
		getUpdatedObject(result, connection, types);
		getDeletedObjects(result, connection, types);
		return result;
	}

	@Override
	public Collection<? extends Integer> getNodeIds(ObjectDefinition od){
		PartnerConnection connection = salesforceConnectionProvider.getConnection();
		List<Integer> results = new ArrayList<Integer>();
		try{
			final String sql = "select id from " + od.getName();
			log.debug(sql);
			QueryResult queryResult = connection.query(sql);
			boolean done = false;
			while (!done){
				final SObject[] records = queryResult.getRecords();
				for (SObject record : records){
					final int id = srcIdToIdCacheImpl.getId(record.getId(), od);
					if (id == -1)
						continue;
					results.add(id);
				}

				if (queryResult.isDone()){
					done = true;
				} else{
					queryResult = connection.queryMore(queryResult.getQueryLocator());
				}
			}
		} catch (UnexpectedErrorFault e){
			if (e.getExceptionCode() == ExceptionCode.INVALID_SESSION_ID){
				salesforceConnectionProvider.recreateConnection();
				getNodeIds(od);
			}
		} catch (ConnectionException e){
			log.error(e);
		}
		return results;
	}

	private void getDeletedObjects(List<CisObject> result, PartnerConnection connection, List<ObjectDefinition> types){
		Calendar end = new GregorianCalendar(TimeZone.getTimeZone("UTC/GMT"));
		end.setTime(new Date());

		for (ObjectDefinition od : types){
			if (od.isEdge())
				continue;
			Calendar start = getStartTime(od, latestDeleteCovered);
			log.debug("Start time: " + start.getTime());
			log.debug("End date: " + end.getTime());
			try{
				GetDeletedResult deletedResult = connection.getDeleted(od.getName(), start, end);
//				DeletedRecord[] records = deletedResult.getDeletedRecords();
//				for(DeletedRecord dr : records){
//					String sid = dr.getId();
//					CisObject co = makeCisObject(sid, od, ObjectStatus.Deleted, dr.getDeletedDate().getTime());
//					if (co == null){
//						log.error("Cannot create cis_object to id: " + sid);
//						continue;
//					}
//					log.debug("Deleted object: " + sid + " -> " + co.getId());
//					result.add(co);
//				}
				Calendar latest = deletedResult.getLatestDateCovered();
				log.debug("Latest covered for " + od.getName() + ": " + latest.getTime());
				latestDeleteCovered.put(od, latest);
			} catch (UnexpectedErrorFault e){
				if (e.getExceptionCode() == ExceptionCode.INVALID_SESSION_ID){
					salesforceConnectionProvider.recreateConnection();
					getDeletedObjects(result, connection, types);
				}
			} catch (ConnectionException e){
				log.error("Error get deleted objects for: " + od.getName());
			}
		}
	}

	private void getUpdatedObject(List<CisObject> result, PartnerConnection connection, List<ObjectDefinition> types){
		Calendar end = new GregorianCalendar(TimeZone.getTimeZone("UTC/GMT"));
		end.setTime(new Date());

		for (ObjectDefinition od : types){
			if (od.isEdge())
				continue;

			Calendar start = getStartTime(od, latestUpdateCovered);

			try{
				log.debug("Start time: " + start.getTime());
				log.debug("End date: " + end.getTime());
				GetUpdatedResult updatedResult = connection.getUpdated(od.getName(), start, end);
				String[] updatedIds = updatedResult.getIds();
				log.debug("Updated ids for object: " + od.getName());
				log.debug(Arrays.toString(updatedIds));
				for (String sId : updatedIds){
					CisObject co = makeCisObject(sId, od, ObjectStatus.Normal, new Date());
					if (co == null){
						log.error("Cannot create cis_object to id: " + sId);
						continue;
					}
					log.debug("Updated object: " + sId + " -> " + co.getId());
					result.add(co);
				}
				Calendar latest = updatedResult.getLatestDateCovered();
				log.debug("Latest covered for " + od.getName() + ": " + latest.getTime());
				latestUpdateCovered.put(od, latest);
			} catch (UnexpectedErrorFault e){
				if (e.getExceptionCode() == ExceptionCode.INVALID_SESSION_ID){
					salesforceConnectionProvider.recreateConnection();
					getUpdatedObject(result, salesforceConnectionProvider.getConnection(), types);
				}
			} catch (ConnectionException e){
				log.error("Error get updated for " + od.getName(), e);
			}
		}
	}

	private Calendar getStartTime(ObjectDefinition od, Map<ObjectDefinition, Calendar> dateMap){
		Calendar start;
		if (!dateMap.containsKey(od)){
			start = new GregorianCalendar(TimeZone.getTimeZone("UTC/GMT"));
			start.setTime(new Date());
			dateMap.put(od, start);
		} else
			start = dateMap.get(od);
		return start;
	}

	private CisObject makeCisObject(String srcId, ObjectDefinition type, ObjectStatus status, Date lastModified){
		int id = srcIdToIdCacheImpl.getId(srcId, type);
		CisObject co = new CisObject();
		co.setId(id);
		co.setTypeId(type.getId());
		co.setStatus(status);
		co.setLastModified(lastModified);
		return co;
	}
}
