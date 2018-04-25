/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.domain;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;

import com.ni3.ag.navigator.client.gateway.ObjectConnectionGateway;
import com.ni3.ag.navigator.client.gateway.impl.HttpObjectConnectionGatewayImpl;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.util.Utility;
import com.ni3.ag.navigator.shared.domain.LineStyle;

public class EdgeMetaphor{
	public static final int INVALID_INDEX = -1;
	private List<BasicStroke> strokes;
	private List<Color> lineColors;
	private List<ObjectConnection> objectConnections;

	public EdgeMetaphor(int ID){
		strokes = new ArrayList<BasicStroke>();
		lineColors = new ArrayList<Color>();

		ObjectConnectionGateway gateway = new HttpObjectConnectionGatewayImpl();
		objectConnections = gateway.getObjectConnections(ID);

		for (ObjectConnection oc : objectConnections){
			lineColors.add(Utility.createColor(oc.getColor()));
			strokes.add(createStroke(oc, 1.0));
		}
	}

	private BasicStroke createStroke(ObjectConnection m, double scaleFactor){
		return createStroke(m, scaleFactor, 1.0);
	}

	private BasicStroke createStroke(ObjectConnection m, double scaleFactor, double minInitWidth){
		float lineWidth = (float) (m.getLineWidth() < minInitWidth ? minInitWidth : m.getLineWidth());

		if (scaleFactor == 0)
			scaleFactor = 0.01;
		if (m.getLineStyle() == LineStyle.TRANSPARENT)
			return new InvisibleStroke((float) (lineWidth * scaleFactor));

		if (m.getLineStyle() == LineStyle.FULL)
			return new BasicStroke((float) (lineWidth * scaleFactor));
		else{
			final float[] dash = m.getLineStyle().getDashes();
			int l = dash.length;
			float[] tempDash = new float[l];
			for (int n = 0; n < l; n++)
				tempDash[n] = (float) (dash[n] * scaleFactor / 1.4);

			return new BasicStroke((int) (lineWidth * scaleFactor), BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f,
			        tempDash, tempDash[0] / 2.0f);
		}
	}

	public boolean canCreate(Node FromType[], Node ToType[], int _ConnectionObject){
		for (Node aFromType : FromType){
			for (Node aToType : ToType){
				boolean ret = false;

				for (ObjectConnection m : objectConnections){
					if (m.getFromObject() == aFromType.Type && m.getToObject() == aToType.Type
					        && m.getConnectionObject() == _ConnectionObject){
						ret = true;
						break;
					}
				}

				if (!ret)
					return false;
			}
		}

		return true;
	}

	public int resolveMetaphor(int FromType, int ToType, int _ConnectionType){
		for (int n = 0; n < objectConnections.size(); n++){
			ObjectConnection m = objectConnections.get(n);
			if (m.getFromObject() == FromType && m.getToObject() == ToType && m.getConnectionType() == _ConnectionType){
				return n;
			}
		}
		return INVALID_INDEX;
	}

	public BasicStroke getStroke(int metaindex){
		return strokes.get(metaindex);
	}

	public Color getLineColor(int metaindex){
		return lineColors.get(metaindex);
	}

	public BasicStroke createStroke(int metaindex, double scaleFactor){
		return createStroke(objectConnections.get(metaindex), scaleFactor);
	}

	public BasicStroke createStroke(int metaindex, double scaleFactor, double minInitWidth){
		return createStroke(objectConnections.get(metaindex), scaleFactor, minInitWidth);
	}

	public boolean validateConnection(int fromNodeType, int toNodeType, int connectionType){
		for (ObjectConnection m : objectConnections){
			if (m.getFromObject() == fromNodeType && m.getToObject() == toNodeType
			        && m.getConnectionType() == connectionType){
				return true;
			}
		}
		return false;
	}
}
