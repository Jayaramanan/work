package com.ni3.ag.navigator.server.domain;


public class Edge{
	protected int ID;// cis_edges.id
	protected int favoriteId;// cis_edges.favid
	protected int type;// sys_object.id of edge
	protected int status;// cis_edges.status
	protected int directed;// cis_edges.directed
	protected int connectionType;// cis_edges.connectiontype
	protected float strength;// cis_edges.strength
	protected int inPath;// cis_edges.inpath
	protected int creatorUser;// cis_edges.creator
	protected int creatorGroup;// sys_group.id of cis_edges.creator
	protected boolean isContextEdge;// in context flag

	private Node fromNode;
	private Node toNode;

	public Edge(){
	}

	public Edge(int id){
		this.ID = id;
	}

	public int getID(){
		return ID;
	}

	public void setID(int iD){
		ID = iD;
	}

	public int getFavoriteId(){
		return favoriteId;
	}

	public void setFavoriteId(int favoriteId){
		this.favoriteId = favoriteId;
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

	public int getDirected(){
		return directed;
	}

	public void setDirected(int directed){
		this.directed = directed;
	}

	public int getConnectionType(){
		return connectionType;
	}

	public void setConnectionType(int connectionType){
		this.connectionType = connectionType;
	}

	public float getStrength(){
		return strength;
	}

	public void setStrength(float strength){
		this.strength = strength;
	}

	public int getInPath(){
		return inPath;
	}

	public void setInPath(int inPath){
		this.inPath = inPath;
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

	public boolean isContextEdge(){
		return isContextEdge;
	}

	public void setContextEdge(boolean isContextEdge){
		this.isContextEdge = isContextEdge;
	}

	public Node getFromNode(){
		return fromNode;
	}

	public void setFromNode(Node fromNode){
		this.fromNode = fromNode;
	}

	public Node getToNode(){
		return toNode;
	}

	public void setToNode(Node toNode){
		this.toNode = toNode;
	}

	public void copyTo(Edge edge){
		edge.ID = ID;
		edge.favoriteId = favoriteId;
		edge.type = type;
		edge.status = status;
		edge.directed = directed;
		edge.connectionType = connectionType;
		edge.strength = strength;
		edge.inPath = inPath;
		edge.creatorUser = creatorUser;
		edge.creatorGroup = creatorGroup;
		edge.isContextEdge = isContextEdge;
	}
}
