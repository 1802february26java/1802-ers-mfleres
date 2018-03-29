package com.revature.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.revature.ajax.ClientMessage;
import com.revature.model.Employee;
import com.revature.service.EmployeeServiceAlpha;
import com.revature.util.ConnectionUtil;

public class LoginControllerAlpha implements LoginController {

	//logger
		private static Logger logger = Logger.getLogger(ConnectionUtil.class);
		static {
			logger.setLevel(Level.ALL);
		}
	
	/* Singleton */
	private static LoginControllerAlpha loginController = new LoginControllerAlpha();
	private LoginControllerAlpha() {}
	public static LoginController getInstance() {
		return loginController;
	}
	
	
	@Override
	public Object login(HttpServletRequest request) {
		if(request.getMethod() == "GET") {
			logger.trace("login->GET");
			return new ClientMessage("Invalid Login");
		}
		logger.trace("LoginController: logging in...");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		logger.trace("username="+username+" password="+password);
		Employee loggedEmployee = EmployeeServiceAlpha.getInstance().authenticate(
			new Employee(0, null, null, username, password, null, null));
		if(loggedEmployee == null) {
			logger.trace("login->loggedEmployee == null");
			return new ClientMessage("Invalid Login");
		}else {
			request.getSession().setAttribute("loggedEmployee", loggedEmployee);
			logger.trace("Logged into "+loggedEmployee);
			return loggedEmployee;
		}
	}

	@Override
	public String logout(HttpServletRequest request) {
		Employee loggedEmployee = (Employee)request.getSession().getAttribute("loggedEmployee");
		logger.trace("Logging out of Employee: "+loggedEmployee);
		request.getSession().setAttribute("loggedEmployee", null);
		request.getSession().invalidate();
		return "login.html";
	}

}
