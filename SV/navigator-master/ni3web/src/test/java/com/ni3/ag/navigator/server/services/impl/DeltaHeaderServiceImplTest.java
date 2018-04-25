package com.ni3.ag.navigator.server.services.impl;

import java.lang.reflect.Method;
import java.util.*;

import com.ni3.ag.navigator.server.cache.GraphNi3Engine;
import com.ni3.ag.navigator.server.dao.FavoriteDAO;
import com.ni3.ag.navigator.server.dao.FavoritesFolderDAO;
import com.ni3.ag.navigator.server.dao.ThematicMapDAO;
import com.ni3.ag.navigator.server.domain.*;
import com.ni3.ag.navigator.server.services.GraphEngineFactory;
import com.ni3.ag.navigator.shared.domain.DeltaType;
import com.ni3.ag.navigator.shared.domain.ThematicMap;
import com.ni3.ag.navigator.shared.domain.User;
import junit.framework.TestCase;

public class DeltaHeaderServiceImplTest extends TestCase{
	private List<Long> invalidDeltaIds = new ArrayList<Long>();

	@SuppressWarnings("unchecked")
	public void testGetDeltasToMarkAsProcessed() throws Exception{
		List<DeltaHeader> generated = generateDeltaHeaders();
		DeltaHeaderServiceImpl impl = new DeltaHeaderServiceImpl();
		impl.setFavoriteDAO(new TestFavoriteDAO());
		impl.setFavoritesFolderDAO(new TestFavoritesFolderDAO());
		impl.setThematicMapDAO(new TestThematicMapDAO());
		impl.setGraphEngineFactory(new TestEngineFactory());
		Method method = DeltaHeaderServiceImpl.class.getDeclaredMethod("getDeltasToMark", List.class);
		method.setAccessible(true);
		List<DeltaHeader> result = (List<DeltaHeader>) method.invoke(impl, generated);
		validateResult(result);
	}

	private void validateResult(List<DeltaHeader> result){
		for (DeltaHeader dh : result){
			assertTrue(invalidDeltaIds.contains(dh.getId()));
			invalidDeltaIds.remove(dh.getId());
		}
		assertTrue(invalidDeltaIds.isEmpty());
	}

	private List<DeltaHeader> generateDeltaHeaders(){
		List<DeltaHeader> result = new ArrayList<DeltaHeader>();
		generateFavoritesDeltas(result);
		generateFoldersDeltas(result);
		generateThematicMapsDeltas(result);
		generateObjectsDeltas(result);
		return result;
	}

	private void generateObjectsDeltas(List<DeltaHeader> result){
		generateEdgeDeltas(result);
		generateNodeDeltas(result);
	}

	private void generateEdgeDeltas(List<DeltaHeader> result){
		result.add(makeCreateEdgeDelta(12));

		result.add(makeCreateEdgeDelta(11));
		result.add(makeUpdateEdgeDelta(11));

		result.add(makeCreateEdgeDelta(14));
		result.add(makeUpdateEdgeDelta(14));// 14 invalid
		invalidDeltaIds.add(5014L);
		invalidDeltaIds.add(5114L);

		result.add(makeCreateEdgeDelta(13));
		result.add(makeUpdateEdgeDelta(13));
		result.add(makeDeleteEdgeDelta(13));
		invalidDeltaIds.add(5013L);
		invalidDeltaIds.add(5113L);
		invalidDeltaIds.add(5213L);

		result.add(makeUpdateEdgeDelta(15));
		result.add(makeDeleteEdgeDelta(15));
		invalidDeltaIds.add(5115L);
	}

	private DeltaHeader makeDeleteEdgeDelta(int i){
		Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
		params.put(DeltaParamIdentifier.DeleteObjectObjectId, new DeltaParam(DeltaParamIdentifier.DeleteObjectObjectId, ""
				+ i));
		params.put(DeltaParamIdentifier.DeleteObjectSchemaId, new DeltaParam(DeltaParamIdentifier.DeleteObjectSchemaId,
				"" + 1));
		DeltaHeader dh = new DeltaHeader(DeltaType.OBJECT_DELETE, new User(), params);
		dh.setId(5200 + i);
		return dh;
	}

	private DeltaHeader makeUpdateEdgeDelta(int i){
		Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
		params.put(DeltaParamIdentifier.UpdateEdgeObjectId, new DeltaParam(DeltaParamIdentifier.UpdateEdgeObjectId, "" + i));
		params.put(DeltaParamIdentifier.UpdateEdgeSchemaId, new DeltaParam(DeltaParamIdentifier.UpdateEdgeSchemaId, "" + 1));
		DeltaHeader dh = new DeltaHeader(DeltaType.EDGE_UPDATE, new User(), params);
		dh.setId(5100 + i);
		return dh;
	}

