/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.graph;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.DBObject;
import com.ni3.ag.navigator.client.domain.Value;

public class GraphObject{
	public int ID;
	public int Type;
	public DBObject Obj;

	private boolean filteredOut;
	public boolean contracted;
	public int status;
	public int favoritesID;
	public int userID, groupID;

	public String lbl;
	private List<String> lblLines;

	public boolean inHierarchy;

	public GraphObject(){
		favoritesID = 0;
		lbl = null;
	}

	public boolean isFilteredOut(){
		return filteredOut;
	}

	public void setFilteredOut(boolean filteredOut){
		this.filteredOut = filteredOut;
	}

	public boolean isActive(){
		return !(contracted || filteredOut);
	}

	public void refreshLabel(){
		if (Obj != null){
			lbl = Obj.getLabel();
			lblLines = null;
		}
	}

	public Color[] getHalos(){
		if (Obj == null)
			return new Color[0];

		List<Color> result = new ArrayList<Color>();
		for (Attribute a : Obj.getEntity().getReadableAttributes()){
			if (!a.predefined)
				continue;
			if (Obj.getValue(a.ID) != null){
				if (a.multivalue){
					for (Value vvx : (Value[]) (Obj.getValue(a.ID)))
						if (vvx.getHaloColor() != null && vvx.isHaloColorSelected()
								&& !vvx.getHaloColor().equals(Color.black)){
							result.add(vvx.getHaloColor());
							if (result.size() >= 10)
								break;
						}
				} else{
					Value vx = (Value) Obj.getValue(a.ID);
					if (vx.getHaloColor() != null && vx.isHaloColorSelected() && !vx.getHaloColor().equals(Color.black)){
						result.add(vx.getHaloColor());
						if (result.size() >= 10)
							break;
					}
				}
			}
		}

		return result.toArray(new Color[result.size()]);
	}

	public List<String> getSplittedLabel(int maxWidth){
		if (lblLines == null){
			lblLines = wrapLabel(lbl, maxWidth);
		}
		return lblLines;
	}

	List<String> wrapLabel(String label, int maxWidth){
		List<String> lines = new ArrayList<String>();
		if (label != null && label.length() > maxWidth){
			String words[] = label.split(" ");

			String line = "";
			for (String word : words){
				if ((line + word).length() >= maxWidth){
					if (!line.isEmpty()){
						lines.add(line);
						line = "";
					}
				}
				if (!line.isEmpty() && !word.isEmpty()){
					line += " ";
				}
				line += word;
			}

			if (!line.isEmpty()){
				lines.add(line);
			}
		} else{
			lines.add(label);
		}
		return lines;
	}

	@Override
	public boolean equals(Object o){
		if (this == o)
			return true;
		if (!(o instanceof GraphObject))
			return false;

		GraphObject that = (GraphObject) o;

		if (ID != that.ID)
			return false;

		return true;
	}

	@Override
	public int hashCode(){
		return ID;
	}
}
