/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.cache;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class GraphCache{
	private static final Logger log = Logger.getLogger(GraphCache.class);
	private Map<Integer, GraphNi3Engine> graphs;

	private GraphCache(){
        graphs = new HashMap<Integer, GraphNi3Engine>();
	}

	private static GraphCache instance;
	static{
		instance = new GraphCache();
	}

	public static synchronized GraphCache getInstance(){
		return instance;
	}

	public GraphNi3Engine getGraph(int schemaId){
        if(!graphs.containsKey(schemaId))
            return null;
        GraphNi3Engine graph = graphs.get(schemaId);
        graph.syncGraphWithDB(false);
		return graph;
	}

	public GraphNi3Engine getGraph(int schemaId, boolean background){
        if(!graphs.containsKey(schemaId))
            return null;
        GraphNi3Engine graph = graphs.get(schemaId);
        graph.syncGraphWithDB(background);
		return graph;
	}

	public void setGraph(GraphNi3Engine graph){
        graphs.put(graph.getSchemaId(), graph);
	}

	public void clear(int schemaId){
		if(schemaId == -1){
			log.info("Resetting graphs for all schemas");
            graphs.clear();
		}
        else{
			log.info("Resetting graph for schema: " + schemaId);
            graphs.remove(schemaId);
		}
	}
}