	private DeltaHeader makeCreateEdgeDelta(int i){
		Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
		params.put(DeltaParamIdentifier.CreateEdgeNewId, new DeltaParam(DeltaParamIdentifier.CreateEdgeNewId, "" + i));
		params.put(DeltaParamIdentifier.CreateEdgeSchemaId, new DeltaParam(DeltaParamIdentifier.CreateEdgeSchemaId, "" + 1));
		DeltaHeader dh = new DeltaHeader(DeltaType.EDGE_CREATE, new User(), params);
		dh.setId(5000 + i);
		return dh;
	}

	private void generateNodeDeltas(List<DeltaHeader> result){
		result.add(makeCreateNodeDelta(2));

		result.add(makeCreateNodeDelta(1));
		result.add(makeUpdateNodeDelta(1));

		result.add(makeCreateNodeDelta(4));
		result.add(makeUpdateNodeDelta(4));// 4 invalid
		invalidDeltaIds.add(4004L);
		invalidDeltaIds.add(4104L);

		result.add(makeCreateNodeDelta(3));
		result.add(makeUpdateNodeDelta(3));
		result.add(makeDeleteNodeDelta(3));
		invalidDeltaIds.add(4003L);
		invalidDeltaIds.add(4103L);
		invalidDeltaIds.add(4203L);

		result.add(makeUpdateNodeDelta(5));
		result.add(makeDeleteNodeDelta(5));
		invalidDeltaIds.add(4105L);
	}

	private DeltaHeader makeDeleteNodeDelta(int i){
		Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
		params.put(DeltaParamIdentifier.DeleteObjectObjectId, new DeltaParam(DeltaParamIdentifier.DeleteObjectObjectId, ""
				+ i));
		params.put(DeltaParamIdentifier.DeleteObjectSchemaId, new DeltaParam(DeltaParamIdentifier.DeleteObjectSchemaId,
				"" + 1));
		DeltaHeader dh = new DeltaHeader(DeltaType.OBJECT_DELETE, new User(), params);
		dh.setId(4200 + i);
		return dh;
	}

	private DeltaHeader makeUpdateNodeDelta(int i){
		Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
		params.put(DeltaParamIdentifier.UpdateNodeObjectId, new DeltaParam(DeltaParamIdentifier.UpdateNodeObjectId, "" + i));
		params.put(DeltaParamIdentifier.UpdateNodeSchemaId, new DeltaParam(DeltaParamIdentifier.UpdateNodeSchemaId, "" + 1));
		DeltaHeader dh = new DeltaHeader(DeltaType.NODE_UPDATE, new User(), params);
		dh.setId(4100 + i);
		return dh;
	}

	private DeltaHeader makeCreateNodeDelta(int i){
		Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
		params.put(DeltaParamIdentifier.CreateNodeNewId, new DeltaParam(DeltaParamIdentifier.CreateNodeNewId, "" + i));
		params.put(DeltaParamIdentifier.CreateNodeSchemaId, new DeltaParam(DeltaParamIdentifier.CreateNodeSchemaId, "" + 1));
		DeltaHeader dh = new DeltaHeader(DeltaType.NODE_CREATE, new User(), params);
		dh.setId(4000 + i);
		return dh;
	}

	private void generateThematicMapsDeltas(List<DeltaHeader> result){
		result.add(makeCreateThematicMapDelta(5));

		result.add(makeCreateThematicMapDelta(4));
		result.add(makeUpdateThematicMapDelta(4));

		result.add(makeCreateThematicMapDelta(2));
		result.add(makeUpdateThematicMapDelta(2));
		invalidDeltaIds.add(3002L);
		invalidDeltaIds.add(3102L);

		result.add(makeCreateThematicMapDelta(1));
		result.add(makeUpdateThematicMapDelta(1));
		result.add(makeDeleteThematicMapDelta(1));
		invalidDeltaIds.add(3001L);
		invalidDeltaIds.add(3101L);

		result.add(makeUpdateThematicMapDelta(3));
		result.add(makeDeleteThematicMapDelta(3));
		invalidDeltaIds.add(3103L);
	}

	private DeltaHeader makeDeleteThematicMapDelta(int i){
		Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
		params.put(DeltaParamIdentifier.DeleteGeoAnalyticsId, new DeltaParam(DeltaParamIdentifier.DeleteGeoAnalyticsId, ""
				+ i));
		DeltaHeader dh = new DeltaHeader(DeltaType.GEO_ANALYTICS_DELETE, new User(), params);
		dh.setId(3200 + i);
		return dh;
	}

