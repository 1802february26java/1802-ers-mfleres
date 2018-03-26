package com.revature.service;

import java.util.Set;

import com.revature.model.Employee;
import com.revature.model.EmployeeToken;
import com.revature.repository.EmployeeJDBS;

public class EmployeeServiceAlpha implements EmployeeService{
	
	// Singleton
	private static EmployeeServiceAlpha employeeService = new EmployeeServiceAlpha();
	private EmployeeServiceAlpha() {}
	public static EmployeeService getInstance() {
		return employeeService;
	}
	
	
	@Override
	public Employee authenticate(Employee employee) {
		Employee loggedEmployee = EmployeeJDBS.getInstance().select(employee.getUsername());
		String hashedPassword = EmployeeJDBS.getInstance().getPasswordHash(employee);
		
		if(loggedEmployee.getPassword().equals(hashedPassword)) {
			return loggedEmployee;
		}
		return null;
	}

	@Override
	public Employee getEmployeeInformation(Employee employee) {
		Employee selectedEmployee = EmployeeJDBS.getInstance().select(employee.getId());
		if(selectedEmployee != null) {
			return selectedEmployee;
		} else {
			return EmployeeJDBS.getInstance().select(employee.getUsername());
		}
	}

	@Override
	public Set<Employee> getAllEmployeesInformation() {
		return EmployeeJDBS.getInstance().selectAll();
	}

	@Override
	public boolean createEmployee(Employee employee) {
		return EmployeeJDBS.getInstance().insert(employee);
	}

	@Override
	public boolean updateEmployeeInformation(Employee employee) {
		return EmployeeJDBS.getInstance().update(employee);
	}

	@Override
	public boolean updatePassword(Employee employee) {
		String hashedPassword = EmployeeJDBS.getInstance().getPasswordHash(employee);
		Employee loggedEmployee = EmployeeJDBS.getInstance().select(employee.getId());
		loggedEmployee.setPassword(hashedPassword);
		return EmployeeJDBS.getInstance().update(loggedEmployee);
	}

	@Override
	public boolean isUsernameTaken(Employee employee) {
		Employee loggedEmployee = EmployeeJDBS.getInstance().select(employee.getUsername());
		return loggedEmployee != null;
	}

	@Override
	public boolean createPasswordToken(Employee employee) {
		// TODO Auto-generated method stub (OPTIONAL FEATURE)
		return false;
	}

	@Override
	public boolean deletePasswordToken(EmployeeToken employeeToken) {
		// TODO Auto-generated method stub (OPTIONAL FEATURE)
		return false;
	}

	@Override
	public boolean isTokenExpired(EmployeeToken employeeToken) {
		// TODO Auto-generated method stub (OPTIONAL FEATURE)
		return false;
	}

}
