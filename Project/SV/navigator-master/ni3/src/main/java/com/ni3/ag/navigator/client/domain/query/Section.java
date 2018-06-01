/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.domain.query;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.domain.Schema;
import com.ni3.ag.navigator.shared.util.StringTokenizerEx;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXML;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXMLAttribute;

public class Section{
	private String name;
	private List<Condition> conditions;
	private Entity ent;
	private List<Order> order;

	public Section(){
		this.name = null;
		this.ent = null;
		conditions = new ArrayList<Condition>();
		order = new ArrayList<Order>();
	}

	public Section(String name, Entity ent){
		this.name = name;
		this.ent = ent;
		conditions = new ArrayList<Condition>();
		order = new ArrayList<Order>();
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public Entity getEnt(){
		return ent;
	}

	public List<Order> getOrder(){
		return order;
	}

	public List<Condition> getConditions(){
		return conditions;
	}

	public void add(Condition c){
		conditions.add(c);
	}

	public void add(Order o, int index){
		while (index >= order.size())
			order.add(null);
		order.set(index, o);
	}

	public String toString(){
		String ret = ent.ID + "\t" + name + "\t";
		for (Condition c : conditions)
			ret += c.toString();
		ret += "End\t";

		for (Order o : order)
			ret += o.toString();
		ret += "End\t";

		return ret;
	}

	public String toXML(){
		String ret = "<Section EntityID='" + ent.ID + "' Name='" + name + "'>";

		for (Condition c : conditions)
			ret += c.toXML();

		for (Order o : order)
			ret += o.toXML();
		ret += "</Section>";

		return ret;
	}

	public void fromXML(Schema schema, NanoXML xml){
		NanoXMLAttribute attrXML;

		while ((attrXML = xml.Tag.getNextAttribute()) != null){
			if ("EntityID".equals(attrXML.Name)){
				ent = schema.getEntity(attrXML.getIntegerValue());
			} else if ("Name".equals(attrXML.Name)){
				name = attrXML.getValue();
			}
		}

		Condition c;
		Order o;
		NanoXML nextX;
		while ((nextX = xml.getNextElement()) != null){
			if ("Condition".equals(nextX.getName())){
				c = new Condition(ent);
				c.fromXML(nextX);
				conditions.add(c);
			} else if ("Order".equals(nextX.getName())){
				o = new Order(ent);
				o.fromXML(nextX);
				order.add(o);
			}
		}
	}

	public boolean fromString(Schema schema, StringTokenizerEx tok){
		Condition c;

		if (!tok.hasMoreTokens())
			return false;

		String ID = tok.nextToken();
		if ("End".equals(ID))
			return false;

		ent = schema.getEntity(Integer.valueOf(ID));
		name = tok.nextToken();

		while (true){
			c = new Condition(ent);
			if (c.fromString(schema, tok)){
				conditions.add(c);
			} else
				break;
		}

		while (true){
			Order o = new Order(ent);
			if (o.fromString(tok)){
				order.add(o);
			} else
				break;
		}

		return true;
	}
}