	private DeltaHeader makeUpdateThematicMapDelta(int i){
		Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
		params.put(DeltaParamIdentifier.SaveGeoAnalyticsId, new DeltaParam(DeltaParamIdentifier.SaveGeoAnalyticsId, "" + i));
		DeltaHeader dh = new DeltaHeader(DeltaType.GEO_ANALYTICS_SAVE, new User(), params);
		dh.setId(3100 + i);
		return dh;
	}

	private DeltaHeader makeCreateThematicMapDelta(int i){
		Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
		params.put(DeltaParamIdentifier.SaveGeoAnalyticsId, new DeltaParam(DeltaParamIdentifier.SaveGeoAnalyticsId, "" + i));
		DeltaHeader dh = new DeltaHeader(DeltaType.GEO_ANALYTICS_SAVE, new User(), params);
		dh.setId(3000 + i);
		return dh;
	}

	private void generateFoldersDeltas(List<DeltaHeader> result){
		result.add(makeCreateFolderDelta(2));

		result.add(makeCreateFolderDelta(1));
		result.add(makeUpdateFolderDelta(1));

		result.add(makeCreateFolderDelta(4));
		result.add(makeUpdateFolderDelta(4));
		invalidDeltaIds.add(2004L);
		invalidDeltaIds.add(2104L);

		result.add(makeCreateFolderDelta(3));
		result.add(makeUpdateFolderDelta(3));
		result.add(makeDeleteFolderDelta(3));
		invalidDeltaIds.add(2003L);
		invalidDeltaIds.add(2103L);
		invalidDeltaIds.add(2203L);

		result.add(makeUpdateFolderDelta(5));
		result.add(makeDeleteFolderDelta(5));
		invalidDeltaIds.add(2105L);
	}

	private DeltaHeader makeDeleteFolderDelta(int i){
		Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
		params.put(DeltaParamIdentifier.DeleteFavoriteFolderObjId, new DeltaParam(
				DeltaParamIdentifier.DeleteFavoriteFolderObjId, "" + i));
		DeltaHeader dh = new DeltaHeader(DeltaType.FAVORITE_FOLDER_DELETE, new User(), params);
		dh.setId(2200 + i);
		return dh;
	}

	private DeltaHeader makeUpdateFolderDelta(int i){
		Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
		params.put(DeltaParamIdentifier.UpdateFavoriteFolderFolderId, new DeltaParam(
				DeltaParamIdentifier.UpdateFavoriteFolderFolderId, "" + i));
		DeltaHeader dh = new DeltaHeader(DeltaType.FAVORITE_FOLDER_UPDATE, new User(), params);
		dh.setId(2100 + i);
		return dh;
	}

	private DeltaHeader makeCreateFolderDelta(int i){
		Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
		params.put(DeltaParamIdentifier.CreateFavoriteFolderNewId, new DeltaParam(
				DeltaParamIdentifier.CreateFavoriteFolderNewId, "" + i));
		DeltaHeader dh = new DeltaHeader(DeltaType.FAVORITE_FOLDER_CREATE, new User(), params);
		dh.setId(2000 + i);
		return dh;
	}

	private void generateFavoritesDeltas(List<DeltaHeader> result){
		result.add(makeCreateFavoriteDelta(1));

		result.add(makeCreateFavoriteDelta(2));
		result.add(makeUpdateFavoriteDelta(2));

		result.add(makeCreateFavoriteDelta(3));
		result.add(makeUpdateFavoriteDelta(3));
		invalidDeltaIds.add(1003L);
		invalidDeltaIds.add(1103L);

		result.add(makeCreateFavoriteDelta(4));
		result.add(makeUpdateFavoriteDelta(4));
		result.add(makeDeleteFavoriteDelta(4));
		invalidDeltaIds.add(1004L);
		invalidDeltaIds.add(1104L);
		invalidDeltaIds.add(1204L);

		result.add(makeUpdateFavoriteDelta(5));
		result.add(makeDeleteFavoriteDelta(5));
		invalidDeltaIds.add(1105L);
	}

	private DeltaHeader makeDeleteFavoriteDelta(int i){
		Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
		params.put(DeltaParamIdentifier.DeleteFavoriteId, new DeltaParam(DeltaParamIdentifier.DeleteFavoriteId, "" + i));
		DeltaHeader dh = new DeltaHeader(DeltaType.FAVORITE_DELETE, new User(), params);
		dh.setId(1200 + i);
		return dh;
	}

