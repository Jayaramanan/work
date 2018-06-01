package com.ni3.ag.navigator.server.geocode;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.quartz.JobDataMap;

public class Config {
    private static final Logger log = Logger.getLogger(Config.class);

    private static final String SQL_BASE = "com.ni3.ag.geo.sql";
    private static final String MINIMAL_GEOCODE_ACCURACY = "com.ni3.ag.geo.minimalAccuracy";
	private static final String TOO_MANY_QUERY_ERROR_COUNT_TO_ABORT = "com.ni3.ag.geo.tooManyQueriesErrorCountToAbort";

    private static final String[] propertyNames = {SQL_BASE + 1, MINIMAL_GEOCODE_ACCURACY};

    private JobDataMap properties;

    public boolean initConfiguration(JobDataMap properties) {
        this.properties = properties;
        for (String name : propertyNames) {
            if (!properties.containsKey(name)) {
                log.error("Missing property: " + name);
                return false;
            }
        }
        return true;
    }

    public List<String> sqls(){
        List<String> sqls = new ArrayList<String>();
        for(int i = 1; ; i++){
            if(!properties.containsKey(SQL_BASE + i))
                break;
            sqls.add(((String)properties.get(SQL_BASE + i)).trim());
        }
        return sqls;
    }

    public int getMinAccuracy() {
        return Integer.parseInt((String) properties.get(MINIMAL_GEOCODE_ACCURACY));
    }

	public int getTooManyQueriesCountToAbort(){
		if(properties.get(TOO_MANY_QUERY_ERROR_COUNT_TO_ABORT) == null)
			return 1000;
		return Integer.parseInt((String) properties.get(TOO_MANY_QUERY_ERROR_COUNT_TO_ABORT));
	}
}
