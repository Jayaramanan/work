package com.ni3.ag.navigator.server.jobs;

import java.io.IOException;
import java.util.*;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.GeoCacheDAO;
import com.ni3.ag.navigator.server.dao.GeoCodeErrDAO;
import com.ni3.ag.navigator.server.dao.NodeDAO;
import com.ni3.ag.navigator.server.geocode.Config;
import com.ni3.ag.navigator.server.geocode.coding.GeoCoder;
import com.ni3.ag.navigator.server.geocode.coding.GoogleGeoCoder;
import com.ni3.ag.navigator.server.geocode.data.GeoCodeError;
import com.ni3.ag.navigator.server.geocode.data.GeoCodeItem;
import com.ni3.ag.navigator.server.geocode.data.Location;
import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

public class GeoCodingJob implements StatefulJob {
    private static final Logger log = Logger.getLogger(GeoCodingJob.class);
    private GeoCodeErrDAO geoCodeErrorDAO;
    private NodeDAO nodeDAO;
    private GeoCoder geoCoder;
    private GeoCacheDAO geoCacheDAO;

    public GeoCodingJob() {
        geoCodeErrorDAO = NSpringFactory.getInstance().getGeoCodeErrorDAO();
        nodeDAO = NSpringFactory.getInstance().getNodeDAO();
        geoCoder = NSpringFactory.getInstance().getGeoCoder();
        geoCacheDAO = NSpringFactory.getInstance().getGeoCacheDAO();
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("Geo coding job [START]");
        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        Config config = new Config();
        if (!config.initConfiguration(dataMap)) {
            log.error("Geo coding job [DONE_WITH_ERROR] >> Error parse config");
            return;
        }
        doGeocodeJob(config);
        log.info("Geo coding job [DONE]");
    }

    private void doGeocodeJob(Config config) {
        Map<Integer, GeoCodeError> nodesToSkip = new HashMap<Integer, GeoCodeError>();
        if (!initPreviousErrors(nodesToSkip)) {
            log.error("Error init data from sys_geocode_error");
            return;
        }
        List<String> cacheTables = new ArrayList<String>();
        if(!initCacheTableNames(cacheTables)){
            log.error("Error init cache table names");
            return;
        }

        List<String> sqls = config.sqls();

        for (String sql : sqls) {
            log.debug("Processing sql " + sql);
            List<GeoCodeItem> items = nodeDAO.getAllToCode(sql);
            if (items == null) {
                log.error("Error get items to geocode");
                break;
            }
            log.info("Extracted item count " + items.size());
			try{
            	processResultSet(items, nodesToSkip, config, cacheTables);
			} catch (GoogleGeoCoder.GoogleTooManyQueriesAbortException e){
				log.error("Too many queries error level reached", e);
				break;
			}
		}
        geoCacheDAO.updateCache(cacheTables);
    }

    private boolean initCacheTableNames(List<String> cacheTables) {
        Set<String> tables = geoCacheDAO.getCacheTables();
        if(tables == null)
            return false;
        cacheTables.addAll(tables);
        return true;
    }

    private void processResultSet(List<GeoCodeItem> items, Map<Integer, GeoCodeError> nodesToSkip, Config config, List<String> cacheTables) throws GoogleGeoCoder.GoogleTooManyQueriesAbortException{
        int errorCount = 0;
        for (GeoCodeItem item : items) {
            if (skipNode(item.getId(), item.getRequest(), nodesToSkip))
                continue;
            try {
                log.debug("---------------------------------------------------");
                Location adr = geoCoder.getLocation(item.getRequest(), config);
                if (adr.status != GeoCoder.GeoCodeStatus.G_GEO_SUCCESS) {
                    log.error("Error geocode id: " + item.getId() + " -> (" + item.getRequest() + ")" + ": (" + adr.status + ") " + adr.status.getDescription());
                    storeInvalidResult(item.getId(), item.getRequest(), adr, nodesToSkip);
                    continue;
                }
                if(!nodeDAO.updateNodeGeoCoords(item.getId(), adr.lon, adr.lat, true))
                    log.error("Error update node " + item.getId() + " coordinates");
                else{
                    log.info("Node id:" + item.getId() + " geocoded successfully");
                    if (nodesToSkip.containsKey(item.getId()))
                        geoCodeErrorDAO.removeError(item.getId());
                    geoCacheDAO.cleanNodeCache(item.getId(), cacheTables);
                }
                errorCount = 0;
                continue;
            } catch (IOException e) {
                log.error("Service request error for node " + item.getId() + " (" + item.getRequest() + ")", e);
				if(e instanceof GoogleGeoCoder.GoogleTooManyQueriesAbortException){
					log.error("GoogleTooManyQueries error limit reached. Stopping process");
					throw (GoogleGeoCoder.GoogleTooManyQueriesAbortException)e;
				}
            }
            if (++errorCount > 50) {
				log.error("Error count exceeds max allowed count: " + errorCount + ". Stopping process");
                break;
            }
            mySleep(1500);
        }
    }

    private boolean skipNode(int id, String sAddr, Map<Integer, GeoCodeError> nodesToSkip) {
        if (nodesToSkip.containsKey(id)) {
            GeoCodeError gce = nodesToSkip.get(id);
            if (!gce.getPreviousAddressRequest().equals(sAddr)) {
                log.info("Error table contains node " + id + ", but address seems that was changed (" + gce.getPreviousAddressRequest() + " -> " + sAddr + ")");
                return false;
            }
            log.warn("Node: " + id + " already found in table sys_geocode_err with status " + nodesToSkip.get(id).getStatus() + " - so skipping it");
            return true;
        }
        return false;
    }

    private void storeInvalidResult(int id, String sAddr, Location adr, Map<Integer, GeoCodeError> nodesToSkip) {
        if (adr.status == GeoCoder.GeoCodeStatus.G_GEO_TOO_MANY_QUERIES)
            return;
        if (nodesToSkip.containsKey(id))
            updateErrorResult(id, sAddr, adr);
        else
            insertErrorResult(id, sAddr, adr);
    }

    private void updateErrorResult(int id, String sAddr, Location adr) {
        geoCodeErrorDAO.updateErrorResult(id, sAddr, adr);
    }

    private void insertErrorResult(int id, String sAddr, Location adr) {
        geoCodeErrorDAO.insertErrorResult(id, sAddr, adr);
    }

    private void mySleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            log.warn("Error sleep", e);
        }
    }


    private boolean initPreviousErrors(Map<Integer, GeoCodeError> nodesToSkip) {
        Map<Integer, GeoCodeError> loadedNodes = geoCodeErrorDAO.getPreviousErrorsMap();
        if (loadedNodes == null)
            return false;
        nodesToSkip.putAll(loadedNodes);
        return true;
    }
}
