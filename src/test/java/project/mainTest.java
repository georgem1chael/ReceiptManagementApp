package project;
import java.time.LocalDate;
import java.util.*;

public class mainTest {

	public static void main(String[] args) {
		
		PermissionHandler permH = new PermissionHandler();
		ReceiptHandler recH = new ReceiptHandler(permH);
		
		User a = new User("anna", Role.MANAGER, "abc");
		User b = new User("bob", Role.ACCOUNTANT, "abc");
		User b1 = new User("other bob", Role.ACCOUNTANT, "abc");
		User c = new User("charlie", Role.SALESPERSON, "abc");
		
		recH.createReceipt(c, 3904.3, LocalDate.now(), "first receipt", "photo 1");
		recH.createReceipt(b, 384.3, LocalDate.now(), "second receipt", "photo 2");
		recH.createReceipt(c, 48964.3, LocalDate.now(), "third receipt", "photo 3");
		recH.createReceipt(a, 295.6, LocalDate.now(), "fourth receipt", "photo 4");
		
		List<Receipt> receipts = recH.listReceipts();
		
		for(Receipt r: receipts) {
			
			System.out.println(r.getDescription() + " submitted by " + r.getSubmitter().getUsername() + " with receiptId: " + r.getReceiptId());
		}		
		
		recH.handleReceipt(b, 3);
		recH.handleReceipt(a, 1);
		recH.approveReceipt(b, 1);
		recH.handleReceipt(b, 1);
		recH.handleReceipt(b1, 1);
		
		recH.rejectReceipt(a, 3, "i dont like this receipt");
		recH.rejectReceipt(a, 4, "my receipt");
				
		return;
	}
}
