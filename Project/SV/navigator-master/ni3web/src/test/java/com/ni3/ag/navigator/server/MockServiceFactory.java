package com.ni3.ag.navigator.server;

import com.ni3.ag.navigator.server.services.ExportService;
import com.ni3.ag.navigator.server.services.FavoritesService;

public class MockServiceFactory implements ServiceFactory{

	@Override
	public FavoritesService getFavoritesService(){
		return null;
	}

	@Override
	public ExportService getExportService(){
		return null;
	}

}
