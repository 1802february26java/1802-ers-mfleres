package com.revature.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.revature.ajax.ClientMessage;
import com.revature.model.Employee;
import com.revature.service.EmployeeServiceAlpha;
import com.revature.util.ConnectionUtil;

public class EmployeeInformationControllerAlpha implements EmployeeInformationController {

	//logger
	private static Logger logger = Logger.getLogger(ConnectionUtil.class);
	static {
		logger.setLevel(Level.ALL);
	}

	//Singleton
	private static EmployeeInformationControllerAlpha employeeInformationControllerAlpha = new EmployeeInformationControllerAlpha();
	private EmployeeInformationControllerAlpha() {}
	public static EmployeeInformationController getInstance() {
		return employeeInformationControllerAlpha;
	}
	
	@Override
	public Object registerEmployee(HttpServletRequest request) {
		return null;
	}

	@Override
	public Object updateEmployee(HttpServletRequest request) {
		String newFirstName = request.getParameter("firstName");
		String newLastName = request.getParameter("lastName");
		String newEmail = request.getParameter("email");
		Employee loggedEmployee = (Employee)request.getSession().getAttribute("loggedEmployee");
		if(newFirstName != null) {
			loggedEmployee.setFirstName(newFirstName);
		}
		if(newLastName != null) {
			loggedEmployee.setLastName(newLastName);
		}
		if(newEmail != null) {
			loggedEmployee.setEmail(newEmail);
		}
		if(EmployeeServiceAlpha.getInstance().updateEmployeeInformation(loggedEmployee)) {
			return new ClientMessage("SUCCESS");
		} else {
			return new ClientMessage("FAILURE");
		}
	}

	@Override
	public Object viewEmployeeInformation(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object viewAllEmployees(HttpServletRequest request) {
		logger.trace("View all employees");
		Employee loggedEmployee = (Employee)request.getSession().getAttribute("loggedEmployee");
		if(loggedEmployee == null) {
			logger.error("No employee logged in");
			return null;
		}
		loggedEmployee = EmployeeServiceAlpha.getInstance().getEmployeeInformation(loggedEmployee);
		if(loggedEmployee == null || !loggedEmployee.getEmployeeRole().getType().equals("MANAGER")) {
			logger.error("Invalid employee");
			return null;
		}
		return EmployeeServiceAlpha.getInstance().getAllEmployeesInformation();
	}

	@Override
	public Object usernameExists(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}