	private DeltaHeader makeUpdateFavoriteDelta(int i){
		Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
		params.put(DeltaParamIdentifier.UpdateFavoriteId, new DeltaParam(DeltaParamIdentifier.UpdateFavoriteId, "" + i));
		DeltaHeader dh = new DeltaHeader(DeltaType.FAVORITE_UPDATE, new User(), params);
		dh.setId(1100 + i);
		return dh;
	}

	private DeltaHeader makeCreateFavoriteDelta(int i){
		Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
		params.put(DeltaParamIdentifier.CreateFavoriteNewId,
				new DeltaParam(DeltaParamIdentifier.CreateFavoriteNewId, "" + i));
		DeltaHeader dh = new DeltaHeader(DeltaType.FAVORITE_CREATE, new User(), params);
		dh.setId(1000 + i);
		return dh;
	}

	private class TestFavoriteDAO implements FavoriteDAO{
		@Override
		public void delete(Favorite favorite){
			throw new UnsupportedOperationException();
		}

		@Override
		public void delete(Integer id){
			throw new UnsupportedOperationException();
		}

		@Override
		public void deleteByFolder(Integer folderId){
			throw new UnsupportedOperationException();
		}

		@Override
		public Favorite get(Integer id){
			throw new UnsupportedOperationException();
		}

		@Override
		public Favorite save(Favorite favorite){
			throw new UnsupportedOperationException();
		}

		@Override
		public Favorite create(Favorite favorite){
			throw new UnsupportedOperationException();
		}

		@Override
		public long getCount(){
			throw new UnsupportedOperationException();
		}

		@Override
		public List<Favorite> getBySchema(int schemaId, int userId){
			throw new UnsupportedOperationException();
		}

		@Override
		public List<Favorite> getBySchema(int schemaId){
			return null;
		}

		@Override
		public List<Integer> getFavoriteIdsByFolder(Integer id){
			throw new UnsupportedOperationException();
		}

		@Override
		public List<Favorite> getFavorites(){
			List<Favorite> favorites = new ArrayList<Favorite>();
			Favorite f = new Favorite();
			f.setId(1);
			favorites.add(f);

			f = new Favorite();
			f.setId(2);
			favorites.add(f);

			f = new Favorite();
			f.setId(4);
			favorites.add(f);

			f = new Favorite();
			f.setId(5);
			favorites.add(f);
			return favorites;
		}
	}

	private class TestFavoritesFolderDAO implements FavoritesFolderDAO{
		@Override
		public Integer create(FavoritesFolder folder){
			throw new UnsupportedOperationException();
		}

		@Override
		public void delete(FavoritesFolder folder){
			throw new UnsupportedOperationException();
		}

		@Override
		public void delete(Integer id){
			throw new UnsupportedOperationException();
		}

		@Override
		public FavoritesFolder get(Integer id){
			throw new UnsupportedOperationException();
		}

		@Override
		public List<FavoritesFolder> getSubfolders(int id){
			throw new UnsupportedOperationException();
		}

		@Override
		public FavoritesFolder save(FavoritesFolder folder){
			throw new UnsupportedOperationException();
		}

		@Override
		public long getCount(){
			throw new UnsupportedOperationException();
		}

		@Override
		public List<FavoritesFolder> findByName(String name){
			throw new UnsupportedOperationException();
		}

		@Override
		public List<FavoritesFolder> getFolders(int schemaId, int userId){
			throw new UnsupportedOperationException();
		}

		@Override
		public List<Integer> getTraverseListParentFirst(List<FavoritesFolder> folders, Integer folderId){
			throw new UnsupportedOperationException();
		}

		@Override
		public List<FavoritesFolder> getFolders(){
			List<FavoritesFolder> result = new ArrayList<FavoritesFolder>();
			FavoritesFolder ff = new FavoritesFolder();
			ff.setId(1);
			result.add(ff);

			ff = new FavoritesFolder();
			ff.setId(2);
			result.add(ff);

			ff = new FavoritesFolder();
			ff.setId(3);
			result.add(ff);

			ff = new FavoritesFolder();
			ff.setId(5);
			result.add(ff);
			return result;
		}
	}

	private class TestThematicMapDAO implements ThematicMapDAO{
		@Override
		public int createThematicMap(ThematicMap thematicMap){
			throw new UnsupportedOperationException();
		}

		@Override
		public void updateThematicMap(ThematicMap thematicMap){
			throw new UnsupportedOperationException();
		}

