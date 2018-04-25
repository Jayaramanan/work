package com.ni3.ag.navigator.shared.domain;

import java.awt.Color;
import java.util.List;

public class GISOverlay{
	private int id;
	private int schemaId;
	private String name;
	private String tablename;
	private Color color;
	private int lineWidth;
	private boolean filled;
	private int version;
	private boolean loaded;

	private List<GisOverlayGeometry> geometries;

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getSchemaId(){
		return schemaId;
	}

	public void setSchemaId(int schemaId){
		this.schemaId = schemaId;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getTablename(){
		return tablename;
	}

	public void setTablename(String tablename){
		this.tablename = tablename;
	}

	public Color getColor(){
		return color;
	}

	public void setColor(Color color){
		this.color = color;
	}

	public int getLineWidth(){
		return lineWidth;
	}

	public void setLineWidth(int lineWidth){
		this.lineWidth = lineWidth;
	}

	public boolean isFilled(){
		return filled;
	}

	public void setFilled(boolean filled){
		this.filled = filled;
	}

	public List<GisOverlayGeometry> getGeometries(){
		return geometries;
	}

	public void setGeometries(List<GisOverlayGeometry> geometries){
		this.geometries = geometries;
	}

	public int getVersion(){
		return version;
	}

	public void setVersion(int version){
		this.version = version;
	}

	public boolean isLoaded(){
		return loaded;
	}

	public void setLoaded(boolean loaded){
		this.loaded = loaded;
	}

	public void addGeometry(GisOverlayGeometry geometry){
		this.geometries.add(geometry);
	}

	@Override
	public boolean equals(Object o){
		if (!(o instanceof GISOverlay)){
			return false;
		}
		return ((GISOverlay) o).getId() == id && ((GISOverlay) o).getVersion() == version;
	}

	@Override
	public String toString(){
		return "GISOverlay [id=" + id + ", schemaId=" + schemaId + ", name=" + name + ", tablename=" + tablename
		        + ", color=" + color + ", lineWidth=" + lineWidth + ", filled=" + filled + ", version=" + version
		        + ", loaded=" + loaded + ", geometries.size()=" + geometries.size() + "]";
	}
}
