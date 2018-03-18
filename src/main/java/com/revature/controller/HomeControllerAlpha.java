package com.revature.controller;

import javax.servlet.http.HttpServletRequest;

import com.revature.model.Employee;

public class HomeControllerAlpha implements HomeController {

	/* Singleton */
	private static HomeControllerAlpha homeController = new HomeControllerAlpha();
	private HomeControllerAlpha() {}
	public static HomeController getInstance() {
		return homeController;
	}
	
	@Override
	public String showEmployeeHome(HttpServletRequest request) {
		Employee loggedEmployee = (Employee)request.getSession().getAttribute("loggedEmployee");
		if(loggedEmployee == null) {
			return "login.html";
		}
		else if(loggedEmployee.getEmployeeRole().getId() == 2) {
			return "ManagerHome.html";
		} else {
			return "EmployeeHome.html";
		}
	}

}
