/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.domain;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ni3.ag.navigator.client.domain.cache.MetaphorCache;
import com.ni3.ag.navigator.client.domain.metaphor.NumericMetaphor;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import com.ni3.ag.navigator.shared.domain.MetaphorIcon;
import com.ni3.ag.navigator.shared.domain.NodeMetaphor;
import com.ni3.ag.navigator.shared.util.StringTokenizerEx;

public class DBObject{
	private static final Logger log = Logger.getLogger(DBObject.class);
	private Entity entity; // DBObject type
	private int id;
	private double lon, lat; // GIS coordinates in degrees
	private NodeMetaphor metaphor;
	private NumericMetaphor numericMetaphor;
	private Map<Integer, Object> data;

	public DBObject(){
		data = new HashMap<Integer, Object>();
	}

	public DBObject(final Entity entity){
		this();
		initObjectData(entity);
	}

	// this is a helper constructor to be able to lookup an object in a List, etc.
	public DBObject(int id){
		this();
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public Entity getEntity(){
		return entity;
	}

	public double getLon(){
		return lon;
	}

	public void setLon(double lon){
		this.lon = lon;
	}

	public double getLat(){
		return lat;
	}

	public void setLat(double lat){
		this.lat = lat;
	}

	public void initObjectData(final Entity entity){
		metaphor = null;
		this.entity = entity;
		data.clear();
	}

	public Object getValue(final int attributeId){
		return data.get(attributeId);
	}

	public void setValue(final int attributeId, final Object value){
		if (value == null){
			data.remove(attributeId);
		} else{
			data.put(attributeId, value);
		}
	}

	public void setValueByAttributeName(final String attributeName, final Object value){
		final Attribute a = entity.getAttribute(attributeName);
		if (a != null){
			setValue(a.ID, value);
		}
	}

	public Object getValueByAttributeName(final String name){
		final Attribute a = entity.getAttribute(name);
		if (a != null){
			return data.get(a.ID);
		}

		return null;
	}

	public double getValueAsDouble(Attribute attribute){
		double value = 0.0;
		Object v = data.get(attribute.ID);
		if (v instanceof Double)
			value = (Double) v;
		else if (v instanceof Integer)
			value = (Integer) v;
		return value;
	}

	public String getLabel(){
		final StringBuilder ret = new StringBuilder();
		for (final Attribute a : entity.getInLabelAttributes()){
			if (ret.length() > 0){
				ret.append(" ");
			}

			if (data.get(a.ID) != null){
				ret.append(a.displayValue(data.get(a.ID)));
			}
		}

		return ret.toString();
	}

	public Map<Integer, Object> getData(){
		return data;
	}

	public void setData(final com.ni3.ag.navigator.shared.domain.DBObject src){
		data.clear();
		id = src.getId();
		for (int attrId : src.getData().keySet()){
			String fieldValue = src.getData().get(attrId);
			Attribute a = entity.getAttribute(attrId);
			assignValue(fieldValue, a);
		}

		if (entity.isNode()){
			setGeoCoords(src);
			metaphor = src.getMetaphor();
		}
	}

	private void setGeoCoords(com.ni3.ag.navigator.shared.domain.DBObject src){
		Attribute alon = entity.getAttribute("lon");
		Attribute alat = entity.getAttribute("lat");
		if (alon == null || alat == null){
			log.error("Lon or Lat attributes not found for entity " + entity.ID + " lon=" + alon + " lat=" + alat);
			return;
		}
		String slon = src.getData().get(alon.ID);
		String slat = src.getData().get(alat.ID);
		if (slon == null || slat == null)
			return;
		lon = Double.parseDouble(slon);
		lat = Double.parseDouble(slat);
	}

	public boolean hasContextValues(final Context c){
		for (final Attribute a : c.getAttributes()){
			if (data.get(a.ID) != null){
				return true;
			}
		}

		return false;
	}

	public void assignValue(String fieldValue, Attribute a){
		if (a.predefined){
			if (a.multivalue){
				final Value[] ret = new Value[fieldValue.length() - fieldValue.replace("{", "").length()];
				int i = 0;
				try{
					final StringTokenizerEx tok = new StringTokenizerEx(fieldValue, "}", false);
					tok.setReturnEmptyTokens(false);

					while (tok.hasMoreTokens()){
						final int valID = Integer.valueOf(tok.nextToken().substring(1));
						for (final Value v : a.getValues()){
							if (v.getId() == valID){
								ret[i] = v;
								i++;
								break;
							}
						}
					}

					data.put(a.ID, ret);
				} catch (final NumberFormatException e){
					data.remove(a.ID);
				}
			} else{
				try{
					data.put(a.ID, a.getValue(fieldValue));
				} catch (final Exception e){
					e.printStackTrace();
				}
			}
		} else{
			if (a.multivalue){
				Object[] ret;
				final int size = fieldValue.length() - fieldValue.replace("{", "").length();

				if (size > 0){
					ret = new Object[size];
					int i = 0;

					final StringTokenizerEx tok = new StringTokenizerEx(fieldValue, "}", false);
					tok.setReturnEmptyTokens(false);

					while (tok.hasMoreTokens()){
						ret[i] = a.getDataType().getValue(tok.nextToken().substring(1));
						i++;
					}
				} else{
					ret = new String[1];
					ret[0] = fieldValue;
				}

				data.put(a.ID, ret);
			} else{
				data.put(a.ID, a.getDataType().getValue(fieldValue));
			}
		}
	}

	@Override
	public String toString(){
		return getLabel();
	}

	@Override
	public boolean equals(Object o){
		if (o == null)
			return false;
		if (!(o instanceof DBObject))
			return false;
		return ((DBObject) o).id == id;
	}

	public MetaphorIcon getMetaphor(){
		MetaphorIcon result = null;
		if (metaphor == null){
			log.warn("Metaphor not defined for node: " + id);
		} else{
			result = metaphor.getMetaphor(SystemGlobals.getMetaphorSet());
		}
		return result;
	}

	public void setMetaphor(NodeMetaphor metaphor){
		this.metaphor = metaphor;
	}

	public void setNumericMetaphor(NumericMetaphor numericMetaphor){
		this.numericMetaphor = numericMetaphor;
	}

	public NumericMetaphor getNumericMetaphor(){
		return numericMetaphor;
	}

	public String getIconName(){
		final MetaphorIcon metaphor = getMetaphor();
		return metaphor != null ? metaphor.getIconName() : null;
	}

	public Image getIcon(){
		Image icon = null;
		MetaphorIcon m = getMetaphor();
		if (m != null){
			icon = m.getIcon();
			if (icon == null){
				icon = MetaphorCache.getInstance().getImage(m.getIconName());
				if (icon == null){
					log.warn("Unknown metaphor: " + m.getIconName() + ", default is taken");
					icon = MetaphorCache.getInstance().getImage("all.png");
				}
				m.setIcon(icon);
			}
		} else{
			icon = MetaphorCache.getInstance().getImage("all.png");
		}
		return icon;
	}

	public void setAssignedIconName(String displayName){
		if (displayName == null)
			metaphor.setAssignedMetaphor(null);
		else
			metaphor.setAssignedMetaphor(new MetaphorIcon(displayName, 0));
	}

	public String getAssignedIconName(){
		return metaphor.getAssignedMetaphor() != null ? metaphor.getAssignedMetaphor().getIconName() : null;
	}
}
