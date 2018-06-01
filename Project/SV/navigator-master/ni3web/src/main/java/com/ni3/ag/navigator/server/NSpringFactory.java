package com.ni3.ag.navigator.server;

import com.ni3.ag.navigator.server.cache.SSOCache;
import com.ni3.ag.navigator.server.cache.UserGroupCache;
import com.ni3.ag.navigator.server.dao.*;
import com.ni3.ag.navigator.server.db.DatabaseAdapter;
import com.ni3.ag.navigator.server.gateway.SyncGateway;
import com.ni3.ag.navigator.server.geocode.coding.GeoCoder;
import com.ni3.ag.navigator.server.jobs.DeltaUserRouterJob;
import com.ni3.ag.navigator.server.license.LicenseValidator;
import com.ni3.ag.navigator.server.passadmin.PasswordSender;
import com.ni3.ag.navigator.server.reports.ReportManager;
import com.ni3.ag.navigator.server.services.*;
import com.ni3.ag.navigator.server.servlets.util.UserDataIntegrityValidator;
import com.ni3.ag.navigator.server.session.ThreadLocalStorage;
import com.ni3.ag.navigator.server.sync.SynchronizationManager;
import com.ni3.ag.navigator.shared.gateway.LoginGateway;
import com.ni3.ag.navigator.shared.util.passwordencoder.PasswordSaltGetter;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class NSpringFactory{
	private static NSpringFactory instance = null;
	private ClassPathXmlApplicationContext context = null;
	private static final Logger log = Logger.getLogger(NSpringFactory.class);

	public static void init(){
		if (instance == null){
			log.info("Initializing Navigator bean factory");
			try{
				instance = new NSpringFactory();
				instance.context = new ClassPathXmlApplicationContext("spring-beans.xml");
			} catch (final Exception e){
				log.error("Error while initializing NSpringFactory", e);
			}
		} else{
			log.warn("Spring factory is already initialized.");
		}
	}

	public static void destroy(){
		log.info("Finalizing spring factory");
		instance.context.close();
		instance = null;
	}

	private NSpringFactory(){
	}

	public static NSpringFactory getInstance(){
		return instance;
	}

	public AttributeDAO getAttributeDao(){
		return (AttributeDAO) getBean("attributeDAO");
	}

	public Object getBean(final String beanName){
		final Object bean = context.getBean(beanName);
		return bean;
	}

	public ContextAttributeDAO getContextAttributeDao(){
		return (ContextAttributeDAO) getBean("contextAttributeDAO");
	}

	public ContextDAO getContextDao(){
		return (ContextDAO) getBean("contextDAO");
	}

	public DeltaParamDAO getDeltaParamDAO(){
		return (DeltaParamDAO) getBean("deltaParamDAO");
	}

	public EdgeDAO getEdgeDao(){
		return (EdgeDAO) getBean("edgeDAO");
	}

	public FavoriteDAO getFavoritesDao(){
		return (FavoriteDAO) getBean("favoriteDAO");
	}

	public FavoritesFolderDAO getFavoritesFolderDao(){
		return (FavoritesFolderDAO) getBean("favoritesFolderDAO");
	}

	public IAmDAO getIAmDao(){
		return (IAmDAO) getBean("iAmDAO");
	}

	public IconDAO getIconDao(){
		return (IconDAO) getBean("iconDAO");
	}

	public PredefinedAttributesDAO getPredefinedAttributesDao(){
		return (PredefinedAttributesDAO) getBean("predefinedAttributesDAO");
	}

	public ReportDAO getReportDao(){
		return (ReportDAO) getBean("reportDAO");
	}

	public ReportManager getReportManager(){
		return (ReportManager) getBean("reportManager");
	}

	public SSOCache getSsoCache(){
		return (SSOCache) getBean("ssoCache");
	}

	public DatabaseAdapter getDatabaseAdapter(){
		return (DatabaseAdapter) getBean("databaseAdapter");
	}

	public UserActivityDAO getUserActivityDao(){
		return (UserActivityDAO) getBean("userActivityDAO");
	}

	public GeoAnalyticsDAO getGeoAnalyticsDao(){
		return (GeoAnalyticsDAO) getBean("geoAnalyticsDAO");
	}

	public UserDAO getUserDao(){
		return (UserDAO) getBean("userDAO");
	}

	public UserSettingsDAO getUserSettingsDao(){
		return (UserSettingsDAO) getBean("userSettingsDAO");
	}

	public DeltaHeaderDAO getDeltaHeaderDAO(){
		return (DeltaHeaderDAO) getBean("deltaHeaderDAO");
	}

	public SyncGateway getSyncGateway(){
		return (SyncGateway) getBean("syncGateway");
	}

	public LoginGateway getLoginGateway(){
		return (LoginGateway) getBean("loginGateway");
	}

	public ThreadLocalStorage getThreadLocalStorage(){
		return (ThreadLocalStorage) getBean("threadLocalStorage");
	}

	public DeltaProcessor getDeltaProcessor(){
		return (DeltaProcessor) getBean("deltaProcessor");
	}

	public DeltaHeaderUserDAO getDeltaHeaderUserDAO(){
		return (DeltaHeaderUserDAO) getBean("deltaHeaderUserDAO");
	}

	public ObjectManagementService getObjectManagementService(){
		return (ObjectManagementService) getBean("objectManagementService");
	}

	public GroupDAO getGroupDao(){
		return (GroupDAO) getBean("groupDAO");
	}

	public UserGroupDAO getUserGroupDao(){
		return (UserGroupDAO) getBean("userGroupDAO");
	}

	public ObjectUserGroupDAO getObjectUserGroupDao(){
		return (ObjectUserGroupDAO) getBean("objectUserGroupDAO");
	}

	public DeltaUserRouterJob getDeltaUserRouterJob(){
		return (DeltaUserRouterJob) getBean("deltaUserRouterJob");
	}

	public UncommittedDeltasDAO getUncommittedDeltasDAO(){
		return (UncommittedDeltasDAO) getBean("uncommittedDeltasDAO");
	}

	public UserService getUserService(){
		return (UserService) getBean("userService");
	}

	public SchemaDAO getSchemaDAO(){
		return (SchemaDAO) getBean("schemaDAO");
	}

	public ObjectDefinitionDAO getObjectDefinitionDAO(){
		return (ObjectDefinitionDAO) getBean("objectDefinitionDAO");
	}

	public NodeDAO getNodeDAO(){
		return (NodeDAO) getBean("nodeDAO");
	}

	public ObjectScopeDAO getObjectScopeDAO(){
		return (ObjectScopeDAO) getBean("objectScopeDAO");
	}

	public ObjectDAO getObjectDAO(){
		return (ObjectDAO) getBean("objectDAO");
	}

	public ObjectDisposer getObjectDisposer(){
		return (ObjectDisposer) getBean("objectDisposer");
	}

	public GeoAnalyticsService getGeoAnalyticsService(){
		return (GeoAnalyticsService) getBean("geoAnalyticsService");
	}

	public PasswordSaltGetter getPasswordSaltGetter(){
		return (PasswordSaltGetter) getBean("passwordSaltGetter");
	}

	public GISService getGISService(){
		return (GISService) getBean("gisService");
	}

	public GisMapDAO getGisMapDAO(){
		return (GisMapDAO) getBean("gisMapDAO");
	}

	public GisTerritoryDAO getGisTerritoryDAO(){
		return (GisTerritoryDAO) getBean("gisTerritoryDAO");
	}

	public PaletteService getPaletteService(){
		return (PaletteService) getBean("paletteService");
	}

	public PaletteDAO getPaletteDAO(){
		return (PaletteDAO) getBean("paletteDAO");
	}

	public ObjectConnectionDAO getObjectConnectionDAO(){
		return (ObjectConnectionDAO) getBean("objectConnectionDAO");
	}

	public FavoritesFolderService getFavoritesFolderService(){
		return (FavoritesFolderService) getBean("favoritesFolderService");
	}

	public FavoritesService getFavoritesService(){
		return (FavoritesService) getBean("favoritesService");
	}

	public LanguageDAO getLanguageDAO(){
		return (LanguageDAO) getBean("languageDAO");
	}

	public ActivityStreamService getActivityStreamService(){
		return (ActivityStreamService) getBean("activityStreamService");
	}

	public ThematicMapDAO getThematicMapDAO(){
		return (ThematicMapDAO) getBean("thematicMapDAO");
	}

	public GISOverlayDAO getGisOverlayDAO(){
		return (GISOverlayDAO) getBean("gisOverlayDAO");
	}

	public ChartService getChartService(){
		return (ChartService) getBean("chartService");
	}

	public ChartsDAO getChartsDAO(){
		return (ChartsDAO) getBean("chartsDAO");
	}

	public ObjectChartDAO getObjectChartDAO(){
		return (ObjectChartDAO) getBean("objectChartDAO");
	}

	public ChartAttributeDAO getChartAttributeDAO(){
		return (ChartAttributeDAO) getBean("chartAttributeDAO");
	}

	public MetaphorSetDAO getMetaphorSetDAO(){
		return (MetaphorSetDAO) getBean("metaphorSetDAO");
	}

	public PrefilterService getPrefilterService(){
		return (PrefilterService) getBean("prefilterService");
	}

	public PrefilterDAO getPrefilterDAO(){
		return (PrefilterDAO) getBean("prefilterDAO");
	}

	public DeltaHeaderService getDeltaHeaderService(){
		return (DeltaHeaderService) getBean("deltaHeaderService");
	}

	public GeoCodeErrDAO getGeoCodeErrorDAO(){
		return (GeoCodeErrDAO) getBean("geoCodeErrDAO");
	}

	public GeoCoder getGeoCoder(){
		return (GeoCoder) getBean("geoCoder");
	}

	public GeoCacheDAO getGeoCacheDAO(){
		return (GeoCacheDAO) getBean("geoCacheDAO");
	}

	public GraphEngineFactory getGraphEngineFactory(){
		return (GraphEngineFactory) getBean("graphEngineFactory");
	}

	public LicenseValidator getLicenseValidator(){
		return (LicenseValidator) getBean("licenseValidator");
	}

	public PasswordSender getPasswordSender(){
		return (PasswordSender) getBean("passwordSender");
	}

	public ExportService getExportService(){
		return (ExportService) getBean("exportService");
	}

	public UserDataService getUserDataService(){
		return (UserDataService) getBean("userDataService");
	}

	public UserDataIntegrityValidator getUserDataIntegrityValidator(){
		return (UserDataIntegrityValidator) getBean("userDataIntegrityValidator");
	}

	public SynchronizationManager getSynchronizationManager(){
		return (SynchronizationManager) getBean("synchronizationManager");
	}

	public LicenseDAO getLicenseDAO(){
		return (LicenseDAO) getBean("licenseDAO");
	}

	public SystemStatusDAO getSystemStatusDAO(){
		return (SystemStatusDAO) getBean("systemStatusDAO");
	}

	public ThickClientModuleService getThickClientModuleService(){
		return (ThickClientModuleService) getBean("thickClientModuleService");
	}

	public MetaphorService getMetaphorService(){
		return (MetaphorService) getBean("metaphorService");
	}

	public DynamicAttributeService getDynamicAttributeService(){
		return (DynamicAttributeService) getBean("dynamicAttributeService");
	}

	public SchemaLoaderService getSchemaLoaderService(){
		return (SchemaLoaderService) getBean("schemaLoaderService");
	}

	public SearchService getSearchService(){
		return (SearchService) getBean("searchService");
	}

	public VisibilityService getVisibilityService(){
		return (VisibilityService) getBean("visibilityService");
	}

	public TranslationService getTranslationService(){
		return (TranslationService) getBean("translationService");
	}

	public UserGroupCache getUserGroupCache(){
		return (UserGroupCache) getBean("userGroupCache");
	}

	public GroupScopeProvider getGroupScopeProvider(){
		return (GroupScopeProvider) getBean("groupScopeProvider");
	}

	public CISObjectProviderService getCISObjectProviderService(){
		return (CISObjectProviderService) getBean("cisObjectProviderService");
	}
}
