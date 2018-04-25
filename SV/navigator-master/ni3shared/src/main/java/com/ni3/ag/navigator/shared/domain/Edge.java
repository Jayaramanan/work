package com.ni3.ag.navigator.shared.domain;

public class Edge{
	private int id;
	private double strength;
	private int inPath;
	private int status;
	private int directed;

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public double getStrength(){
		return strength;
	}

	public void setStrength(double strength){
		this.strength = strength;
	}

	public int getInPath(){
		return inPath;
	}

	public void setInPath(int inPath){
		this.inPath = inPath;
	}

	public int getStatus(){
		return status;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public int getDirected(){
		return directed;
	}

	public void setDirected(int directed){
		this.directed = directed;
	}

}
