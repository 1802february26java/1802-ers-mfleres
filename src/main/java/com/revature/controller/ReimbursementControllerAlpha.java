package com.revature.controller;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.revature.model.Employee;
import com.revature.model.Reimbursement;
import com.revature.model.ReimbursementStatus;
import com.revature.model.ReimbursementType;
import com.revature.service.EmployeeServiceAlpha;
import com.revature.service.ReimbursementService;
import com.revature.service.ReimbursementServiceAlpha;
import com.revature.util.ConnectionUtil;
import com.revature.util.GlobalVars;

public class ReimbursementControllerAlpha implements ReimbursementController{

	//logger
	private static Logger logger = Logger.getLogger(ConnectionUtil.class);
	static {
		logger.setLevel(Level.ALL);
	}
	
	//Singleton
	private static ReimbursementControllerAlpha reimbursementController = new ReimbursementControllerAlpha();
	private ReimbursementControllerAlpha() {}
	public static ReimbursementController getInstance() {
		return reimbursementController;
	}
	
	@Override
	public Object submitRequest(HttpServletRequest request) {
		Object loggedEmployeeObject = request.getSession().getAttribute("loggedEmployee");
		if(loggedEmployeeObject == null || !(loggedEmployeeObject instanceof Employee)) {
			logger.error("No employee logged in");
			return "Failure";
		}
		Employee loggedEmployee = (Employee)loggedEmployeeObject;
		double amount = (Double)request.getAttribute("amount");
		String description = request.getAttribute("description").toString();
		String reimbursementTypeString = request.getAttribute("type").toString();
		ReimbursementType type = null;
		Set<ReimbursementType> reimbursementTypes = ReimbursementServiceAlpha.getInstance().getReimbursementTypes();
		for(ReimbursementType rt : reimbursementTypes) {
			if(rt.getType().toUpperCase().equals(reimbursementTypeString.toUpperCase())) {
				type = rt;
				break;
			}
		}
		Reimbursement reimbursement = new Reimbursement(0, null, null, amount, description, loggedEmployee, null,
				null, type);
		if(ReimbursementServiceAlpha.getInstance().submitRequest(reimbursement)) {
			return "Success";
		}
		
		return "Failure";
	}

	@Override
	public Object singleRequest(HttpServletRequest request) {
		Employee loggedEmployee = (Employee)request.getSession().getAttribute("loggedEmployee");
		if(loggedEmployee == null) {
			logger.error("No employee logged in");
			return null;
		}
		int reimbursementId = (int)request.getAttribute("id");
		Reimbursement reimbursement = ReimbursementServiceAlpha.getInstance().getSingleRequest(new Reimbursement(reimbursementId, null, null, 0.0, null, null, null, null, null));
		
		if(reimbursement == null){
			logger.error("Reimbursement not found");
			return null;
		} else if(loggedEmployee.getEmployeeRole().getType() != "MANAGER" && reimbursement.getRequester().getId() != loggedEmployee.getId()) {
			logger.error("Insufficient permissions to view reimbursement");
			return null;
		}
		
		return reimbursement;
	}

	@Override
	public Object multipleRequests(HttpServletRequest request) {
		Employee loggedEmployee = (Employee) request.getSession().getAttribute("loggedEmployee");
		String reimbursementStatus = (String) request.getAttribute("status");
		logger.trace("reimbursement status: "+reimbursementStatus);
		if(loggedEmployee == null) {
			logger.error("No employee logged in");
			return GlobalVars.NO_EMPLOYEE;
		}
		if(reimbursementStatus == null || !(reimbursementStatus.equals("PENDING") || !reimbursementStatus.equals("RESOLVED"))) {
			logger.error("Invalid status request.");
			return GlobalVars.INVALID_REQUEST;
		}
		loggedEmployee = EmployeeServiceAlpha.getInstance().getEmployeeInformation(loggedEmployee);
		if(loggedEmployee.getEmployeeRole().getId() == 2) {
			//Manager views all requests
			
			if(reimbursementStatus.equals("PENDING")) {
				logger.trace("returning all pending requests");
				return ReimbursementServiceAlpha.getInstance().getAllPendingRequests();
			}
			else {
				logger.trace("returning all resolved requests");
				return ReimbursementServiceAlpha.getInstance().getAllResolvedRequests();
			}
		} else {
			//Employee only views their requests
			
			if(reimbursementStatus.equals("PENDING")) {
				logger.trace("returning all pending requests for employee");
				return ReimbursementServiceAlpha.getInstance().getUserPendingRequests(loggedEmployee);
			}
			else {
				logger.trace("returning all resolved requests for employee");
				return ReimbursementServiceAlpha.getInstance().getUserFinalizedRequests(loggedEmployee);
			}
		}
	}

	@Override
	public Object finalizeRequest(HttpServletRequest request) {
		Employee loggedEmployee = (Employee)request.getSession().getAttribute("loggedEmployee");
		Integer reimbursementID = (Integer)request.getAttribute("reimbursementId");
		String reimbursementStatusType = (String)request.getAttribute("reimbursementStatus");
		if(loggedEmployee == null) {
			logger.error("No employee logged in");
			return null;
		} else if(reimbursementID == null) {
			logger.error("No reimbursement id inputted as parameter");
			return null;
		} else if (reimbursementStatusType == null || (!reimbursementStatusType.toUpperCase().equals("APPROVED") && !reimbursementStatusType.toUpperCase().equals("DECLINED"))) {
			logger.error("Invalid reimbursment status");
			return null;
		}
		loggedEmployee = EmployeeServiceAlpha.getInstance().getEmployeeInformation(loggedEmployee);
		Reimbursement reimbursement = ReimbursementServiceAlpha.getInstance().getSingleRequest(new Reimbursement(reimbursementID, null, null, 0, null, null, null, null, null));
		if(loggedEmployee.getEmployeeRole().getId() == 2) {
			//Only manager can finalize
			reimbursement.setApprover(loggedEmployee);
			reimbursement.setStatus(new ReimbursementStatus(reimbursementStatusType.toUpperCase()));
			ReimbursementServiceAlpha.getInstance().finalizeRequest(reimbursement);
		}
		return null;
	}

	@Override
	public Object getRequestTypes(HttpServletRequest request) {
		return ReimbursementServiceAlpha.getInstance().getReimbursementTypes();
	}

}
