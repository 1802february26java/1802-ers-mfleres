package com.revature.service;

import java.time.LocalDateTime;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.revature.model.Employee;
import com.revature.model.Reimbursement;
import com.revature.model.ReimbursementStatus;
import com.revature.model.ReimbursementType;
import com.revature.repository.EmployeeJDBS;
import com.revature.repository.ReimbursementJDMS;
import com.revature.util.ConnectionUtil;

public class ReimbursementServiceAlpha implements ReimbursementService {

	private static Logger logger = Logger.getLogger(ConnectionUtil.class);
	static {
		logger.setLevel(Level.ALL);
	}
	
	// Make Singleton
	private static ReimbursementServiceAlpha reimbursementService = new ReimbursementServiceAlpha();
	private ReimbursementServiceAlpha() {}
	public static ReimbursementService getInstance() {
		return reimbursementService;
	}
	
	@Override
	public boolean submitRequest(Reimbursement reimbursement) {
		Employee requestor = reimbursement.getRequester();
		Employee loggedRequestor = EmployeeJDBS.getInstance().select(requestor.getId());
		if(loggedRequestor == null) {
			logger.error("Requesting employee does not exist");
			return false;
		}
		
		Reimbursement loggedReimbursement = ReimbursementJDMS.getInstance().select(reimbursement.getId());
		if (loggedReimbursement != null) {
			logger.trace("Reimbursement with that id already exists in the database");
			return false;
		}
		if(reimbursement.getStatus() == null) {
			reimbursement.setStatus(new ReimbursementStatus(1, "PENDING"));
		}
		if(reimbursement.getType() == null) {
			reimbursement.setType(new ReimbursementType(ReimbursementType.Types.OTHER));
		}
		reimbursement.setRequested(LocalDateTime.now());
		reimbursement.setResolved(null);
		return ReimbursementJDMS.getInstance().insert(reimbursement);
	}

	@Override
	public boolean finalizeRequest(Reimbursement reimbursement) {
		Reimbursement loggedReimbursement = ReimbursementJDMS.getInstance().select(reimbursement.getId());
		if(loggedReimbursement == null) {
			logger.error("Reimbursement not found in the database");
			return false;
		}
		if(reimbursement.getStatus().getStatus() == "APPROVED" || reimbursement.getStatus().getStatus() == "DECLINED") {
			loggedReimbursement.setStatus(reimbursement.getStatus());
		} else {
			logger.error("Reimbursement has not been finalized.");
			return false;
		}
		if(reimbursement.getApprover() == null) {
			logger.error("Reimbursement has no approver.");
			return false;
		} else {
			loggedReimbursement.setApprover(reimbursement.getApprover());
		}
		loggedReimbursement.setResolved(LocalDateTime.now());
		
		// TODO Add email (Optional)
		return ReimbursementJDMS.getInstance().update(loggedReimbursement);
	}

	@Override
	public Reimbursement getSingleRequest(Reimbursement reimbursement) {
		return ReimbursementJDMS.getInstance().select(reimbursement.getId());
	}

	@Override
	public Set<Reimbursement> getUserPendingRequests(Employee employee) {
		return ReimbursementJDMS.getInstance().selectPending(employee.getId());
	}

	@Override
	public Set<Reimbursement> getUserFinalizedRequests(Employee employee) {
		return ReimbursementJDMS.getInstance().selectFinalized(employee.getId());
	}

	@Override
	public Set<Reimbursement> getAllPendingRequests() {
		return ReimbursementJDMS.getInstance().selectAllPending();
	}

	@Override
	public Set<Reimbursement> getAllResolvedRequests() {
		return ReimbursementJDMS.getInstance().selectAllFinalized();
	}

	@Override
	public Set<ReimbursementType> getReimbursementTypes() {
		return ReimbursementJDMS.getInstance().selectTypes();
	}

}
