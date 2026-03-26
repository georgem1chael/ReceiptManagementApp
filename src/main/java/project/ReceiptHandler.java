package project;
import java.time.LocalDate;
import java.util.*;

/**
 * Manages receipt operations and enforces business rules.
 * Handles receipt creation, approval, rejection, and role-based filtering.
 */
public class ReceiptHandler{
	
	// List of all receipts in the system
	private List<Receipt> receipts;
	// Permission checker for authorization
	private PermissionHandler permissionHandler;
	
	/**
	 * Creates a new ReceiptHandler with the specified permission handler.
	 * 
	 * @param permissionHandler The handler for checking user permissions
	 */
	public ReceiptHandler(PermissionHandler permissionHandler) {
		
		this.permissionHandler = permissionHandler;
		this.receipts = new ArrayList<>();
		
	}
	
	/**
	 * Creates a new receipt and adds it to the system.
	 * 
	 * @param user The user submitting the receipt
	 * @param amount The receipt amount in DKK
	 * @param date The date of the expense
	 * @param description Description of the expense
	 * @param photoPath Path to the receipt image file
	 * @param bankPath Path to the bank statement file
	 */
	public void createReceipt(User user, double amount, LocalDate date, String description, String photoPath, String bankPath) {
		
		Receipt newReceipt = new Receipt(user, amount, date, description, photoPath, bankPath);
		
		receipts.add(newReceipt);
	}
	
	/**
	 * Approves a receipt. Only managers can approve receipts that have been handled.
	 * 
	 * @param user The manager approving the receipt
	 * @param receiptId The ID of the receipt to approve
	 * @throws IllegalArgumentException if receipt not found
	 * @throws SecurityException if receipt not handled or user lacks permission
	 */
	public void approveReceipt(User user, int receiptId) {
		Receipt rec = findReceiptById(receiptId);
		if (rec == null) {
			throw new IllegalArgumentException("Receipt " + receiptId + " not found");
		}

		if (rec.getAccountant() == null) {
			throw new SecurityException("Can't approve receipt if it hasn't been handled by an Accountant yet!");
		}

		if (!permissionHandler.canApprove(user, rec)) {
			throw new SecurityException("User " + user.getUsername() + " (" + user.getRole() + ") is not allowed to approve receipts.");
		}

		rec.approve(user);
	}
	
	/**
	 * Rejects a receipt with a reason. Only managers can reject receipts that have been handled.
	 * 
	 * @param user The manager rejecting the receipt
	 * @param receiptId The ID of the receipt to reject
	 * @param reason The explanation for rejection
	 * @throws IllegalArgumentException if receipt not found
	 * @throws SecurityException if receipt not handled or user lacks permission
	 */
	public void rejectReceipt(User user, int receiptId, String reason) {
		Receipt rec = findReceiptById(receiptId);
		if (rec == null) {
			throw new IllegalArgumentException("Receipt " + receiptId + " not found");
		}

		if (rec.getAccountant() == null) {
			throw new SecurityException("Can't reject receipt if it hasn't been handled by an Accountant yet!");
		}

		if (!permissionHandler.canApprove(user, rec)) {
			throw new SecurityException("User " + user.getUsername() + " (" + user.getRole() + ") is not allowed to approve receipts.");
		}

		rec.reject(user, reason);
	}
	
	/**
	 * Marks a receipt as handled by an accountant.
	 * 
	 * @param user The accountant handling the receipt
	 * @param receiptId The ID of the receipt to handle
	 * @throws IllegalArgumentException if receipt not found
	 * @throws SecurityException if user lacks permission to handle receipts
	 */
	public void handleReceipt(User user, int receiptId) {
		Receipt rec = findReceiptById(receiptId);
		if (rec == null) {
			throw new IllegalArgumentException("Receipt " + receiptId + " not found");
		}

		if (!permissionHandler.canHandle(user, rec)) {
			throw new SecurityException("User " + user.getUsername() + " (" + user.getRole() + ") is not allowed to handle receipts.");
		}

		rec.handle(user);
	}
	
	/**
	 * Returns all receipts in the system.
	 * Used by administrators to view all receipts.
	 * 
	 * @return List of all receipts
	 */
	public List<Receipt> listReceipts() {
		
		return receipts;
	}

	/**
	 * Returns receipts visible to a specific user based on their role.
	 * - SALESPERSON: Only their own receipts
	 * - ACCOUNTANT: Pending receipts and receipts they've handled
	 * - MANAGER: All handled, approved, and rejected receipts (except their own)
	 * 
	 * @param user The user requesting the receipt list
	 * @return List of receipts the user is allowed to see
	 * @throws SecurityException if user is an administrator (should use listReceipts instead)
	 */
	public List<Receipt> listReceipt(User user){

		List<Receipt> newList = new ArrayList<>();

		if(user.getRole() == Role.SALESPERSON){
			
			for(Receipt r : receipts){
				
				if(r.getSubmitter().equals(user))
					newList.add(r);
			}

			return newList;
		}

		else if(user.getRole() == Role.ACCOUNTANT){
			
			// Return all receipts that an accountant can see/work with
			for(Receipt r : receipts){
				
				// Pending receipts (not yet handled) or receipts this accountant has handled
				if(r.getStatus() == Status.PENDING || 
				   (r.getAccountant() != null && r.getAccountant().equals(user))) {
					newList.add(r);
				}
			}

			return newList;
		}

		else if(user.getRole() == Role.MANAGER){
			
			// Return all receipts that managers can see/work with
			for(Receipt r : receipts){
				
				// All handled receipts (ready for review) and receipts already approved/rejected
				if((r.getStatus() == Status.HANDLED || r.getStatus() == Status.APPROVED || r.getStatus() == Status.REJECTED) 
				   && !r.getSubmitter().equals(user)) {
					newList.add(r);
				}
			}

			return newList;
		}

		throw new SecurityException("Administrators can't view a comprehensive Receipt list!");
	}
	
	/**
	 * Finds a receipt by its ID.
	 * 
	 * @param receiptId The ID of the receipt to find
	 * @return The Receipt object if found, null otherwise
	 */
	public Receipt findReceiptById(int receiptId) {
		
	    for (Receipt r : receipts) {
	    	
	        if (r.getReceiptId() == receiptId) {
	            return r;
	        }
	    }
	   
	    System.out.println("Receipt " + receiptId + " not found");
	    return null;
	    
	    }
	
}


