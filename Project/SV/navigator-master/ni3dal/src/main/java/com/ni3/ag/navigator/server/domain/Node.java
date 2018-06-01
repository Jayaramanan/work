package com.ni3.ag.navigator.server.domain;

import java.util.ArrayList;
import java.util.List;

public class Node{
	protected int ID; // cis_nodes.id
	protected  int type;// sys_object id for node
	protected  int status;// status from cis_nodes
	protected  int creatorUser;// id of user created the node
	protected  int creatorGroup; // id of group of user

	protected List outEdges;
	protected List inEdges;

	public Node(){

	}

	public Node(int id){
		this.ID = id;
		outEdges = new ArrayList();
		inEdges = new ArrayList();
	}

	public int getID(){
		return ID;
	}

	public void setID(int iD){
		ID = iD;
	}

	public int getType(){
		return type;
	}

	public void setType(int type){
		this.type = type;
	}

	public int getStatus(){
		return status;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public int getCreatorUser(){
		return creatorUser;
	}

	public void setCreatorUser(int creatorUser){
		this.creatorUser = creatorUser;
	}

	public int getCreatorGroup(){
		return creatorGroup;
	}

	public void setCreatorGroup(int creatorGroup){
		this.creatorGroup = creatorGroup;
	}

	public List<Edge> getOutEdges(){
		return outEdges;
	}

	public List<Edge> getInEdges(){
		return inEdges;
	}

	public void setOutEdges(List outEdges){
		this.outEdges = outEdges;
	}

	public void setInEdges(List inEdges){
		this.inEdges = inEdges;
	}

	public int getEdgeCount(){
		return inEdges.size() + outEdges.size();
	}

	public void addOutEdge(Edge edge){
		outEdges.add(edge);
	}

	public void addInEdge(Edge edge){
		inEdges.add(edge);
	}

	public void removeOutEdge(Edge edge){
		outEdges.remove(edge);
	}

	public void removeInEdge(Edge edge){
		inEdges.remove(edge);
	}

	public void copyDataTo(Node node){
		node.ID = ID;
		node.type = type;
		node.status = status;
		node.creatorUser = creatorUser;
		node.creatorGroup = creatorGroup;
	}

}
