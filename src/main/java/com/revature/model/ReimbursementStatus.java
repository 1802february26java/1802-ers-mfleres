package com.revature.model;

import java.io.Serializable;

/**
 * It defines the current status of a Reimbursement request.
 * 
 * @author Revature LLC
 */
public class ReimbursementStatus implements Serializable {

	/**
	 * Compatibility with Java 1.x
	 */
	private static final long serialVersionUID = 7006265099284488241L;

	public enum Status {PENDING,DECLINED,APPROVED}
	
	/**
	 * PRIMARY KEY
	 */
	private int id;
	
	/**
	 * NOT NULL (You can make this UNIQUE)
	 * Initial data: PENDING, APPROVED, DECLINED.
	 */
	private String status;
	
	public ReimbursementStatus() {}
	
	public ReimbursementStatus(int id, String status) {
		this.id = id;
		this.status = status;
	}
	
	public ReimbursementStatus(Status status) {
		switch(status) {
		case PENDING:
			this.id = 1;
			this.status ="PENDING";
			break;
		case DECLINED:
			this.id = 2;
			this.status = "DECLINED";
			break;
		case APPROVED:
			this.id = 3;
			this.status = "APPROVED";
		}
	}
	
	public ReimbursementStatus(String status) {
		switch(status.toUpperCase()) {
		case "PENDING":
			this.id = 1;
			this.status ="PENDING";
			break;
		case "DECLINED":
			this.id = 2;
			this.status = "DECLINED";
			break;
		case "APPROVED":
			this.id = 3;
			this.status = "APPROVED";
			break;
		default:
			this.id = 1;
			this.status = "PENDING";
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ReimbursementStatus [id=" + id + ", status=" + status + "]";
	}
}
