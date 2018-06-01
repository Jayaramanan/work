package com.ni3.ag.navigator.shared.domain;


public class GeoTerritory{
	private int id;
	private String name;
	private double sum;
	private int nodeCount;

	public GeoTerritory(){
	}

	public GeoTerritory(int id, double sum, int nodeCount){
		this();
		this.id = id;
		this.sum = sum;
		this.nodeCount = nodeCount;
	}

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public double getSum(){
		return sum;
	}

	public void setSum(double sum){
		this.sum = sum;
	}

	public int getNodeCount(){
		return nodeCount;
	}

	public void setNodeCount(int nodeCount){
		this.nodeCount = nodeCount;
	}

	public double getAvg(){
		return nodeCount > 0 ? sum / nodeCount : 0;
	}

	public double getValue(boolean isTotal){
		return isTotal ? getSum() : getAvg();
	}

}