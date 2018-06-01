package com.ni3.ag.navigator.server.search;

import java.util.ArrayList;
import java.util.List;

public class AdvancedCriteria extends Criteria{
	private String geoCondition;
	private int queryType;
	private List<Section> sections;

	public void setGeoCondition(String geoCondition){
		this.geoCondition = geoCondition;
	}

	public void setQueryType(int queryType){
		this.queryType = queryType;
	}

	public void setSections(List<Section> sections){
		this.sections = sections;
	}

	public String getGeoCondition(){
		return geoCondition;
	}

	public int getQueryType(){
		return queryType;
	}

	public List<Section> getSections(){
		return sections;
	}

	@Override
	public boolean equals(Object o){
		if (this == o) return true;
		if (!(o instanceof AdvancedCriteria)) return false;

		AdvancedCriteria that = (AdvancedCriteria) o;

		if (queryType != that.queryType) return false;
		if (geoCondition != null ? !geoCondition.equals(that.geoCondition) : that.geoCondition != null) return false;
		if (sections != null ? !sections.equals(that.sections) : that.sections != null) return false;

		return true;
	}

	@Override
	public String toString(){
		return "AdvancedCriteria{" +
				"geoCondition='" + geoCondition + '\'' +
				", queryType=" + queryType +
				", sections=" + sections +
				'}';
	}

	public static class Section{
		private int entity;
		private List<ConditionGroup> conditionGroups = new ArrayList<ConditionGroup>();
		private List<Condition> conditions = new ArrayList<Condition>();
		private List<Order> orders = new ArrayList<Order>();

		public int getEntity(){
			return entity;
		}

		public void addCondition(Condition condition){
			conditions.add(condition);
		}

		public void addOrder(Order order){
			orders.add(order);
		}

		public List<Condition> getConditions(){
			return conditions;
		}

		public List<Order> getOrders(){
			return orders;
		}

		public void setEntity(int entity){
			this.entity = entity;
		}

		public ConditionGroup createConditionGroup(int attributeId){
			ConditionGroup cg = new ConditionGroup(attributeId);
			conditionGroups.add(cg);
			return cg;
		}

		public List<ConditionGroup> getConditionGroups(){
			return conditionGroups;
		}

		@Override
		public boolean equals(Object o){
			if (this == o) return true;
			if (!(o instanceof Section)) return false;

			Section section = (Section) o;

			if (entity != section.entity) return false;
			if (conditionGroups != null ? !conditionGroups.equals(section.conditionGroups) : section.conditionGroups != null)
				return false;
			if (conditions != null ? !conditions.equals(section.conditions) : section.conditions != null) return false;
			if (orders != null ? !orders.equals(section.orders) : section.orders != null) return false;

			return true;
		}

		@Override
		public String toString(){
			return "Section{" +
					"entity=" + entity +
					", conditionGroups=" + conditionGroups +
					", conditions=" + conditions +
					", orders=" + orders +
					'}';
		}

		public static  class Condition{

			private int attributeId;
			private String operation;
			private String term;
			private boolean nullAllowed;

			public Condition(int attributeId, String operation, String term, boolean nullAllowed){
				this.attributeId = attributeId;
				this.operation = operation;
				this.term = term;
				this.nullAllowed = nullAllowed;
			}

			public int getAttributeId(){
				return attributeId;
			}

			public String getOperation(){
				return operation;
			}

			public String getTerm(){
				return term;
			}

			@Override
			public boolean equals(Object o){
				if (this == o) return true;
				if (!(o instanceof Condition)) return false;

				Condition condition = (Condition) o;

				if (attributeId != condition.attributeId) return false;
				if (operation != null ? !operation.equals(condition.operation) : condition.operation != null)
					return false;
				if (term != null ? !term.equals(condition.term) : condition.term != null) return false;
				if(nullAllowed != condition.nullAllowed)return false;
				return true;
			}

			@Override
			public int hashCode(){
				int result = attributeId;
				result = 31 * result + (operation != null ? operation.hashCode() : 0);
				result = 31 * result + (term != null ? term.hashCode() : 0);
				return result;
			}

			@Override
			public String toString(){
				return "Condition{" +
						"attributeId=" + attributeId +
						", operation='" + operation + '\'' +
						", term='" + term + '\'' +
						'}';
			}

			public boolean getNullAllowed(){
				return nullAllowed;
			}

			public void setTerm(String term){
				this.term = term;
			}
		}

		public static class ConditionGroup{
			private List<Condition> conditions = new ArrayList<Condition>();
			private boolean useAnd;
			private int attributeId;

			public ConditionGroup(int attributeId){
				this.attributeId = attributeId;
			}

			public int getAttributeId(){
				return attributeId;
			}

			public ConditionGroup addCondition(Condition condition){
				conditions.add(condition);
				return this;
			}

			public boolean getConditionConnectionType(){
				return useAnd;
			}

			public void setConditionConnectionType(boolean b){
				useAnd = b;
			}

			@Override
			public boolean equals(Object o){
				if (this == o) return true;
				if (!(o instanceof ConditionGroup)) return false;

				ConditionGroup that = (ConditionGroup) o;

				if (useAnd != that.useAnd) return false;
				if (conditions != null ? !conditions.equals(that.conditions) : that.conditions != null) return false;

				return true;
			}

			@Override
			public int hashCode(){
				int result = conditions != null ? conditions.hashCode() : 0;
				result = 31 * result + (useAnd ? 1 : 0);
				return result;
			}

			@Override
			public String toString(){
				return "ConditionGroup{" +
						"conditions=" + conditions +
						", useAnd=" + useAnd +
						'}';
			}

			public List<Condition> getConditions(){
				return conditions;
			}
		}

		public static class Order{

			private int attributeId;
			private boolean asc;

			public Order(int attributeId, boolean asc){
				this.attributeId = attributeId;
				this.asc = asc;
			}

			public int getAttributeId(){
				return attributeId;
			}

			public boolean getAsc(){
				return asc;
			}

			@Override
			public boolean equals(Object o){
				if (this == o) return true;
				if (!(o instanceof Order)) return false;

				Order order = (Order) o;

				if (asc != order.asc) return false;
				if (attributeId != order.attributeId) return false;

				return true;
			}

			@Override
			public int hashCode(){
				int result = attributeId;
				result = 31 * result + (asc ? 1 : 0);
				return result;
			}

			@Override
			public String toString(){
				return "Order{" +
						"attributeId=" + attributeId +
						", asc=" + asc +
						'}';
			}
		}
	}
}
