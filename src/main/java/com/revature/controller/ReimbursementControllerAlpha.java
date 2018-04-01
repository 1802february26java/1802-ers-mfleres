package com.revature.controller;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.revature.ajax.ClientMessage;
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
		double amount = 0.0;
		try {
			amount = Double.valueOf(request.getParameter("amount"));
		} catch (NumberFormatException e) {
			return new ClientMessage("Invalid Input");
		}
		logger.trace("amount: " + amount);
		String description = request.getParameter("description");
		logger.trace("description: "+ description);
		String reimbursementTypeString = request.getParameter("type");
		logger.trace("type: " + reimbursementTypeString);
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
			return new ClientMessage("Success");
		}
		
		return new ClientMessage("Failure");
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
		String reimbursementStatus = (String) request.getParameter("status");
		if(reimbursementStatus == null) {
			reimbursementStatus = (String) request.getAttribute("status");
		}
		logger.trace("eployeeParam: "+request.getParameter("employeeId"));	
		Employee desiredEmployee;
		try {
			Integer desiredEmployeeId = Integer.valueOf(request.getParameter("employeeId"));
			desiredEmployee = new Employee(desiredEmployeeId, null, null, null, null, null, null);
			desiredEmployee = EmployeeServiceAlpha.getInstance().getEmployeeInformation(desiredEmployee);
		} catch (NumberFormatException e) {
			desiredEmployee = new Employee(0, null, null, request.getParameter("employeeId"), null, null, null);
			desiredEmployee = EmployeeServiceAlpha.getInstance().getEmployeeInformation(desiredEmployee);
		}
		logger.trace("reimbursement status: "+reimbursementStatus);
		logger.trace("desiredEmployee: "+ desiredEmployee);
		if(loggedEmployee == null) {
			logger.error("No employee logged in");
			return new ClientMessage(GlobalVars.NO_EMPLOYEE);
		}
		if((desiredEmployee == null) && (reimbursementStatus == null || !(reimbursementStatus.equals("PENDING") || reimbursementStatus.equals("RESOLVED")))) {
			logger.error("Invalid status request.");
			return new ClientMessage(GlobalVars.INVALID_REQUEST);
		}
		loggedEmployee = EmployeeServiceAlpha.getInstance().getEmployeeInformation(loggedEmployee);
		if(loggedEmployee.getEmployeeRole().getId() == 2) {
			if(reimbursementStatus != null) {
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
				logger.trace(desiredEmployee);
				Set<Reimbursement> employeeRequests = ReimbursementServiceAlpha.getInstance().getUserPendingRequests(desiredEmployee);
				Set<Reimbursement> resolvedRequests = ReimbursementServiceAlpha.getInstance().getUserFinalizedRequests(desiredEmployee);
				employeeRequests.addAll(resolvedRequests);
				return employeeRequests;
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
		String reimbursementIDString = (String)request.getParameter("reimbursementId");
		String reimbursementStatusType = (String)request.getParameter("reimbursementStatus");
		logger.trace(reimbursementIDString + ", " + reimbursementStatusType);
		if(loggedEmployee == null) {
			logger.error("No employee logged in");
			return new ClientMessage("No Employee");
		} else if(reimbursementIDString == null) {
			logger.error("No reimbursement id inputted as parameter");
			return new ClientMessage("No reimbursement id");
		} else if (reimbursementStatusType == null || (!reimbursementStatusType.toUpperCase().equals("APPROVED") && !reimbursementStatusType.toUpperCase().equals("DECLINED"))) {
			logger.error("Invalid reimbursment status");
			return new ClientMessage("Invalid reimbursement status");
		}
		loggedEmployee = EmployeeServiceAlpha.getInstance().getEmployeeInformation(loggedEmployee);
		Reimbursement reimbursement = ReimbursementServiceAlpha.getInstance().getSingleRequest(new Reimbursement(Integer.valueOf(reimbursementIDString), null, null, 0, null, null, null, null, null));
		if(loggedEmployee.getEmployeeRole().getId() == 2) {
			//Only manager can finalize
			reimbursement.setApprover(loggedEmployee);
			reimbursement.setStatus(new ReimbursementStatus(reimbursementStatusType.toUpperCase()));
			if(ReimbursementServiceAlpha.getInstance().finalizeRequest(reimbursement)) {
				return new ClientMessage("Reimbursement " + reimbursementIDString + " finalized");
			} else {
				return new ClientMessage("Failed to finalize " + reimbursementIDString);
			}
		}
		return null;
	}

	@Override
	public Object getRequestTypes(HttpServletRequest request) {
		return ReimbursementServiceAlpha.getInstance().getReimbursementTypes();
	}

}