		@Override
		public ThematicMap getThematicMap(int id){
			throw new UnsupportedOperationException();
		}

		@Override
		public List<ThematicMap> getThematicMapsByFolderId(int folderId, int groupId){
			throw new UnsupportedOperationException();
		}

		@Override
		public ThematicMap getThematicMapByName(String name, int folderId, int groupId){
			throw new UnsupportedOperationException();
		}

		@Override
		public void deleteThematicMap(int thematicMapId){
			throw new UnsupportedOperationException();
		}

		@Override
		public int createThematicMapWithId(ThematicMap tm){
			throw new UnsupportedOperationException();
		}

		@Override
		public List<ThematicMap> getThematicMaps(){
			List<ThematicMap> result = new ArrayList<ThematicMap>();
			ThematicMap tm = new ThematicMap();
			tm.setId(1);
			result.add(tm);

			tm = new ThematicMap();
			tm.setId(3);
			result.add(tm);

			tm = new ThematicMap();
			tm.setId(4);
			result.add(tm);

			tm = new ThematicMap();
			tm.setId(5);
			result.add(tm);
			return result;
		}
	}

	private class TestEngineFactory implements GraphEngineFactory{
		@Override
		public GraphNi3Engine newGraph(int schemaId){
			return new TestGraph(1);
		}
	}

	private class TestGraph extends GraphNi3Engine{
		protected TestGraph(int schema){
			super(schema);
		}

		@Override
		public List<Object> getNodeWithEdges(int rootID, int groupId, DataFilter dataFilter){
			return null;
		}

		@Override
		public Node getNode(int RootID, int groupId, DataFilter dataFilter){
			throw new UnsupportedOperationException();
		}

		@Override
		public Edge getEdge(int _EdgeID, int groupIndex, DataFilter dataFilter){
			throw new UnsupportedOperationException();
		}

		@Override
		public List<Edge> getEdgesByFavorite(int favoriteId, int groupId, DataFilter dataFilter){
			throw new UnsupportedOperationException();
		}

		@Override
		public List<Object> findPath(int Node1ID, int Node2ID, int MaxPathLength, int PathLengthOverrun, int groupId,
				DataFilter dataFilter){
			throw new UnsupportedOperationException();
		}

		@Override
		public void deleteNode(int ID){
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean newNode(int newNodeID, ObjectDefinition ent){
			throw new UnsupportedOperationException();
		}

		@Override
		public Node reloadNode(int NodeID, int groupId, DataFilter dataFilter){
			throw new UnsupportedOperationException();
		}

		@Override
		public Node reloadNode(int NodeID){
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean reloadEdge(int id){
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean newEdge(int EdgeID, int FromID, int ToID, int ObjectType, int Type, float Strength, int InPath,
				int status, int Directed, int favoritesID, int userID, int groupID, boolean contextEdge){
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean newEdge(int newEdgeID){
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean updateEdge(int EdgeID){
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean updateEdge(int EdgeID, int FromID, int ToID, int connectionType, float strength, int inPath,
				int status, int Directed, int favoritesID, int objectType){
			throw new UnsupportedOperationException();
		}

		@Override
		public void deleteEdge(int EdgeID){
			throw new UnsupportedOperationException();
		}

		@Override
		public void removeTopicEdges(int TopicID){
			throw new UnsupportedOperationException();
		}

		@Override
		public void syncGraphWithDB(boolean background){
		}

		@Override
		public Edge getEdge(int edgeId){
			if (edgeId == 14)
				return null;
			Edge edge = new Edge();
			edge.setID(edgeId);
			return edge;
		}

		@Override
		public Node getNode(int nodeId){
			if (nodeId == 4)
				return null;
			return new Node(nodeId);
		}

		@Override
		public List<Integer> filterEdgesByFromTo(List<Integer> edgeIds, List<Integer> fromIds, List<Integer> toIds, int limit){
			return null; // To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public List<Integer> filterEdgesByNodes(List<Integer> searchedEdgeIds, List<Integer> searchedNodeIds, int limit){
			return null; // To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public List<Integer> getAllConnectedNodes(Integer id){
			return null; // To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Collection<Integer> getAllEdges(Integer id){
			return null; // To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Map<Integer, Integer> getAllEdgesWithType(int objectId){
			return null; // To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void retainVisibleNodes(Collection<Integer> ids, Group group, DataFilter dataFilter){
		}

		@Override
		public DataFilter createDataFilter(List<Integer> filteredValueIds){
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean containsNode(int id){
			return false;
		}
	}
}
