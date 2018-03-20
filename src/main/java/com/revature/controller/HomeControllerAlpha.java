package com.revature.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.revature.model.Employee;
import com.revature.util.ConnectionUtil;

public class HomeControllerAlpha implements HomeController {

	//logger
	private static Logger logger = Logger.getLogger(ConnectionUtil.class);
	static {
		logger.setLevel(Level.ALL);
	}
	
	/* Singleton */
	private static HomeControllerAlpha homeController = new HomeControllerAlpha();
	private HomeControllerAlpha() {}
	public static HomeController getInstance() {
		return homeController;
	}
	
	@Override
	public String showEmployeeHome(HttpServletRequest request) {
		logger.trace("show home");
		Employee loggedEmployee = (Employee)request.getSession().getAttribute("loggedEmployee");
		if(loggedEmployee == null) {
			logger.trace("Showing login, no employee currently logged in.");
			return "login.html";
		}
		else if(loggedEmployee.getEmployeeRole().getType().equals("MANAGER")) {
			logger.trace("show home for manager id: "+loggedEmployee.getId());
			return "ManagerHome.html";
		} else {
			logger.trace("show home for employee id: "+loggedEmployee.getId());
			return "EmployeeHome.html";
		}
	}

}
