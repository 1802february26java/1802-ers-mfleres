package com.revature.controller;

import javax.servlet.http.HttpServletRequest;

import com.revature.model.Employee;
import com.revature.service.EmployeeServiceAlpha;

public class LoginControllerAlpha implements LoginController {

	/* Singleton */
	private static LoginControllerAlpha loginController = new LoginControllerAlpha();
	private LoginControllerAlpha() {}
	public static LoginController getInstance() {
		return loginController;
	}
	
	
	@Override
	public String login(HttpServletRequest request) {
		if(request.getMethod() == "GET") {
			return "login.html";
		}
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		Employee loggedEmployee = EmployeeServiceAlpha.getInstance().authenticate(
			new Employee(0, null, null, username, password, null, null));
		if(loggedEmployee == null) {
			return "login.html";
		}else {
			request.getSession().setAttribute("loggedEmployee", loggedEmployee);
			if(loggedEmployee.getEmployeeRole().getType().equals("Manager")) {
				return "ManagerHome.html";
			} else {
				return "EmployeeHome.html";
			}
		}
	}

	@Override
	public String logout(HttpServletRequest request) {
		return "login.html";
	}

}
