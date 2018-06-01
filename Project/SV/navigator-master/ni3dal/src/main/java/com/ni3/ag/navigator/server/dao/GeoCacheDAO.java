package com.ni3.ag.navigator.server.dao;

import java.util.List;
import java.util.Set;

public interface GeoCacheDAO {
    Set<String> getCacheTables();

    void cleanNodeCache(int id, List<String> cacheTables);

    void updateCache(List<String> cacheTables);
}
