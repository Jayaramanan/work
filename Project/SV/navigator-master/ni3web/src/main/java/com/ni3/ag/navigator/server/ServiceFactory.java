package com.ni3.ag.navigator.server;

import com.ni3.ag.navigator.server.services.ExportService;
import com.ni3.ag.navigator.server.services.FavoritesService;

public interface ServiceFactory{

	ExportService getExportService();

	FavoritesService getFavoritesService();

}
