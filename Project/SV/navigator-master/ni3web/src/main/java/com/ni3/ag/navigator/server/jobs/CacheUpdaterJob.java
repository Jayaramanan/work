package com.ni3.ag.navigator.server.jobs;

import java.util.List;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.cache.GraphCache;
import com.ni3.ag.navigator.server.cache.GraphNi3Engine;
import com.ni3.ag.navigator.server.domain.Schema;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

public class CacheUpdaterJob implements StatefulJob{
	private static final Logger log = Logger.getLogger(CacheUpdaterJob.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException{
		long currentTime = System.currentTimeMillis();
		log.info("Sync local cache [STARTED]");
		doJob();
		log.info("Sync local cache [FINISHED](in " + (System.currentTimeMillis() - currentTime) + " ms)");
	}

	private void doJob(){
        List<Schema> schemas = NSpringFactory.getInstance().getSchemaLoaderService().getAllSchemas();
        for(Schema sch : schemas){
            GraphNi3Engine graph = GraphCache.getInstance().getGraph(sch.getId(), true);
            if (graph == null){
                log.warn("Seems that no one requested for graph until now, skipping sync " + sch.getId());
            }
        }
	}
}
