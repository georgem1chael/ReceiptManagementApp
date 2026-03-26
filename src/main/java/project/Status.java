package project;

/**
 * Defines the possible states of a receipt in its lifecycle.
 * Receipts progress from PENDING → HANDLED → APPROVED/REJECTED.
 */
public enum Status {
	
	/** Receipt has been approved by a manager */
	APPROVED,
	
	/** Receipt has been rejected by a manager */
	REJECTED,
	
	/** Receipt submitted but not yet reviewed by an accountant */
	PENDING, 
	
	/** Receipt reviewed by accountant, awaiting manager decision */
	HANDLED
}
