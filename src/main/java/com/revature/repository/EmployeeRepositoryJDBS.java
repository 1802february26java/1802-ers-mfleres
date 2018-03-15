package com.revature.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.revature.model.Employee;
import com.revature.model.EmployeeRole;
import com.revature.model.EmployeeToken;

public class EmployeeRepositoryJDBS implements EmployeeRepository{
	
	private static Logger logger = Logger.getLogger(ConnectionUtil.class);
	static {
		logger.setLevel(Level.ALL);
	}
	
	/**
	 * Make singleton
	 */
	private static EmployeeRepositoryJDBS employeeRepositoryJDBS = new EmployeeRepositoryJDBS();
	private EmployeeRepositoryJDBS() {}
	public static EmployeeRepositoryJDBS getInstance() {
		return employeeRepositoryJDBS;
	}

	@Override
	public boolean insert(Employee employee) {
		if(employee == null) {
			return false;
		}
		try(Connection connection = ConnectionUtil.getConnection()){
			String sql = "INSERT INTO USER_T VALUES(?,?,?,?,?,?,?)";
			PreparedStatement statement = connection.prepareStatement(sql);
			int parameterIndex = 0;
			statement.setInt(++parameterIndex, employee.getId());
			statement.setString(++parameterIndex, employee.getFirstName());
			statement.setString(++parameterIndex, employee.getLastName());
			statement.setString(++parameterIndex, employee.getUsername());
			statement.setString(++parameterIndex, employee.getPassword());
			statement.setString(++parameterIndex, employee.getEmail());
			statement.setInt(++parameterIndex, employee.getEmployeeRole().getId());
			
			return statement.executeUpdate() > 0;
			
		} catch (SQLException e) {
			logger.error("SQLException in insert(Employee)" + e);
		}
		return false;
	}

	@Override
	public boolean update(Employee employee) {
		if(employee == null) {
			return false;
		}
		try(Connection connection = ConnectionUtil.getConnection()){
			logger.trace("update(Employee); connected");
			String sql = "UPDATE USER_T SET "
					+ "U_FIRSTNAME = ?, "
					+ "U_LASTNAME = ?, "
					+ "U_USERNAME = ?, "
					+ "U_PASSWORD = ?, "
					+ "U_EMAIL = ?, "
					+ "UR_ID = ?"
					+ "WHERE U_ID = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			int parameterIndex = 0;
			statement.setString(++parameterIndex, employee.getFirstName());
			statement.setString(++parameterIndex, employee.getLastName());
			statement.setString(++parameterIndex, employee.getUsername());
			statement.setString(++parameterIndex, employee.getPassword());
			statement.setString(++parameterIndex, employee.getEmail());
			statement.setInt(++parameterIndex, employee.getEmployeeRole().getId());
			statement.setInt(++parameterIndex, employee.getId());
			logger.trace("Updating: "+employee);
			return statement.executeUpdate() > 0;
			
			
		} catch (SQLException e) {
			logger.error("SQLException in update(Employee) "+e);
		}
		return false;
	}

	private Employee parseEmployeeFromResultSet(ResultSet result) {
		try (Connection connection = ConnectionUtil.getConnection()) {
			int id = result.getInt("U_ID");
			String firstName = result.getString("U_FIRSTNAME");
			String lastName = result.getString("U_LASTNAME");
			String username = result.getString("U_USERNAME");
			String password = result.getString("U_PASSWORD");
			String email = result.getString("U_EMAIL");
			int roleId = result.getInt("UR_ID");
			String roleType = null;
			
			//Make EmployeeRole object
			String sql = "SELECT UR_TYPE FROM USER_ROLE WHERE UR_ID = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, roleId);
			ResultSet roleResult = statement.executeQuery();
			if(roleResult.next()) {
				roleType = roleResult.getString("UR_TYPE");
			} else {
				return null;
			}
			EmployeeRole employeeRole = new EmployeeRole(roleId, roleType);
			
			return new Employee(id, firstName, lastName, username, password, email, employeeRole);
			
		} catch (SQLException e) {
			logger.error("SQLException in parseEmployeeFromResultSet(ResultSet) " + e);
		}
		return null;
	}
	
