package com.revature.model;

import java.io.Serializable;

/**
 * It defines the type of the Reimbursement request.
 * 
 * @author Revature LLC
 */
public class ReimbursementType implements Serializable {
	
	/**
	 * Compatibility with Java 1.x
	 */
	private static final long serialVersionUID = -6682033542018508191L;
	public enum Types { OTHER, COURSE, CERTIFICATION, TRAVELING };
	
	
	/**
	 * PRIMARY KEY
	 */
	private int id;
	
	/**
	 * NOT NULL
	 */
	private String type;
	
	public ReimbursementType() {}

	public ReimbursementType(int id, String type) {
		this.id = id;
		this.type = type;
	}
	
	public ReimbursementType(Types type) {
		this.id = getTypeId(type);
		this.type = getTypeAsString(type);
	}
	
	private int getTypeId(Types type) {
		switch(type) {
		case OTHER:
			return 1;
		case COURSE:
			return 2;
		case CERTIFICATION:
			return 3;
		case TRAVELING:
			return 4;
		default:
			return 0;
		}
	}
	
	private String getTypeAsString(Types type) {
		switch(type) {
		case OTHER:
			return "OTHER";
		case COURSE:
			return "COURSE";
		case CERTIFICATION:
			return "CERTIFICATION";
		case TRAVELING:
			return "TRAVELING";
		default:
			return null;
		}
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "ReimbursementType [id=" + id + ", type=" + type + "]";
	}
}
