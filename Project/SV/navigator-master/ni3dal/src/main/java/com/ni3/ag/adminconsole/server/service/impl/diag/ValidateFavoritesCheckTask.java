/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl.diag;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Favorites;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.dao.FavoritesDAO;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class ValidateFavoritesCheckTask implements DiagnosticTask{

	private final static String DESCRIPTION = "Validating favorites";
	private final static String TOOLTIP_NO_CREATOR = "Favorite without a creator id detected, id = `{1}` name = `{2}`";
	private final static String TOOLTIP_MINOR_OUTDATE = "Potentially outdated favorite detected, id = `{1}` name = `{2}`";
	private final static String TOOLTIP_MAJOR_OUTDATE = "Outdated favorite detected, id = `{1}` name = `{2}`";
	private final static String ACTION_DESCRIPTION = "Contact system administrator";

	private FavoritesDAO favoritesDAO;

	private String databaseVersion;

	public void setDatabaseVersion(String databaseVersion){
		this.databaseVersion = databaseVersion;
	}

	public void setFavoritesDAO(FavoritesDAO favoritesDAO){
		this.favoritesDAO = favoritesDAO;
	}

	@Override
	public DiagnoseTaskResult makeDiagnose(Schema sch){
		// check for major-outdated favorites
		List<Favorites> majorOutdatedFavs = favoritesDAO.getMajorOutdatedFavorites(sch, databaseVersion);
		if (!majorOutdatedFavs.isEmpty()){
			Favorites fav = majorOutdatedFavs.get(0);
			String tooltip = getTooltip(TOOLTIP_MAJOR_OUTDATE, new Object[] { fav.getId(), fav.getName() });
			return new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, false, DiagnoseTaskStatus.Error, tooltip,
			        ACTION_DESCRIPTION);
		}
		// check for minor-outdated favorites
		List<Favorites> minorOutdatedFavs = favoritesDAO.getMinorOutdatedFavorites(sch, databaseVersion);
		if (!minorOutdatedFavs.isEmpty()){
			Favorites fav = minorOutdatedFavs.get(0);
			String tooltip = getTooltip(TOOLTIP_MINOR_OUTDATE, new Object[] { fav.getId(), fav.getName() });
			return new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, false, DiagnoseTaskStatus.Warning, tooltip,
			        null);
		}
		// check for favorites with no creator set
		List<Favorites> nullCreatorFavs = favoritesDAO.getFavoritesWithoutCreator(sch);
		if (!nullCreatorFavs.isEmpty()){
			Favorites fav = nullCreatorFavs.get(0);
			String tooltip = getTooltip(TOOLTIP_NO_CREATOR, new Object[] { fav.getId(), fav.getName() });
			return new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, false, DiagnoseTaskStatus.Warning, tooltip,
			        null);
		}
		return new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, false, DiagnoseTaskStatus.Ok, null, null);
	}

	@Override
	public DiagnoseTaskResult makeFix(DiagnoseTaskResult taskResult) throws ACFixTaskException, ACException{
		return null;
	}

	@Override
	public String getTaskDescription(){
		return DESCRIPTION;
	}

	private String getTooltip(String template, Object[] params){
		if (template != null && params != null && params.length > 0){
			for (int i = 0; i < params.length; i++){
				template = template.replace("{" + (i + 1) + "}", (params[i] == null ? "" : params[i].toString()));
			}
		}
		return template;
	}

}
