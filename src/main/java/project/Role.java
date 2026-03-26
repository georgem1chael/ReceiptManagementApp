package project;

/**
 * Defines the different roles users can have in the receipt management system.
 * Each role has different permissions and access levels.
 */
public enum Role {
	
	/** Submits receipts for reimbursement */
	SALESPERSON,
	
	/** Reviews and marks receipts as handled */
	ACCOUNTANT,
	
	/** Approves or rejects handled receipts */
	MANAGER,
	
	/** Full system access including user management */
	ADMIN

}
