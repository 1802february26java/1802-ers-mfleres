package com.revature.request;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.revature.controller.EmployeeInformationController;
import com.revature.controller.EmployeeInformationControllerAlpha;
import com.revature.controller.ErrorControllerAlpha;
import com.revature.controller.HomeControllerAlpha;
import com.revature.controller.LoginControllerAlpha;
import com.revature.controller.ReimbursementControllerAlpha;
import com.revature.util.ConnectionUtil;

/**
 * The RequestHelper class is consulted by the MasterServlet and provides
 * him with a view URL or actual data that needs to be transferred to the
 * client.
 * 
 * It will execute a controller method depending on the requested URI.
 * 
 * Recommended to change this logic to consume a ControllerFactory.
 * 
 * @author Revature LLC
 */
public class RequestHelper {
	private static RequestHelper requestHelper;
	//logger
	private static Logger logger = Logger.getLogger(ConnectionUtil.class);
	static {
		logger.setLevel(Level.ALL);
	}

	private RequestHelper() {}

	public static RequestHelper getRequestHelper() {
		if(requestHelper == null) {
			return new RequestHelper();
		}
		else {
			return requestHelper;
		}
	}
	
	/**
	 * Checks the URI within the request object passed by the MasterServlet
	 * and executes the right controller with a switch statement.
	 * 
	 * @param request
	 * 		  The request object which contains the solicited URI.
	 * @return A String containing the URI where the user should be
	 * forwarded, or data (any object) for AJAX requests.
	 */
	public Object process(HttpServletRequest request) {
		request.setAttribute("status", null);
		switch(request.getRequestURI())
		{
		case "/ERS/home.do":
			logger.trace("RequestHelper: home.do");
			return HomeControllerAlpha.getInstance().showEmployeeHome(request);
		case "/ERS/login.do":
			logger.trace("RequestHelper: login.do");
			return LoginControllerAlpha.getInstance().login(request);
		case "/ERS/logout.do":
			logger.trace("RequestHelper: logout.do");
			return LoginControllerAlpha.getInstance().logout(request);
		case "/ERS/viewPending.do":
			logger.trace("RequestHelper: viewPending.do");
			request.setAttribute("status", "PENDING");
			return ReimbursementControllerAlpha.getInstance().multipleRequests(request);
		case "/ERS/viewResolved.do":
			logger.trace("RequestHelper: viewResolved.do");
			request.setAttribute("status", "RESOLVED");
			return ReimbursementControllerAlpha.getInstance().multipleRequests(request);
		case "/ERS/viewEmployeeReimbursements.do":
			logger.trace("RequestHelper: viewEmployeeReimbursements.do");
			return ReimbursementControllerAlpha.getInstance().multipleRequests(request);
		case "/ERS/viewAll.do":
			logger.trace("RequestHelper: viewAll.do");
			return EmployeeInformationControllerAlpha.getInstance().viewAllEmployees(request);
		case "/ERS/resolveReimbursement.do":
			return ReimbursementControllerAlpha.getInstance().finalizeRequest(request);
		case "/ERS/reimbursementTypes.do":
			return ReimbursementControllerAlpha.getInstance().getRequestTypes(request);
		case "/ERS/submitReimbursement.do":
			return ReimbursementControllerAlpha.getInstance().submitRequest(request);
		case "/ERS/updateEmployee.do":
			return EmployeeInformationControllerAlpha.getInstance().updateEmployee(request);
		default:
			logger.trace("RequestHelper: default");
			return HomeControllerAlpha.getInstance().showEmployeeHome(request);
		}
	}
}
