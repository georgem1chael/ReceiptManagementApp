package project;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReceiptHandlerTest {
    private ReceiptHandler handler;
    private PermissionHandler permissionHandler;
    private User manager;
    private User accountant;
    private User salesperson;
    private User admin;

    @BeforeEach
    void setUp() {
        permissionHandler = new PermissionHandler();
        handler = new ReceiptHandler(permissionHandler);
        manager = new User("anna", Role.MANAGER, "abc", "email");
        accountant = new User("charlie", Role.ACCOUNTANT, "abc", "email");
        salesperson = new User("bob", Role.SALESPERSON, "abc", "email");
    }

    @Test
    void testCreateReceiptAddsToList() {
        handler.createReceipt(salesperson, 50.0, LocalDate.now(), "third receipt", "photo 3", "bank statement");
        assertEquals(1, handler.listReceipts().size());
        assertEquals("third receipt", handler.listReceipts().get(0).getDescription());
        assertEquals(salesperson, handler.listReceipts().get(0).getSubmitter());
    }

    @Test
    void testHandleReceiptByAccountant() {
        handler.createReceipt(salesperson, 50.0, LocalDate.now(), "third receipt", "photo 3", "bank statement");
        Receipt rec = handler.listReceipts().get(0);

        handler.handleReceipt(accountant, rec.getReceiptId());
        assertEquals(accountant, rec.getAccountant());
    }

    @Test
    void testReceiptCantBeHandledBySalesperson() {
        handler.createReceipt(salesperson, 50.0, LocalDate.now(), "third receipt", "photo 3", "bank statement");
        Receipt rec = handler.listReceipts().get(0);

        assertThrows(SecurityException.class, () -> handler.handleReceipt(salesperson, rec.getReceiptId()));
        assertNull(rec.getAccountant());
    }

    @Test
    void testReceiptCantBeHandledByManager(){
        handler.createReceipt(salesperson, 50.0, LocalDate.now(), "third receipt", "photo 3", "bank statement");
        Receipt rec = handler.listReceipts().get(0);

        assertThrows(SecurityException.class, () -> handler.handleReceipt(manager, rec.getReceiptId()));
        assertNull(rec.getAccountant());
    }

    @Test
    void testReceiptCantBeApprovedBySalesperson(){
        handler.createReceipt(salesperson, 50.0, LocalDate.now(), "third receipt", "photo 3", "bank statement");
        Receipt rec = handler.listReceipts().get(0);

        rec.handle(accountant);

        assertThrows(SecurityException.class, () -> handler.approveReceipt(salesperson, rec.getReceiptId()));
        assertNull(rec.getStatusChangedBy());
    }

    @Test
    void testReceiptCantBeRejectedBySalesperson(){
        handler.createReceipt(salesperson, 50.0, LocalDate.now(), "third receipt", "photo 3", "bank statement");
        Receipt rec = handler.listReceipts().get(0);

        rec.handle(accountant);

        assertThrows(SecurityException.class, () -> handler.rejectReceipt(salesperson, rec.getReceiptId(), "rejected..."));
        assertNull(rec.getStatusChangedBy());
    }

    @Test
    void testReceiptCantBeApprovedByAccountant(){
        handler.createReceipt(salesperson, 50.0, LocalDate.now(), "third receipt", "photo 3", "bank statement");
        Receipt rec = handler.listReceipts().get(0);

        User acc = new User("acc", Role.ACCOUNTANT, "password", "email");

        rec.handle(accountant);

        assertThrows(SecurityException.class, () -> handler.approveReceipt(acc, rec.getReceiptId()));
        assertNull(rec.getStatusChangedBy());
    }

    @Test
    void testReceiptCantBeRejectedByAccountant(){
        handler.createReceipt(salesperson, 50.0, LocalDate.now(), "third receipt", "photo 3","bank statement");
        Receipt rec = handler.listReceipts().get(0);

        User acc = new User("acc", Role.ACCOUNTANT, "password", "email");

        rec.handle(accountant);

        assertThrows(SecurityException.class, () -> handler.rejectReceipt(acc, rec.getReceiptId(), "rejected..."));
        assertNull(rec.getStatusChangedBy());
    }

    @Test
    void testSamePersonCantHandleTheirReceipt() {
        handler.createReceipt(accountant, 50.0, LocalDate.now(), "third receipt", "photo 3", "bank statement");
        Receipt rec = handler.listReceipts().get(0);

        assertThrows(SecurityException.class, () -> handler.handleReceipt(accountant, rec.getReceiptId()));        
        assertNull(rec.getAccountant());
    }

    @Test
    void testManagerCantApproveOwnReceipt(){
        handler.createReceipt(manager, 50.0, LocalDate.now(), "third receipt", "photo 3", "bank statement");
        Receipt rec = handler.listReceipts().get(0);

        rec.handle(accountant);

        assertThrows(SecurityException.class, () -> handler.approveReceipt(manager, rec.getReceiptId()));        
        assertNull(rec.getStatusChangedBy());
    }

    @Test
    void testManagerCantRejectOwnReceipt(){
        handler.createReceipt(manager, 50.0, LocalDate.now(), "third receipt", "photo 3", "bank statement");
        Receipt rec = handler.listReceipts().get(0);

        rec.handle(accountant);

        assertThrows(SecurityException.class, () -> handler.rejectReceipt(manager, rec.getReceiptId(), "rejected..."));        
        assertNull(rec.getStatusChangedBy());
    }

    @Test
    void testApproveReceipt() {
        handler.createReceipt(salesperson, 50.0, LocalDate.now(), "third receipt", "photo 3", "bank statement");
        Receipt rec = handler.listReceipts().get(0);

        handler.handleReceipt(accountant, rec.getReceiptId());
        handler.approveReceipt(manager, rec.getReceiptId());

        assertEquals(Status.APPROVED, rec.getStatus());
    }

    @Test
    void testRejectReceiptThrowsIfUnauthorized() {
        handler.createReceipt(salesperson, 50.0, LocalDate.now(), "third receipt", "photo 3","bank statement");
        Receipt rec = handler.listReceipts().get(0);

        handler.handleReceipt(accountant, rec.getReceiptId());
        assertThrows(SecurityException.class, () -> handler.rejectReceipt(accountant, rec.getReceiptId(), "Invalid"));
    }

    @Test
    void testCannotBeAcceptedBeforeHandling() {
        handler.createReceipt(salesperson, 50.0, LocalDate.now(), "third receipt", "photo 3", "bank statement");
        Receipt rec = handler.listReceipts().get(0);

        assertThrows(SecurityException.class, () -> handler.approveReceipt(manager, rec.getReceiptId()));
        assertThrows(SecurityException.class, () -> handler.rejectReceipt(manager, rec.getReceiptId(), "reason"));
        assertEquals(Status.PENDING, rec.getStatus());
    }

    @Test
    void testApproveRejectHandleThrowWhenReceiptMissing() {
         int invalidId = 999;

        assertThrows(IllegalArgumentException.class, () -> handler.handleReceipt(accountant, invalidId));
        assertThrows(IllegalArgumentException.class, () -> handler.approveReceipt(manager, invalidId));
        assertThrows(IllegalArgumentException.class, () -> handler.rejectReceipt(manager, invalidId, "reason"));
    }

    @Test
    void testRejectReceipt() {
        handler.createReceipt(salesperson, 50.0, LocalDate.now(), "third receipt", "photo 3", "bank statement");
        Receipt rec = handler.listReceipts().get(0);

        handler.handleReceipt(accountant, rec.getReceiptId());
        handler.rejectReceipt(manager, rec.getReceiptId(), "rejected...");

        assertEquals(rec.getStatus(), Status.REJECTED);
        assertEquals(rec.getReason(), "rejected...");
    }

    @Test
    void testReceiptIdUnique() {
        handler.createReceipt(salesperson, 50.0, LocalDate.now(), "third receipt", "photo 3", "bank statement");
        handler.createReceipt(salesperson, 70.3, LocalDate.now(), "fourth receipt", "photo 4", "bank statement");
        Receipt rec1 = handler.listReceipts().get(0);
        Receipt rec2 = handler.listReceipts().get(1);

        assertNotEquals(rec1.getReceiptId(), rec2.getReceiptId());
    }

    @Test
    void testStatusUnique() {
        handler.createReceipt(salesperson, 50.0, LocalDate.now(), "third receipt", "photo 3", "bank statement");
        handler.createReceipt(salesperson, 70.3, LocalDate.now(), "fourth receipt", "photo 4", "bank statement");
        Receipt rec1 = handler.listReceipts().get(0);
        Receipt rec2 = handler.listReceipts().get(1);

        handler.handleReceipt(accountant, rec1.getReceiptId());
        handler.approveReceipt(manager, rec1.getReceiptId());

        assertNotEquals(rec1.getStatus(), rec2.getStatus());
    }

    @Test
    void testListReceiptForSalesperson(){
        User salesperson2 = new User("alice", Role.SALESPERSON, "password", "email");
        handler.createReceipt(salesperson, 100.0, LocalDate.now(), "receipt1", "photo1", "bank statement 1");
        handler.createReceipt(salesperson, 200.0, LocalDate.now(), "receipt2", "photo2", "bank statement 2");
        List<Receipt> salesperson1Receipts = handler.listReceipt(salesperson);
        assertEquals(2, salesperson1Receipts.size());
        List<Receipt> salesperson2Receipts = handler.listReceipt(salesperson2);
        assertEquals(0, salesperson2Receipts.size());
    }

    @Test
    void testListReceiptForAccountant(){
        handler.createReceipt(salesperson, 100.0, LocalDate.now(), "pending receipt", "photo1", "bank statement 1");
        handler.createReceipt(salesperson, 200.0, LocalDate.now(), "will be handled", "photo2", "bank statement 2");  
        handler.createReceipt(salesperson, 300.0, LocalDate.now(), "will be approved", "photo3", "bank statement 3");
        Receipt rec1 = handler.listReceipts().get(0);  // PENDING
        Receipt rec2 = handler.listReceipts().get(1);  // HANDLED
        Receipt rec3 = handler.listReceipts().get(2);  // WAPPROVED
        // rec1 stays PENDING
        handler.handleReceipt(accountant, rec2.getReceiptId());  // HANDLED
        handler.handleReceipt(accountant, rec3.getReceiptId());  // HANDLED first
        handler.approveReceipt(manager, rec3.getReceiptId());    // APPROVED
        List<Receipt> accountantReceipts = handler.listReceipt(accountant);
        assertEquals(1, accountantReceipts.size());
        assertEquals("pending receipt", accountantReceipts.get(0).getDescription());
    }

    @Test
    void testListReceiptForManager(){
        // Create receipts by salesperson
        handler.createReceipt(salesperson, 100.0, LocalDate.now(), "pending receipt", "photo1", "bank statement 1");
        handler.createReceipt(salesperson, 200.0, LocalDate.now(), "will be handled", "photo2", "bank statement 2");
        handler.createReceipt(salesperson, 300.0, LocalDate.now(), "will be approved", "photo3", "bank statement 3");
        
        Receipt rec1 = handler.listReceipts().get(0);  // PENDING (manager shouldn't see)
        Receipt rec2 = handler.listReceipts().get(1);  // HANDLED (manager SHOULD see)
        Receipt rec3 = handler.listReceipts().get(2);  // APPROVED (manager shouldn't see)
        
        // rec1 stays PENDING
        handler.handleReceipt(accountant, rec2.getReceiptId());  // HANDLED
        handler.handleReceipt(accountant, rec3.getReceiptId());  // ANDLED first
        handler.approveReceipt(manager, rec3.getReceiptId());    // rAPPROVED
        
        // Manager should only see rec2 (HANDLED + not submitted by manager)
        List<Receipt> managerReceipts = handler.listReceipt(manager);
        assertEquals(1, managerReceipts.size());
        assertEquals("will be handled", managerReceipts.get(0).getDescription());
    }

    @Test
    void testListReceiptForAdministrator(){
        User admin = new User("admin", Role.ADMIN, "password", "email");
        handler.createReceipt(salesperson, 100.0, LocalDate.now(), "receipt1", "photo1", "bank statement 1");
        handler.createReceipt(salesperson, 200.0, LocalDate.now(), "receipt2", "photo2", "bank statement 2");
        
        // Administrator should get SecurityException
        assertThrows(SecurityException.class, () -> handler.listReceipt(admin));
    }

    @Test
    void testApproveDoesNotOverwriteAccountant() {
        handler.createReceipt(salesperson, 50.0, LocalDate.now(), "receipt", "photo", "bank statement");
        Receipt rec = handler.listReceipts().get(0);

        handler.handleReceipt(accountant, rec.getReceiptId());
        handler.approveReceipt(manager, rec.getReceiptId());

        assertEquals(accountant, rec.getAccountant());
    }

    @Test
    void testRejectDoesNotOverwriteAccountant() {
        handler.createReceipt(salesperson, 50.0, LocalDate.now(), "receipt", "photo", "bank statement");
        Receipt rec = handler.listReceipts().get(0);

        handler.handleReceipt(accountant, rec.getReceiptId());
        handler.rejectReceipt(manager, rec.getReceiptId(), "bad");

        assertEquals(accountant, rec.getAccountant());
    }

    @Test
    void testRejectAllowsEmptyReason() {
        handler.createReceipt(salesperson, 100, LocalDate.now(), "receipt", "photo", "bank statement");
        Receipt rec = handler.listReceipts().get(0);
        handler.handleReceipt(accountant, rec.getReceiptId());

        handler.rejectReceipt(manager, rec.getReceiptId(), "");

        assertEquals(Status.REJECTED, rec.getStatus());
        assertEquals("", rec.getReason());
    }




}
