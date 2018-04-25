/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.reports.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.awt.*;
import javax.swing.*;

import ar.com.fdvs.dj.core.DJConstants;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.*;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.ImageScaleMode;
import ar.com.fdvs.dj.domain.constants.Page;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import com.google.protobuf.ByteString;
import com.ni3.ag.navigator.server.dao.AttributeDAO;
import com.ni3.ag.navigator.server.dao.ReportDAO;
import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.ReportTemplate;
import com.ni3.ag.navigator.server.domain.ReportType;
import com.ni3.ag.navigator.server.reports.ReportManager;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NRequest.Report;
import com.ni3.ag.navigator.shared.proto.NRequest.ReportAttribute;
import com.ni3.ag.navigator.shared.proto.NRequest.ReportData;
import com.ni3.ag.navigator.shared.proto.NRequest.ReportRow;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JExcelApiExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class JasperReportManagerImpl extends JdbcDaoSupport implements ReportManager{
	private static final Logger log = Logger.getLogger(JasperReportManagerImpl.class);
	private static final String MAP_IMAGE_PARAM = "mapImage";
	private static final String GRAPH_IMAGE_PARAM = "graphImage";
	private static final String LOGO_IMAGE_PARAM = "logoImage";
	private static final String METAPHOR_PROPERTY = "metaphor_prop";

	private ReportDAO reportDAO;
	private AttributeDAO attributeDAO;

	public void setReportDAO(ReportDAO reportDAO){
		this.reportDAO = reportDAO;
	}

	public void setAttributeDAO(AttributeDAO attributeDAO){
		this.attributeDAO = attributeDAO;
	}

	@Override
	public List<ReportTemplate> getReportTemplates(){
		List<ReportTemplate> allTemplates = reportDAO.getReportTemplates();
		List<ReportTemplate> templates = new ArrayList<ReportTemplate>();
		if (allTemplates != null){
			for (ReportTemplate template : allTemplates){
				if (template.getTemplate() != null && template.getTemplate().length > 0){
					templates.add(template);
				}
			}
		}
		log.debug("Got report templates: " + templates.size());
		return templates;
	}

	@Override
	public byte[] getReport(NRequest.Report report){
		int reportId = report.getId();
		ReportTemplate template = reportDAO.getReportTemplate(reportId);
		JasperPrint jp = null;
		if (ReportType.DYNAMIC_REPORT == template.getType()){
			jp = getDynamicReport(report, template);
		} else if (ReportType.STATIC_REPORT == template.getType()){
			jp = getStaticReport(report, template);
		}

		byte[] result = null;
		if (jp != null){
			switch (report.getReportFormat()){
				case PDF:
					result = getPDFReport(jp);
					break;
				case HTML:
					result = getHTMLReport(jp);
					break;
				case XLS:
					result = getXLSReport(jp);
					break;
			}
		}

		return result;
	}

	private JasperPrint getStaticReport(Report report, ReportTemplate template){
		log.debug("Compiling static report: " + template.getName());
		JasperPrint jasperPrint = null;
		ByteArrayInputStream is = null;
		try{
			is = new ByteArrayInputStream(template.getTemplate());
			JasperReport jasperReport = JasperCompileManager.compileReport(is);

			Connection c = getJdbcTemplate().getDataSource().getConnection();
			try{
				Map<String, String> parameters = new HashMap<String, String>();
				jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, c);
				log.debug("Report is compiled");
			} finally{
				if (c != null){
					c.close();
				}
			}
		} catch (JRException e){
			log.error("Invalid report xml", e);
		} catch (SQLException e){
			log.error(e);
		} finally{
			if (is != null){
				try{
					is.close();
				} catch (IOException e){
					log.error(e);
				}
			}
		}
		return jasperPrint;
	}

	private JasperPrint getDynamicReport(NRequest.Report report, ReportTemplate template){
		log.debug("Generating dynamic report for template: " + template.getName());
		final JasperPrint jp = prepareReport(report, template);
		return jp;
	}

	private byte[] getXLSReport(JasperPrint jasperPrint){
		log.debug("Generating xls");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try{
			JExcelApiExporter exp = new JExcelApiExporter();
			exp.setParameter(JRExporterParameter.OUTPUT_STREAM, bos);
			exp.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exp.exportReport();
		} catch (JRException e){
			log.error(e);
			return null;
		}
		return bos.toByteArray();
	}

	private byte[] getPDFReport(JasperPrint jasperPrint){
		log.debug("Generating pdf");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try{
			JasperExportManager.exportReportToPdfStream(jasperPrint, bos);
		} catch (JRException e){
			log.error(e);
			return null;
		}
		return bos.toByteArray();
	}

	private byte[] getHTMLReport(JasperPrint jasperPrint){
		log.debug("Generating html");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try{
			Ni3JRHtmlExporter exp = new Ni3JRHtmlExporter();
			exp.setParameter(JRExporterParameter.OUTPUT_STREAM, bos);
			exp.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exp.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
			exp.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
			exp.setParameter(JRHtmlExporterParameter.BETWEEN_PAGES_HTML, "");
			exp.exportReport();
		} catch (JRException e){
			log.error(e);
			return null;
		}
		return bos.toByteArray();
	}

	private Image getImage(ByteString bs){
		Image result = null;
		if (bs != null && !bs.isEmpty()){
			ImageIcon icon = new ImageIcon(bs.toByteArray());
			result = icon.getImage();
		}
		return result;
	}

	private JasperPrint prepareReport(Report report, ReportTemplate template){
		DynamicReport dr;
		JasperPrint result = null;
		try{
			JasperDesign jd = getReportDesign(template);

			final Map<String, Object> params = new HashMap<String, Object>();
			dr = generate(jd, report.getDataList(), params);

			Map<?, ?> paramsMap = jd.getParametersMap();

			if (paramsMap.containsKey(GRAPH_IMAGE_PARAM)){
				params.put(GRAPH_IMAGE_PARAM, getImage(report.getGraphImage()));
			}
			if (paramsMap.containsKey(MAP_IMAGE_PARAM)){
				params.put(MAP_IMAGE_PARAM, getImage(report.getMapImage()));
			}
			params.put(LOGO_IMAGE_PARAM, getImage(report.getLogoImage()));

			JasperPrint jp = Ni3DynamicJasperHelper.generateJasperPrint(dr, new ClassicLayoutManager(), params, template
			        .getTemplate());
			result = jp;
		} catch (ColumnBuilderException e1){
			log.error(e1);
		} catch (JRException e){
			log.error(e);
		} catch (ClassNotFoundException e){
			log.error(e);
		} catch (DJBuilderException e){
			log.error(e);
		}
		return result;
	}

	private JasperDesign getReportDesign(ReportTemplate template){
		JasperDesign design = null;
		try{
			if (template != null && template.getTemplate() != null && template.getTemplate().length > 0){
				log.debug("Getting design from template xml for report " + template.getName());
				design = JRXmlLoader.load(new ByteArrayInputStream(template.getTemplate()));
			}
		} catch (JRException e){
			log.error("Cannot load page options for report " + template.getName() + ". Erorr: " + e);
		}
		return design;
	}

	private DynamicReport generate(JasperDesign jd, List<ReportData> data, Map<String, Object> params)
	        throws ClassNotFoundException, DJBuilderException, ColumnBuilderException, JRException{
		Page pageSize = new Page(jd.getPageHeight(), jd.getPageWidth());
		Insets margins = new Insets(jd.getTopMargin(), jd.getLeftMargin(), jd.getBottomMargin(), jd.getRightMargin());

		DynamicReportBuilder drb = new DynamicReportBuilder();
		drb.setUseFullPageWidth(true);
		drb.setWhenNoDataAllSectionNoDetail();
		drb.setMargins(margins.top, margins.bottom, margins.left, margins.right);
		drb.setPageSizeAndOrientation(pageSize);
		if (hasDataBlock(jd)){
			createDataBlock(drb, data, margins, pageSize, params);
		}

		DynamicReport dr = drb.build();
		return dr;
	}

	private boolean hasDataBlock(JasperDesign jd){
		JRSection detailSection = jd.getDetailSection();
		if (detailSection != null){
			JRBand[] bands = detailSection.getBands();
			if (bands != null && bands.length > 0 && bands[0].getHeight() > 0){
				return true;
			}
		}
		return false;
	}

	private void createDataBlock(DynamicReportBuilder drb, List<ReportData> allData, Insets margins, Page pageSize,
	        Map<String, Object> params) throws ColumnBuilderException, ClassNotFoundException, DJBuilderException{
		int g = 1;
		for (ReportData rd : allData){
			List<Map<String, Object>> data = getDataList(rd);
			if (data.isEmpty()){
				continue;
			}

			DynamicReport subReport = createSubReport(rd, margins, pageSize);
			String dsName = "ds" + g;
			drb.addConcatenatedReport(subReport, new ClassicLayoutManager(), dsName,
			        DJConstants.DATA_SOURCE_ORIGIN_PARAMETER, DJConstants.DATA_SOURCE_TYPE_JRDATASOURCE);

			JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(data);
			params.put(dsName, ds);
			g++;
		}
	}

	private List<Map<String, Object>> getDataList(ReportData rd){
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		for (ReportRow row : rd.getRowsList()){

			Map<String, Object> mRow = new LinkedHashMap<String, Object>();
			if (rd.getHasMetaphor()){
				mRow.put(METAPHOR_PROPERTY, getMetaphor(row, rd.getIsNumericMetaphor()));
			}
			for (int c = 0; c < rd.getAttributesList().size(); c++){
				ReportAttribute attr = rd.getAttributesList().get(c);
				mRow.put(attr.getName(), row.getValues(c));
			}

			resultList.add(mRow);
		}

		return resultList;
	}

	private Object getMetaphor(ReportRow row, boolean numericMetaphor){
		Object result = null;
		if (numericMetaphor){
			result = row.getIndex();
		} else{
			final ByteString metaphor = row.getMetaphor();
			if (metaphor != null){
				ImageIcon icon = new ImageIcon(metaphor.toByteArray());
				result = icon.getImage();
			}
		}
		return result;
	}

	private DynamicReport createSubReport(ReportData data, Insets margins, Page pageSize) throws ColumnBuilderException,
	        ClassNotFoundException{
		Style subreportTitleStyle = new Style();
		subreportTitleStyle.setHorizontalAlign(HorizontalAlign.LEFT);
		Font font = new Font(14, "SansSerif", true, false, false);
		subreportTitleStyle.setFont(font);
		subreportTitleStyle.setPaddingLeft(10);

		FastReportBuilder fb = new FastReportBuilder();
		fb.setTitle(data.getName());
		fb.setMargins(5, 5, margins.left, margins.right);
		fb.setPageSizeAndOrientation(pageSize);
		fb.setDefaultStyles(subreportTitleStyle, null, null, null);
		fb.setUseFullPageWidth(true);

		if (data.getHasMetaphor()){
			AbstractColumn imageColumn = createMetaphorColumn(data.getIsNumericMetaphor());
			fb.addColumn(imageColumn);
		}

		final List<ReportAttribute> attributes = data.getAttributesList();
		for (ReportAttribute a : attributes){
			Attribute attr = null;
			if (a.getId() > 0){
				attr = attributeDAO.getAttribute(a.getId());
			} else{
				attr = new Attribute(a.getId());
				attr.setName(a.getName());
				attr.setLabel(a.getLabel());
			}
			Style colHeaderStyle = ReportFontStyle.getColumnHeaderStyle(attr.isLabelBold(), attr.isLabelItalic(), attr
			        .isLabelUnderline());
			Style detailStyle = ReportFontStyle.getDetailStyle(attr.isContentBold(), attr.isContentItalic(), attr
			        .isContentUnderline());
			fb.addColumn(attr.getLabel(), attr.getName(), String.class, 50, detailStyle, colHeaderStyle);
		}
		return fb.build();
	}

	private AbstractColumn createMetaphorColumn(boolean numericMetaphor) throws ColumnBuilderException{
		final ColumnBuilder cb = ColumnBuilder.getNew();

		final Style detailStyle = ReportFontStyle.getDetailStyleMetaphor();
		if (numericMetaphor){
			cb.setColumnProperty(METAPHOR_PROPERTY, Integer.class.getName());
			cb.setWidth(24);
			detailStyle.setPaddingRight(3);
		} else{
			cb.setColumnProperty(METAPHOR_PROPERTY, Image.class.getName());
			cb.setWidth(15);
			cb.setColumnType(ColumnBuilder.COLUMN_TYPE_IMAGE);
			cb.setImageScaleMode(ImageScaleMode.FILL_PROPORTIONALLY);
		}

		cb.setTitle("");

		cb.setFixedWidth(true);

		cb.setStyle(detailStyle);
		cb.setHeaderStyle(ReportFontStyle.getColumnHeaderStyle(true, true, false));

		AbstractColumn column = cb.build();

		return column;
	}

}