/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.reports.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRCompiler;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRProperties;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.apache.log4j.Logger;

import ar.com.fdvs.dj.core.CoreException;
import ar.com.fdvs.dj.core.DJDefaultScriptlet;
import ar.com.fdvs.dj.core.DJException;
import ar.com.fdvs.dj.core.DJJRDesignHelper;
import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.LayoutManager;
import ar.com.fdvs.dj.core.registration.ColumnRegistrationManager;
import ar.com.fdvs.dj.core.registration.DJGroupRegistrationManager;
import ar.com.fdvs.dj.core.registration.DJGroupVariableDefRegistrationManager;
import ar.com.fdvs.dj.core.registration.VariableRegistrationManager;
import ar.com.fdvs.dj.domain.ColumnProperty;
import ar.com.fdvs.dj.domain.DJCalculation;
import ar.com.fdvs.dj.domain.DynamicJasperDesign;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.DJGroupVariableDef;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PercentageColumn;
import ar.com.fdvs.dj.util.DJCompilerFactory;
import ar.com.fdvs.dj.util.LayoutUtils;

@SuppressWarnings("unchecked")
public class Ni3DynamicJasperHelper extends DynamicJasperHelper{
	private static Logger log = Logger.getLogger(Ni3DynamicJasperHelper.class);
	private static final String DJ_RESOURCE_BUNDLE = "dj-messages";

	private final static void registerEntities(DynamicJasperDesign jd, DynamicReport dr, LayoutManager layoutManager){
		ColumnRegistrationManager columnRegistrationManager = new ColumnRegistrationManager(jd, dr, layoutManager);
		columnRegistrationManager.registerEntities(dr.getColumns());

		DJGroupRegistrationManager djGroupRegistrationManager = new DJGroupRegistrationManager(jd, dr, layoutManager);
		djGroupRegistrationManager.registerEntities(dr.getColumnsGroups());

		VariableRegistrationManager variableRegistrationManager = new VariableRegistrationManager(jd, dr, layoutManager);
		variableRegistrationManager.registerEntities(dr.getVariables());

		registerPercentageColumnsVariables(jd, dr, layoutManager);
		registerOtherFields(jd, dr.getFields());
		Locale locale = dr.getReportLocale() == null ? Locale.getDefault() : dr.getReportLocale();
		if (log.isDebugEnabled()){
			log.debug("Requested Locale = " + dr.getReportLocale() + ", Locale to use: " + locale);
		}
		ResourceBundle messages = null;
		if (dr.getResourceBundle() != null){
			try{
				messages = ResourceBundle.getBundle(dr.getResourceBundle(), locale);
			} catch (MissingResourceException e){
				log.warn(e.getMessage() + ", usign default (dj-messages)");
			}
		}

		if (messages == null){
			try{
				messages = ResourceBundle.getBundle(DJ_RESOURCE_BUNDLE, locale);
			} catch (MissingResourceException e){
				log.warn(e.getMessage() + ", usign default (dj-messages)");
				try{
					messages = ResourceBundle.getBundle(DJ_RESOURCE_BUNDLE, Locale.ENGLISH);
				} catch (MissingResourceException e2){
					log.error("Default messajes not found: " + DJ_RESOURCE_BUNDLE + ", " + e2.getMessage(), e2);
					throw new DJException("Default messajes file not found: " + DJ_RESOURCE_BUNDLE + "en.properties", e2);
				}
			}
		}
		jd.getParametersWithValues().put(JRDesignParameter.REPORT_RESOURCE_BUNDLE, messages);
		jd.getParametersWithValues().put(JRDesignParameter.REPORT_LOCALE, locale);
	}

