package project;
import java.time.LocalDate;

/**
 * Represents an expense receipt submitted by a salesperson for reimbursement.
 * Tracks the receipt's lifecycle from submission through handling and final approval/rejection.
 */
public class Receipt {
	
	// Receipt identification and basic information
	private int receiptId;
	private double amount;
	private LocalDate date;
	private String description;
	private String photoPath;      // Path to receipt image
	private String bankPath;        // Path to bank statement document
	private Status status;
	private User submitter;
	
	// Auto-incrementing ID counter for receipts
	private static int counter = 1;
	
	// Accountant handling information
	private User accountantHandled;
	private LocalDate accountantHandledAt;
	
	// Manager decision information
	private User statusByManager;
	private LocalDate statusByManagerAt;
	private String rejectedBc;      // Rejection reason
	
	/**
	 * Creates a new receipt with PENDING status.
	 * 
	 * @param salesperson The user submitting the receipt
	 * @param amount The receipt amount in DKK
	 * @param date The date of the expense
	 * @param description Description of the expense
	 * @param photoPath File path to the receipt image
	 * @param bankPath File path to the bank statement document
	 */
	public Receipt (User salesperson, double amount, LocalDate date, String description, String photoPath, String bankPath) {
		
		this.receiptId = counter++;
		this.amount = amount;
		this.date = date;
		this.description = description;
		this.photoPath = photoPath;
		this.bankPath = bankPath;
		this.status = Status.PENDING;
		this.submitter = salesperson;
	}
	
	/**
	 * Approves this receipt. Can only be called by a manager.
	 * Changes status to APPROVED and records who approved it and when.
	 * 
	 * @param user The manager approving the receipt
	 */
	public void approve(User user) {					
		
		this.status = Status.APPROVED;
		this.statusByManager = user;
		this.statusByManagerAt = LocalDate.now();
		
		System.out.println("Receipt " + this.receiptId + " approved!");
	}
	
	/**
	 * Rejects this receipt with a reason. Can only be called by a manager.
	 * Changes status to REJECTED and records who rejected it, when, and why.
	 * 
	 * @param user The manager rejecting the receipt
	 * @param reason The explanation for rejection
	 */
	public void reject(User user, String reason) {					
		
		this.status = Status.REJECTED;
		this.statusByManager = user;
		this.statusByManagerAt = LocalDate.now();
		this.rejectedBc = reason;
		
		System.out.println("Receipt " + this.receiptId + " rejected.. \nReason: " + this.rejectedBc);
	}
	
	/**
	 * Marks this receipt as handled by an accountant.
	 * Changes status to HANDLED and records who handled it and when.
	 * 
	 * @param user The accountant handling the receipt
	 */
	public void handle(User user) {
		
		this.accountantHandled = user;
		this.accountantHandledAt = LocalDate.now();
		this.status = Status.HANDLED;
		
		System.out.println("Receipt " + this.receiptId + " handled!");
	}
	
	// Getter methods for receipt information
	
	/** @return The rejection reason, or null if not rejected */
	public String getReason() {
		
		return this.rejectedBc;
	}
	
	/** @return The accountant who handled this receipt, or null if not handled */
	public User getAccountant() {
		return this.accountantHandled;
	}
	
	/** @return The date when this receipt was handled by an accountant */
	public LocalDate getAccountantDate() {
		return this.accountantHandledAt;
	}
	
	/** @return The current status of this receipt */
	public Status getStatus() {
		return this.status;
	}
	
	/** @return The unique ID of this receipt */
	public int getReceiptId() {
		return this.receiptId;
	}
	
	/** @return The amount in DKK */
	public double getAmount() {
		return this.amount;
	}
	
	/** @return The date of the expense */
	public LocalDate getDate() {
		return this.date;
	}
	
	/** @return The description of the expense */
	public String getDescription() {
		return this.description;
	}
	
	/** @return The date when manager made a decision on this receipt */
	public LocalDate getStatusByManagerAt() {
		return this.statusByManagerAt;
	}
	
	/** @return The file path to the receipt image */
	public String getPhotoPath() {
		return this.photoPath;
	}

	/** @return The file path to the bank statement document */
	public String getBankPath() {
		return this.bankPath;
	}
	
	/** @return The user who submitted this receipt */
	public User getSubmitter() {
		return this.submitter;
	}
	
	/** @return The manager who approved or rejected this receipt */
	public User getStatusChangedBy() {
		return this.statusByManager;
	}
	
	/** @return The date when the status was changed by a manager */
	public LocalDate statusChangedAt() {
		return this.statusByManagerAt;
	}
	

}
