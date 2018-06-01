package com.ni3.ag.navigator.server.services;

import com.ni3.ag.navigator.server.cache.GraphNi3Engine;

public interface GraphEngineFactory {
    GraphNi3Engine newGraph(int schemaId);
}
