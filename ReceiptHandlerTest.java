import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReceiptHandlerTest {
    private ReceiptHandler handler;
    private PermissionHandler permissionHandler;
    private User manager;
    private User accountant;
    private User salesperson;

    @BeforeEach
    void setUp() {
        permissionHandler = new PermissionHandler();
        handler = new ReceiptHandler(permissionHandler);
        manager = new User("anna", Role.MANAGER, "abc");
        accountant = new User("charlie", Role.ACCOUNTANT, "abc");
        salesperson = new User("bob", Role.SALESPERSON, "abc");
    }

    @Test
    void testCreateReceiptAddsToList() {
        handler.createReceipt(salesperson, 50.0, LocalDate.now(), "third receipt", "photo 3");
        assertEquals(1, handler.listReceipts().size());
        assertEquals("third receipt", handler.listReceipts().get(0).getDescription());
        assertEquals(salesperson, handler.listReceipts().get(0).getSubmitter());
    }

    @Test
    void testHandleReceiptByAccountant() {
        handler.createReceipt(salesperson, 50.0, LocalDate.now(), "third receipt", "photo 3");
        Receipt rec = handler.listReceipts().get(0);

        handler.handleReceipt(accountant, rec.getReceiptId());
        assertEquals(accountant, rec.getAccountant());
    }

    @Test
    void testReceiptCantBeHandledBySalesperson() {
        handler.createReceipt(salesperson, 50.0, LocalDate.now(), "third receipt", "photo 3");
        Receipt rec = handler.listReceipts().get(0);

        assertThrows(SecurityException.class, () -> handler.handleReceipt(salesperson, rec.getReceiptId()));
        assertNull(rec.getAccountant());
    }

    @Test
    void testSamePersonCantHandleTheirReceipt() {
        handler.createReceipt(accountant, 50.0, LocalDate.now(), "third receipt", "photo 3");
        Receipt rec = handler.listReceipts().get(0);

        assertThrows(SecurityException.class, () -> handler.handleReceipt(accountant, rec.getReceiptId()));        
        assertNull(rec.getAccountant());
    }

    @Test
    void testApproveReceipt() {
        handler.createReceipt(salesperson, 50.0, LocalDate.now(), "third receipt", "photo 3");
        Receipt rec = handler.listReceipts().get(0);

        handler.handleReceipt(accountant, rec.getReceiptId());
        handler.approveReceipt(manager, rec.getReceiptId());

        assertEquals(Status.APPROVED, rec.getStatus());
    }

    @Test
    void testRejectReceiptThrowsIfUnauthorized() {
        handler.createReceipt(salesperson, 50.0, LocalDate.now(), "third receipt", "photo 3");
        Receipt rec = handler.listReceipts().get(0);

        handler.handleReceipt(accountant, rec.getReceiptId());
        assertThrows(SecurityException.class, () -> handler.rejectReceipt(accountant, rec.getReceiptId(), "Invalid"));
    }

    @Test
    void testCannotBeAcceptedBeforeHandling() {
        handler.createReceipt(salesperson, 50.0, LocalDate.now(), "third receipt", "photo 3");
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
        handler.createReceipt(salesperson, 50.0, LocalDate.now(), "third receipt", "photo 3");
        Receipt rec = handler.listReceipts().get(0);

        handler.handleReceipt(accountant, rec.getReceiptId());
        handler.rejectReceipt(manager, rec.getReceiptId(), "rejected...");

        assertEquals(rec.getStatus(), Status.REJECTED);
        assertEquals(rec.getReason(), "rejected...");
    }

    @Test
    void testReceiptIdUnique() {
        handler.createReceipt(salesperson, 50.0, LocalDate.now(), "third receipt", "photo 3");
        handler.createReceipt(salesperson, 70.3, LocalDate.now(), "fourth receipt", "photo 4");
        Receipt rec1 = handler.listReceipts().get(0);
        Receipt rec2 = handler.listReceipts().get(1);

        assertNotEquals(rec1.getReceiptId(), rec2.getReceiptId());
    }

    @Test
    void testStatusUnique() {
        handler.createReceipt(salesperson, 50.0, LocalDate.now(), "third receipt", "photo 3");
        handler.createReceipt(salesperson, 70.3, LocalDate.now(), "fourth receipt", "photo 4");
        Receipt rec1 = handler.listReceipts().get(0);
        Receipt rec2 = handler.listReceipts().get(1);

        handler.handleReceipt(accountant, rec1.getReceiptId());
        handler.approveReceipt(manager, rec1.getReceiptId());

        assertNotEquals(rec1.getStatus(), rec2.getStatus());
    }
}
