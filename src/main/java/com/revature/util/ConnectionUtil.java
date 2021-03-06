package com.revature.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
//import oracle.jdbc.driver.*;

/**
 * Utility class to obtain a connection object.
 *
 */
public class ConnectionUtil {
	
	private static Logger logger = Logger.getLogger(ConnectionUtil.class);
	static {
		logger.setLevel(Level.ALL);
	}
	
	static {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			logger.warn("Exception thrown adding oracle driver.", e);
		}
	}

	public static Connection getConnection() throws SQLException {
		
		String url = "jdbc:oracle:thin:@myrevaturerds.cwkouucgucbd.us-east-1.rds.amazonaws.com:1521:ORCL";
		String username = "ERS_DB";
		String password = "myErsP4ssw0rd";
		
		return DriverManager.getConnection(url, username, password);
	}

	/** Test that connection is working **/
	public static void main(String[] args) {
		/*
		 * Try with resources will close resources automatically
		 * 
		 * Classes in this kind of try's need to implement AutoCloseable
		 */
		try(Connection connection = ConnectionUtil.getConnection()) {
			logger.info("Connection successful");
		} catch (SQLException e) {
			logger.error("Couldn't connect to the database", e);
		}
	}
}