	private static void registerPercentageColumnsVariables(DynamicJasperDesign jd, DynamicReport dr,
	        LayoutManager layoutManager){
		for (Iterator iterator = dr.getColumns().iterator(); iterator.hasNext();){
			AbstractColumn column = (AbstractColumn) iterator.next();

			/**
			 * Group should not be needed in the percentage column. There should be a variable for each group, using
			 * parent group as "rest group"
			 */
			if (column instanceof PercentageColumn){
				PercentageColumn percentageColumn = ((PercentageColumn) column);
				for (Iterator iterator2 = dr.getColumnsGroups().iterator(); iterator2.hasNext();){
					DJGroup djGroup = (DJGroup) iterator2.next();
					JRDesignGroup jrGroup = LayoutUtils.getJRDesignGroup(jd, layoutManager, djGroup);
					DJGroupVariableDefRegistrationManager variablesRM = new DJGroupVariableDefRegistrationManager(jd, dr,
					        layoutManager, jrGroup);
					DJGroupVariableDef variable = new DJGroupVariableDef(percentageColumn.getGroupVariableName(djGroup),
					        percentageColumn.getPercentageColumn(), DJCalculation.SUM);
					Collection entities = new ArrayList();
					entities.add(variable);
					variablesRM.registerEntities(entities);
				}
			}
		}
	}

	private static void registerOtherFields(DynamicJasperDesign jd, List fields){
		for (Iterator iter = fields.iterator(); iter.hasNext();){
			ColumnProperty element = (ColumnProperty) iter.next();
			JRDesignField field = new JRDesignField();
			field.setValueClassName(element.getValueClassName());
			field.setName(element.getProperty());
			try{
				jd.addField(field);
			} catch (JRException e){
				// if the field is already registered, it's not a problem
				log.warn(e.getMessage());
			}
		}

	}

	public static JasperPrint generateJasperPrint(DynamicReport dr, LayoutManager layoutManager, Map _parameters,
	        byte[] template) throws JRException{
		log.debug("generating JasperPrint");
		JasperPrint jp = null;

		if (_parameters == null)
			_parameters = new HashMap();

		visitSubreports(dr, _parameters);
		compileOrLoadSubreports(dr, _parameters, "r");

		DynamicJasperDesign jd = generateJasperDesign(dr, template);
		Map params = new HashMap();
		if (!_parameters.isEmpty()){
			registerParams(jd, _parameters);
			params.putAll(_parameters);
		}
		registerEntities(jd, dr, layoutManager);
		layoutManager.applyLayout(jd, dr);
		JRProperties.setProperty(JRCompiler.COMPILER_PREFIX, DJCompilerFactory.getCompilerClassName());
		JasperReport jr = JasperCompileManager.compileReport(jd);
		params.putAll(jd.getParametersWithValues());
		jp = JasperFillManager.fillReport(jr, params);

		return jp;
	}

	protected static DynamicJasperDesign generateJasperDesign(DynamicReport dr, byte[] template) throws CoreException{
		DynamicJasperDesign jd = null;
		try{
			if ((template != null && template.length > 0) || dr.getTemplateFileName() != null){
				if (template != null){
					JasperDesign jdesign = JRXmlLoader.load(new ByteArrayInputStream(template));
					jd = DJJRDesignHelper.downCast(jdesign, dr);
				} else{
					log.debug("about to load template file: " + dr.getTemplateFileName()
					        + ", Attemping to find the file directly in the file system.");
					File file = new File(dr.getTemplateFileName());
					if (file.exists()){
						JasperDesign jdesign = JRXmlLoader.load(file);
						jd = DJJRDesignHelper.downCast(jdesign, dr);
					} else{
						log.debug("Not found: Attemping to find the file in the classpath...");
						URL url = DynamicJasperHelper.class.getClassLoader().getResource(dr.getTemplateFileName());
						JasperDesign jdesign = JRXmlLoader.load(url.openStream());
						jd = DJJRDesignHelper.downCast(jdesign, dr);
					}
				}
				Ni3DJJRDesignHelper.populateReportOptionsFromDesign(jd, dr);

			} else{
				// Create new JasperDesign from the scratch
				jd = DJJRDesignHelper.getNewDesign(dr);
			}
			jd.setScriptletClass(DJDefaultScriptlet.class.getName()); // Set up

			registerParameters(jd, dr);
		} catch (JRException e){
			throw new CoreException(e.getMessage(), e);
		} catch (IOException e){
			throw new CoreException(e.getMessage(), e);
		}
		return jd;
	}
}
