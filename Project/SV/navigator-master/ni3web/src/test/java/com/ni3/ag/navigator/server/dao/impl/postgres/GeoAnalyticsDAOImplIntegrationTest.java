package com.ni3.ag.navigator.server.dao.impl.postgres;

import com.ni3.ag.navigator.server.NSpringFactory;
import junit.framework.TestCase;

/**
 * Created by ilya on 26.2.16.
 */
public class GeoAnalyticsDAOImplIntegrationTest extends TestCase {
    private GeoAnalyticsDAOImpl geoAnalyticsDAO;

    @Override
    protected void setUp() throws Exception {
        NSpringFactory.init();
        geoAnalyticsDAO = (GeoAnalyticsDAOImpl) NSpringFactory.getInstance().getGeoAnalyticsDao();

    }

    public void testGeo(){
//        geoAnalyticsDAO.getAllThematicData("geo_canada_province");
        geoAnalyticsDAO.getAllThematicData("geo_canada_hr");
    }
}
