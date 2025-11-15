import java.time.LocalDate;

public class Receipt {
	
	private int receiptId;
	private double amount;
	private LocalDate date;
	private String description;
	private String photoPath;
	private Status status;
	private User submitter;
	
	private static int counter = 1;
	
	private User accountantHandled;
	private LocalDate accountantHandledAt;
	
	private User statusByManager;
	private LocalDate statusByManagerAt;
	private String rejectedBc;
	
	public Receipt (User salesperson, double amount, LocalDate date, String description, String photoPath) {
		
		this.receiptId = counter++;
		this.amount = amount;
		this.date = date;
		this.description = description;
		this.photoPath = photoPath;
		this.status = Status.PENDING;
		this.submitter = salesperson;
	}
	
	public void approve(User user) {					
		
		this.status = Status.APPROVED;
		this.statusByManager = user;
		this.statusByManagerAt = LocalDate.now();
		
		System.out.println("Receipt " + this.receiptId + " approved!");
	}
	
	public void reject(User user, String reason) {					
		
		this.status = Status.REJECTED;
		this.statusByManager = user;
		this.statusByManagerAt = LocalDate.now();
		this.rejectedBc = reason;
		
		System.out.println("Receipt " + this.receiptId + " rejected.. \nReason: " + this.rejectedBc);
	}
	
	public void handle(User user) {
		
		this.accountantHandled = user;
		this.accountantHandledAt = LocalDate.now();
		this.status = Status.HANDLED;
		
		System.out.println("Receipt " + this.receiptId + " handled!");
	}
	
	public String getReason() {
		
		return this.rejectedBc;
	}
	
	public User getAccountant() {
		
		return this.accountantHandled;
	}
	
	public LocalDate getAccountantDate() {
		
		return this.accountantHandledAt;
	}
	
	public Status getStatus() {
		
		return this.status;
	}
	
	public int getReceiptId() {
		
		return this.receiptId;
	}
	
	public double getAmount() {
		
		return this.amount;
	}
	
	public LocalDate getDate() {
		
		return this.date;
	}
	
	public String getDescription() {
		
		return this.description;
	}
	
	public LocalDate getStatusByManagerAt() {
		return this.statusByManagerAt;
	}
	
	public String getPhotoPath() {
		
		return this.photoPath;
	}
	
	public User getSubmitter() {
		
		return this.submitter;
	}
	
	public User getStatusChangedBy() {
		
		return this.statusByManager;
	}
	
	public LocalDate statusChangedAt() {
		
		return this.statusByManagerAt;
	}
	

}
