/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.diag;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

import junit.framework.TestCase;

public class TaskTableModelTest extends TestCase{
	private TaskTableModel model;
	private List<DiagnoseTaskResult> results;

	public void setUp(){
		results = generateResults();
		model = new TaskTableModel(results, new ActionListenerMock());
	}

	private List<DiagnoseTaskResult> generateResults(){
		List<DiagnoseTaskResult> results = new ArrayList<DiagnoseTaskResult>();
		for (int i = 0; i < 10; i++){
			DiagnoseTaskResult dtr = new DiagnoseTaskResult("class" + i, "descr" + i, true, DiagnoseTaskStatus.Error,
			        "errdescr" + i, "action description");
			results.add(dtr);
		}
		return results;
	}

	private TaskButton generateTaskButton(int index, DiagnoseTaskStatus status){
		return new TaskButton(index, status);
	}

	public void testColumnCount(){
		assertEquals(4, model.getColumnCount());
	}

	public void testRowCount(){
		assertEquals(model.getRowCount(), results.size());
	}

	public void testValueAt(){
		for (int i = 0; i < results.size(); i++){
			DiagnoseTaskResult result = results.get(i);
			assertEquals(result.getDescription(), model.getValueAt(i, 0));
			assertEquals(result, model.getValueAt(i, 1));
			assertEquals(null, model.getValueAt(i, 2));
			assertEquals("action description", model.getValueAt(i, 3));
		}
	}

	public void testColumnEditable(){
		for (int i = 0; i < model.getRowCount(); i++){
			DiagnoseTaskResult result = results.get(i);
			assertEquals(result.isFixable(), model.isCellEditable(i, 2));
			assertFalse(model.isCellEditable(i, 0));
			assertFalse(model.isCellEditable(i, 1));
			assertFalse(model.isCellEditable(i, 3));
		}
	}

	public void testReplaceResult(){
		DiagnoseTaskResult dtr = new DiagnoseTaskResult("class3", "nd", true, DiagnoseTaskStatus.Ok, "ed", "ad");
		model.replaceResult(dtr);
		assertEquals(dtr.getDescription(), model.getValueAt(3, 0));
		assertEquals(dtr, model.getValueAt(3, 1));
		assertEquals("ad", model.getValueAt(3, 3));
		TaskButton btn = generateTaskButton(3, dtr.getStatus());
		TaskButton modelBtn = (TaskButton) model.getValueAt(3, 2);
		assertEquals(btn.getIndex(), modelBtn.getIndex());
		assertEquals(btn.getStatus(), modelBtn.getStatus());
	}

	private class ActionListenerMock implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e){
		}
	}
}
