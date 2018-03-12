package com.revature.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Set;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.revature.model.Employee;
import com.revature.model.Reimbursement;
import com.revature.model.ReimbursementStatus;
import com.revature.model.ReimbursementType;

class ReimbursementJDBS implements ReimbursementRepository{
	
	private static Logger logger = Logger.getLogger(ConnectionUtil.class);
	static {
		logger.setLevel(Level.ALL);
	}
	
	/**
	 * Make singleton
	 */
	private static ReimbursementJDBS reimbursementjdbs = new ReimbursementJDBS();
	private ReimbursementJDBS() {}
	public static ReimbursementJDBS getInstance() {
		return reimbursementjdbs;
	}
	
	public static Blob objectToBlob(Object o) throws IOException, SerialException, SQLException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(byteStream);
		oos.writeObject(o);
		byte byteArray[] = byteStream.toByteArray();
		return new SerialBlob(byteArray);
	}
	
	public static Object blobToObject(Blob b) throws SQLException, IOException, ClassNotFoundException {
		long blobLength = b.length();
		if(blobLength > Integer.MAX_VALUE) {
			return null;
		}
		byte byteArray[] = b.getBytes(0, (int)b.length());
		ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
	    ObjectInputStream ois = new ObjectInputStream(bais);
	    return (Object)ois.readObject();
	}
	
	@Override
	public boolean insert(Reimbursement reimbursement) {
		try(Connection connection = ConnectionUtil.getConnection()){
			String sql = "INSERT INTO REIMBURSEMENT VALUES(?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement statement = connection.prepareStatement(sql);
			int parameterIndex = 0;
			
			//Start adding the parameters
			statement.setInt(++parameterIndex,reimbursement.getId());
			statement.setTimestamp(++parameterIndex, Timestamp.valueOf(reimbursement.getRequested()));
			statement.setTimestamp(++parameterIndex, Timestamp.valueOf(reimbursement.getResolved()));
			statement.setDouble(++parameterIndex, reimbursement.getAmount());
			statement.setString(++parameterIndex, reimbursement.getDescription());
			Blob receiptBlob = objectToBlob(reimbursement.getReceipt());
			statement.setBlob(++parameterIndex, receiptBlob);
			statement.setInt(++parameterIndex, reimbursement.getRequester().getId());
			statement.setInt(++parameterIndex, reimbursement.getApprover().getId());
			statement.setInt(++parameterIndex, reimbursement.getStatus().getId());
			statement.setInt(++parameterIndex, reimbursement.getType().getId());
			
			return (statement.executeUpdate() > 0);
			
		} catch (SQLException e) {
			logger.error("SQLException on insert(reimbursement): "+e);
		} catch (IOException e) {
			logger.error("IOException in insert(reimbursement), likely caused by objectToBlob: "+e);
		}
		return false;
	}
	@Override
	public boolean update(Reimbursement reimbursement) {
		try(Connection connection = ConnectionUtil.getConnection()){
			String sql = "UPDATE REIMBURSEMENT " +
					"R_REQUESTED= ?, " + 
					"R_RESOLVED = ?, " + 
					"R_AMOUNT = ?, " + 
					"R_DESCRIPTION = ?, " + 
					"R_RECEIPT = ?, " + 
					"EMPLOYEE_ID = ?, " + 
					"MANAGER_ID = ?, " + 
					"RS_ID = ?, " + 
					"RT_ID = ? " +
					"WHERE R_ID = ?;";
			PreparedStatement statement = connection.prepareStatement(sql);
			
			//UPDATE ALL COLUMNS
			int parameterIndex = 0;
			statement.setTimestamp(++parameterIndex, Timestamp.valueOf(reimbursement.getRequested()));
			statement.setTimestamp(++parameterIndex, Timestamp.valueOf(reimbursement.getResolved()));
			statement.setDouble(++parameterIndex, reimbursement.getAmount());
			statement.setString(++parameterIndex, reimbursement.getDescription());
			Blob receiptBlob = objectToBlob(reimbursement.getReceipt());
			statement.setBlob(++parameterIndex, receiptBlob);
			statement.setInt(++parameterIndex, reimbursement.getRequester().getId());
			statement.setInt(++parameterIndex, reimbursement.getApprover().getId());
			statement.setInt(++parameterIndex, reimbursement.getStatus().getId());
			statement.setInt(++parameterIndex, reimbursement.getType().getId());
			
			statement.setInt(++parameterIndex,reimbursement.getId()); //WHERE CLAUSE
			return (statement.executeUpdate() > 0);
			
		} catch (SQLException e) {
			logger.error("SQLException on update(reimbursement): "+e);

		} catch (IOException e) {
			logger.error("IOException in update(reimbursement), likely caused by objectToBlob: "+e);
		}
		return false;
	}
	
	private ReimbursementStatus queryStatus(int statusId) throws SQLException {
		Connection connection = ConnectionUtil.getConnection();
		String sql = "SELECT RS_STATUS FROM REIMBURSEMENT_STATUS WHERE RS_ID = ?";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setInt(1, statusId);
		ResultSet result = statement.executeQuery();
		if(result.next()) {
			return new ReimbursementStatus(statusId, result.getString("RS_STATUS"));
		}
		return null;
	}
	
	private ReimbursementType queryType(int typeId) throws SQLException {
		Connection connection = ConnectionUtil.getConnection();
		String sql = "SELECT RT_TYPE FROM REIMBURSEMENT_TYPE WHERE RT_ID = ?";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setInt(1, typeId);
		ResultSet result = statement.executeQuery();
		if(result.next()) {
			return new ReimbursementType(typeId, result.getString("RT_STATUS"));
		}
		return null;
	}
	
	@Override
	public Reimbursement select(int reimbursementId) {
		
		try (Connection connection = ConnectionUtil.getConnection()){
			String sql = "SELECT * FROM REIMBURSEMENT WHERE R_ID = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, reimbursementId);
			ResultSet result = statement.executeQuery();
			
			if(result.next()) {
				try {
					LocalDateTime requested = result.getTimestamp("R_REQUESTED").toLocalDateTime();
					LocalDateTime resolved = result.getTimestamp("R_RESOLVED").toLocalDateTime();
					double amount = result.getDouble("R_AMOUNT");
					String description = result.getString("R_DESCRIPTION");
					Blob receiptBlob = result.getBlob("R_RECEIPT");
					int requesterId = result.getInt("EMPLOYEE_ID");
					int approverId = result.getInt("MANAGER_ID");
					int statusId = result.getInt("RS_ID");
					int typeId = result.getInt("RT_ID");
					
					//Need to convert data types for receipt, requester, approver, status, and type
					Object receipt = blobToObject(receiptBlob);
					ReimbursementStatus  status = queryStatus(statusId);
					ReimbursementType type = queryType(typeId);
					Employee requester = EmployeeRepositoryJDBS.getInstance().select(requesterId);
					Employee approver = EmployeeRepositoryJDBS.getInstance().select(approverId);

					Reimbursement r = new Reimbursement(reimbursementId, requested, resolved, amount, description, requester, approver, status, type);
					r.setReceipt(receipt);
					return r;
					
				} catch (SQLException e) {
					logger.error("SQLException during the reading of the query " + e);
				} catch (ClassNotFoundException e) {
					logger.error("ClassNotFoundException, could not determine Object of Blob " + e);
				} catch (IOException e) {
					logger.error("IOException on select(reimbursementId " + e);
				}
			} else {
				logger.error("Reimbursement with ID "+reimbursementId+" not found.");
			}
		} catch (SQLException e) {
			logger.error("SQLException on select(reimbursementId): "+e);
		}
		return null;
	}
	@Override
	public Set<Reimbursement> selectPending(int employeeId) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Set<Reimbursement> selectFinalized(int employeeId) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Set<Reimbursement> selectAllPending() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Set<Reimbursement> selectAllFinalized() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Set<ReimbursementType> selectTypes() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}