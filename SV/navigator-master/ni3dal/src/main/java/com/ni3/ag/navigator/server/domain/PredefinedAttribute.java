package com.ni3.ag.navigator.server.domain;

public class PredefinedAttribute{
	public static final String VALUE = "value";
	public static final String SCHEMA_ID = "attribute.entity.schema.id";

	private Integer id;
	private String value;
	private String label;
	private Integer touse;
	private Integer sort;
	private PredefinedAttribute parent;
	private String sourceId;
	private String haloColor;
	private transient String labelTrl;

	private Attribute attribute;

	public String getHaloColor(){
		return haloColor;
	}

	public Integer getId(){
		return id;
	}

	public String getLabel(){
		return label;
	}

	public PredefinedAttribute getParent(){
		return parent;
	}

	public Integer getSort(){
		return sort;
	}

	public String getSourceId(){
		return sourceId;
	}

	public Integer getTouse(){
		return touse;
	}

	public String getValue(){
		return value;
	}

	public void setHaloColor(final String haloColor){
		this.haloColor = haloColor;
	}

	public void setId(final Integer id){
		this.id = id;
	}

	public void setLabel(final String label){
		this.label = label;
		this.labelTrl = label;
	}

	public void setParent(final PredefinedAttribute pa){
		this.parent = pa;
	}

	public void setSort(final Integer sort){
		this.sort = sort;
	}

	public void setSourceId(final String sourceId){
		this.sourceId = sourceId;
	}

	public void setTouse(final Integer touse){
		this.touse = touse;
	}

	public void setValue(final String value){
		this.value = value;
	}

	public Attribute getAttribute(){
		return attribute;
	}

	public void setAttribute(Attribute attribute){
		this.attribute = attribute;
	}

	public String getLabelTrl(){
		return labelTrl;
	}

	public void setLabelTrl(String labelTrl){
		this.labelTrl = labelTrl;
	}

	public PredefinedAttribute clone(){
		final PredefinedAttribute value = new PredefinedAttribute();
		value.setId(getId());
		value.setAttribute(getAttribute());
		value.setValue(getValue());
		value.setLabel(getLabel());
		value.setTouse(getTouse());
		value.setSort(getSort());
		value.setParent(getParent());
		value.setSourceId(getSourceId());
		value.setHaloColor(getHaloColor());
		return value;
	}

	@Override
	public boolean equals(Object o){
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PredefinedAttribute that = (PredefinedAttribute) o;

		if (id != null ? !id.equals(that.id) : that.id != null) return false;

		return true;
	}

	@Override
	public int hashCode(){
		return id != null ? id.hashCode() : 0;
	}

	@Override
	public String toString(){
		return "PredefinedAttribute{" +
				"id=" + id +
				", value='" + value + '\'' +
				", label='" + label + '\'' +
				'}';
	}
}