	@Override
	public Employee select(int employeeId) {
		try (Connection connection = ConnectionUtil.getConnection()){
			String sql = "SELECT * FROM USER_T WHERE U_ID = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, employeeId);
			ResultSet result = statement.executeQuery();
			if(result.next()) {
				return parseEmployeeFromResultSet(result);
			} else {
				return null;
			}
		} catch (SQLException e) {
			logger.error("SQLException in select(int) " + e);
		}
		return null;
	}

	@Override
	public Employee select(String username) {
		try (Connection connection = ConnectionUtil.getConnection()){
			String sql = "SELECT * FROM USER_T WHERE U_USERNAME = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, username);
			ResultSet result = statement.executeQuery();
			if(result.next()) {
				return parseEmployeeFromResultSet(result);
			} else {
				return null;
			}
		} catch (SQLException e) {
			logger.error("SQLException in select(String) " + e);
		}
		return null;
	}

	@Override
	public Set<Employee> selectAll() {
		try (Connection connection = ConnectionUtil.getConnection()){
			HashSet<Employee> employeeSet = new HashSet<>();
			String sql = "SELECT * FROM USER_T";
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while (result.next()) {
				employeeSet.add(parseEmployeeFromResultSet(result));
			}
			return employeeSet;
		} catch (SQLException e) {
			logger.error("SQLException in selectAll() " + e);
		}
		return null;
	}

	@Override
	public String getPasswordHash(Employee employee) {
		if(employee != null) {
			return employee.getPassword();
		} else {
			return null;
		}
	}

	@Override
	public boolean insertEmployeeToken(EmployeeToken employeeToken) {
		if(employeeToken == null) {
			return false;
		}
		try(Connection connection = ConnectionUtil.getConnection()){
			String sql = "INSERT INTO PASSWORD_RECOVERY VALUES(?,?,?,?)";
			PreparedStatement statement = connection.prepareStatement(sql);
			int parameterIndex = 0;
			statement.setInt(++parameterIndex, employeeToken.getId());
			statement.setString(++parameterIndex, employeeToken.getToken());
			statement.setTimestamp(++parameterIndex, Timestamp.valueOf(employeeToken.getCreationDate()));
			statement.setInt(++parameterIndex, employeeToken.getRequester().getId());
			
			return statement.executeUpdate() > 0;
		} catch (SQLException e) {
			logger.error("SQLException in insertEmployeeToken(EmployeeToken) " + e);
		}
		return false;
	}

	@Override
	public boolean deleteEmployeeToken(EmployeeToken employeeToken) {
		if(employeeToken == null) {
			return false;
		}
		try(Connection connection = ConnectionUtil.getConnection()){
			String sql = "DELETE FROM PASSWORD_RECOVERY WHERE PR_ID = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			int parameterIndex = 0;
			statement.setInt(++parameterIndex, employeeToken.getId());
			
			return statement.executeUpdate() > 0;
			
		} catch (SQLException e) {
			logger.error("SQLException in insertEmployeeToken(EmployeeToken) " + e);
		}
		return false;
	}

	@Override
	public EmployeeToken selectEmployeeToken(EmployeeToken employeeToken) {
		// Assuming we can't just do "return employeeToken; ...
		if(employeeToken == null) {
			return null;
		}
		try(Connection connection = ConnectionUtil.getConnection()){
			int id = employeeToken.getId();
			String sql = "SELECT * FROM PASSWORD_RECOVERY WHERE PR_ID = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, id);
			ResultSet result = statement.executeQuery();
			if(result.next()) {
				String token = result.getString("PR_TOKEN");
				LocalDateTime creationDate = result.getTimestamp("PR_TIME").toLocalDateTime();
				int requesterId = result.getInt("U_ID");
				Employee requester = select(requesterId);
				
				if(requester != null) {
					return new EmployeeToken(id, token, creationDate, requester);
				}
			}
		} catch (SQLException e) {
			logger.error("SQLException in insertEmployeeToken(EmployeeToken) " + e);
		}
		return null;
	}

}
