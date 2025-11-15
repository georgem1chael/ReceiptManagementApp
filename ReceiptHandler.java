import java.time.LocalDate;
import java.util.*;

public class ReceiptHandler{
	
	private List<Receipt> receipts;
	private PermissionHandler permissionHandler;
	
	public ReceiptHandler(PermissionHandler permissionHandler) {
		
		this.permissionHandler = permissionHandler;
		this.receipts = new ArrayList<>();
		
	}
	
	
	public void createReceipt(User user, double amount, LocalDate date, String description, String photoPath) {
		
		Receipt newReceipt = new Receipt(user, amount, date, description, photoPath);
		
		receipts.add(newReceipt);
	}
	
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
	
	public List<Receipt> listReceipts() {
		
		return receipts;
	}

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
			
			for(Receipt r : receipts){
				
				if(r.getStatus() == Status.PENDING)
					newList.add(r);
			}

			return newList;
		}

		else if(user.getRole() == Role.MANAGER){
			
			for(Receipt r : receipts){
				
				if(r.getStatus() == Status.HANDLED && !r.getSubmitter().equals(user))
					newList.add(r);
			}

			return newList;
		}

		throw new SecurityException("Administrators can't view a comprehensive Receipt list!");
	}
	
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


