package com.revature.controller;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.revature.model.Employee;
import com.revature.model.Reimbursement;
import com.revature.model.ReimbursementType;
import com.revature.repository.ConnectionUtil;
import com.revature.service.ReimbursementService;
import com.revature.service.ReimbursementServiceAlpha;

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
		// TODO Finish
		int reimbursementId = (int)request.getAttribute("id");
		return null;
	}

	@Override
	public Object multipleRequests(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object finalizeRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getRequestTypes(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}
