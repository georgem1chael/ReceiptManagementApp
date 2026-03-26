package project;

/**
 * Handles permission checks for receipt operations.
 * Enforces business rules about who can perform which actions on receipts.
 */
public class PermissionHandler {
	
	/**
	 * Checks if a user has permission to approve or reject a receipt.
	 * Only managers can approve, and they cannot approve their own receipts.
	 * 
	 * @param user The user attempting to approve/reject
	 * @param rec The receipt being approved/rejected
	 * @return true if the user can approve, false otherwise
	 */
	public boolean canApprove(User user, Receipt rec) {
		
		if( user.getRole() == Role.SALESPERSON || user.getRole() == Role.ACCOUNTANT || user.getRole() == Role.ADMIN)
			return false;
		
		if( rec.getSubmitter() == user)
			return false;
		
		return true;
	}
	
	/**
	 * Checks if a user has permission to handle (review) a receipt.
	 * Only accountants can handle receipts, and they cannot handle their own receipts.
	 * 
	 * @param user The user attempting to handle the receipt
	 * @param rec The receipt being handled
	 * @return true if the user can handle the receipt, false otherwise
	 */
	public boolean canHandle(User user, Receipt rec) {
		
		if( user.getRole() != Role.ACCOUNTANT)
			return false;
		
		if( rec.getSubmitter() == user)
			return false;
		
		return true;
	}
	
}
