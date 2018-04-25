package com.ni3.ag.adminconsole.server.exporters;

import java.util.List;

import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.service.def.ObjectsConnectionsService;
import com.ni3.ag.adminconsole.validation.ACException;

public class ConnectionTypeExporter extends AbstractExporter<WritableSheet, Schema>{
	private static final Logger log = Logger.getLogger(ConnectionTypeExporter.class);

	private static final String[] CELL_LABELS = new String[] { "EdgeObject", "ConnectionType", "FromObject", "ToObject",
			"LineStyle", "LineColor", "LineWeight", "Hierarchical" };

	private static final Ni3ExcelStyleSheet styleSheet = DefaultNi3ExcelStyleSheet.getInstance();
	public static final String SHEET_NAME = "ObjectConnections";

	private ObjectsConnectionsService objectsConnectionsService;

	public void setObjectsConnectionsService(ObjectsConnectionsService objectsConnectionsService){
		this.objectsConnectionsService = objectsConnectionsService;
	}

	@Override
	protected void makeDecoration(WritableSheet target, Schema dataContainer){
		super.makeDecoration(target, dataContainer);
		try{
			for (int i = 0; i < CELL_LABELS.length; i++){
				Label l = new Label(i, 0, CELL_LABELS[i]);
				l.setCellFormat(styleSheet.getTableHeaderStyle());
				target.addCell(l);
			}
		} catch (RowsExceededException e){
			log.error(e);
		} catch (WriteException e){
			log.error(e);
		}
	}

	@Override
	protected void makeObjectExport(WritableSheet target, Schema schema) throws ACException{
		int row = 1;
		try{
			for (ObjectDefinition od : schema.getObjectDefinitions()){
				if (!od.isEdge())
					continue;
				List<ObjectConnection> connections = od.getObjectConnections();
				for (ObjectConnection oc : connections){
					setCellString(0, row, od.getName(), target);
					setCellString(1, row, oc.getConnectionType().getLabel(), target);
					setCellString(2, row, oc.getFromObject().getName(), target);
					setCellString(3, row, oc.getToObject().getName(), target);
					setCellString(4, row, "" + oc.getLineStyle().toInt(), target);
					setCellString(5, row, oc.getRgb(), target);
					setCellString(6, row, "" + oc.getLineWeight().getId(), target);
					setCellString(7, row, "" + objectsConnectionsService.isHierarchicalConnection(oc), target);
					row++;
				}
			}
		} catch (WriteException e){
			log.error("Error write connection types", e);
		}
	}

	private Label setCellString(int col, int row, String str, WritableSheet target) throws WriteException{
		Label label = new Label(col, row, str);
		target.addCell(label);
		int width = target.getColumnView(col).getSize();
		if (width > 256)
			width >>= 8;
		if (str.length() > width)
			width = str.length();
		target.setColumnView(col, width);
		return label;
	}
}
