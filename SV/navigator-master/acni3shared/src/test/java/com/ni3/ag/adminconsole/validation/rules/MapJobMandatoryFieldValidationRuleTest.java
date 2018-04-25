/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.MapJob;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.jobs.MapJobStatus;
import com.ni3.ag.adminconsole.shared.model.impl.MapJobModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class MapJobMandatoryFieldValidationRuleTest extends TestCase{

	public void testPerformCheck(){
		ACValidationRule rule = new MapJobMandatoryFieldsValidationRule();
		MapJobModel model = new MapJobModel();
		MapJob job = new MapJob();
		job.setUser(new User());
		job.setX1(BigDecimal.ONE);
		job.setX2(BigDecimal.ONE);
		job.setY1(BigDecimal.ONE);
		job.setY2(BigDecimal.ONE);
		job.setScale("scale");
		job.setStatus(MapJobStatus.Scheduled.getValue());
		List<MapJob> jobs = new ArrayList<MapJob>();
		jobs.add(job);
		DatabaseInstance instance = new DatabaseInstance("instance");
		model.setCurrentDatabaseInstance(instance);
		model.setJobs(jobs);

		rule.performCheck(model);
		List<ErrorEntry> errors = rule.getErrorEntries();
		assertTrue(errors.isEmpty());

		job.setUser(null);
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertFalse(errors.isEmpty());

		job.setUser(new User());
		job.setX1(null);
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertFalse(errors.isEmpty());

		job.setX1(BigDecimal.ONE);
		job.setX2(null);
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertFalse(errors.isEmpty());

		job.setX2(BigDecimal.ONE);
		job.setY1(null);
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertFalse(errors.isEmpty());

		job.setY1(BigDecimal.ONE);
		job.setY2(null);
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertFalse(errors.isEmpty());

		job.setY2(BigDecimal.ONE);
		job.setScale(null);
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertFalse(errors.isEmpty());

		job.setScale("");
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertFalse(errors.isEmpty());

		job.setScale("10000,20000");
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertTrue(errors.isEmpty());
	}
}
