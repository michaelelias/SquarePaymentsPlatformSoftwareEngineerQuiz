package com.square.engineering.location;

public class Location {

	private String id;
	private String name;
	private String owner;

	public Location() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String ownerName) {
		this.owner = ownerName;
	}

	@Override
	public String toString() {
		return "Location [id=" + id + ", name=" + name + ", owner=" + owner + "]";
	}

}
