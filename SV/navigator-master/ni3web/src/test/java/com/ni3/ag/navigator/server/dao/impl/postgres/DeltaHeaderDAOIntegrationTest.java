package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.DeltaHeaderDAO;
import com.ni3.ag.navigator.server.dao.UserDAO;
import com.ni3.ag.navigator.server.domain.DeltaHeader;
import com.ni3.ag.navigator.server.domain.DeltaParam;
import com.ni3.ag.navigator.server.domain.DeltaParamIdentifier;
import com.ni3.ag.navigator.shared.domain.DeltaType;
import com.ni3.ag.navigator.server.domain.SyncStatus;
import com.ni3.ag.navigator.shared.domain.User;

public class DeltaHeaderDAOIntegrationTest extends TestCase{
	private DeltaHeaderDAO deltaHeaderDAO = NSpringFactory.getInstance().getDeltaHeaderDAO();
	private UserDAO userDAO = NSpringFactory.getInstance().getUserDao();

	public void testGet(){
		DeltaHeader deltaHeader = deltaHeaderDAO.get(1);
		assertNotNull(deltaHeader);
		assertEquals(1, deltaHeader.getId());

		User expectedUser = userDAO.get(1);
		assertEquals(expectedUser, deltaHeader.getCreator());

		assertEquals(DeltaType.SETTING_UPDATE, deltaHeader.getDeltaType());
		assertEquals(SyncStatus.New, deltaHeader.getSyncStatus());
		assertEquals(1307004777202L, deltaHeader.getTimestamp().getTime());
		assertEquals(false, deltaHeader.isSync());
	}

	public void testSave_DO_NOTHING(){
		long countBefore = deltaHeaderDAO.getUnprocessedCount();
		deltaHeaderDAO.save(DeltaHeader.DO_NOTHING);
		long countAfter = deltaHeaderDAO.getUnprocessedCount();
		assertEquals(countBefore, countAfter);
	}

	public void testCount(){
		long count = deltaHeaderDAO.getUnprocessedCount();
		assertEquals(1, count);
	}

	public void testSave(){
		User user = userDAO.get(1);

		Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();

		params.put(DeltaParamIdentifier.CreateFavoriteDocument, new DeltaParam(DeltaParamIdentifier.CreateFavoriteDocument,
		        "Document"));
		params.put(DeltaParamIdentifier.CreateFavoriteName, new DeltaParam(DeltaParamIdentifier.CreateFavoriteName, "Name"));
		params.put(DeltaParamIdentifier.CreateFavoriteDescription, new DeltaParam(
		        DeltaParamIdentifier.CreateFavoriteDescription, "Description"));
		params.put(DeltaParamIdentifier.CreateFavoriteMode, new DeltaParam(DeltaParamIdentifier.CreateFavoriteMode, "Mode"));
		params.put(DeltaParamIdentifier.CreateFavoriteGroupFolder, new DeltaParam(
		        DeltaParamIdentifier.CreateFavoriteGroupFolder, "GroupFolder"));
		params.put(DeltaParamIdentifier.CreateFavoriteFolderId, new DeltaParam(DeltaParamIdentifier.CreateFavoriteFolderId,
		        "FolderId"));
		params.put(DeltaParamIdentifier.CreateFavoriteLayout, new DeltaParam(DeltaParamIdentifier.CreateFavoriteLayout,
		        "Layout"));
		params.put(DeltaParamIdentifier.CreateFavoriteSchemaId, new DeltaParam(DeltaParamIdentifier.CreateFavoriteSchemaId,
		        "SchemaId"));
		params.put(DeltaParamIdentifier.CreateFavoriteFolderNewId, new DeltaParam(
		        DeltaParamIdentifier.CreateFavoriteFolderNewId, "FavoriteId"));

		DeltaHeader delta = new DeltaHeader(DeltaType.FAVORITE_CREATE, user, params);

		long deltaCountBeforeSave = deltaHeaderDAO.getUnprocessedCount();
		delta = deltaHeaderDAO.save(delta);
		long deltaCountAfterSave = deltaHeaderDAO.getUnprocessedCount();
		assertEquals(deltaCountBeforeSave + 1, deltaCountAfterSave);

		deltaHeaderDAO.delete(delta);
		long deltaCountAfterDelete = deltaHeaderDAO.getUnprocessedCount();
		assertEquals(deltaCountBeforeSave, deltaCountAfterDelete);
	}

}
