package com.ni3.ag.navigator.server.services.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.ni3.ag.navigator.server.cache.GraphNi3Engine;
import com.ni3.ag.navigator.server.services.GraphEngineFactory;
import org.apache.log4j.Logger;

public class GraphEngineFactoryImpl implements GraphEngineFactory {
    private static final Logger log = Logger.getLogger(GraphEngineFactoryImpl.class);

	private String graphClassName;

	public void setGraphClassName(String graphClassName){
		this.graphClassName = graphClassName;
	}

	@Override
    public GraphNi3Engine newGraph(int schemaId) {
        try {
			log.debug("Using: " + graphClassName);
			Class graphClass = Class.forName(graphClassName);
			Constructor constructor = graphClass.getConstructor(int.class);
			return (GraphNi3Engine) constructor.newInstance(schemaId);
        } catch (ClassNotFoundException e){
			log.error("Cannot create graph instance", e);
		} catch (InstantiationException e){
			log.error("Cannot create graph instance", e);
		} catch (IllegalAccessException e){
			log.error("Cannot create graph instance", e);
		} catch (NoSuchMethodException e){
			log.error("Cannot create graph instance", e);
		} catch (InvocationTargetException e){
			log.error("Cannot create graph instance", e);
		} finally {
            log.info("Graph load completed");
        }
		return null;
    }
}
