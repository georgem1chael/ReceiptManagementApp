package project;

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
        manager = new User("anna", Role.MANAGER);
        accountant = new User("charlie", Role.ACCOUNTANT);
        salesperson = new User("bob", Role.SALESPERSON);
    }

    @Test
    void testCreateReceiptAddsToList() {
        handler.createReceipt(salesperson, 50.0, LocalDate.now(), "third receipt", "photo 3");
        assertEquals(1, handler.listReceipts().size());
    }

    @Test
    void testApproveReceipt() {
        handler.createReceipt(salesperson, 50.0, LocalDate.now(), "third receipt", "photo 3");
        int id = handler.listReceipts().get(0).getReceiptId();
        handler.handleReceipt(accountant, id);
        handler.approveReceipt(manager, id);
        assertEquals(Status.APPROVED, handler.listReceipts().get(0).getStatus());
    }

    @Test
    void testRejectReceiptThrowsIfUnauthorized() {
        handler.createReceipt(salesperson, 50.0, LocalDate.now(), "third receipt", "photo 3");
        int id = handler.listReceipts().get(0).getReceiptId();
        handler.handleReceipt(accountant, id);
        assertThrows(SecurityException.class, () -> {
            handler.rejectReceipt(salesperson, id, "rejected...");
        });
    }

    @Test
    void testCannotBeAcceptedBeforeHandling() {
         handler.createReceipt(salesperson, 50.0, LocalDate.now(), "third receipt", "photo 3");
        int id = handler.listReceipts().get(0).getReceiptId();
        assertThrows(SecurityException.class, () -> {
            handler.approveReceipt(manager, id);
        });
    }

    @Test
    void testApproveRejectHandleThrowWhenReceiptMissing() {
        // use an id that doesn't exist
        int missingId = 99999;
        assertThrows(IllegalArgumentException.class, () -> {
            handler.approveReceipt(manager, missingId);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            handler.rejectReceipt(manager, missingId, "nope");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            handler.handleReceipt(accountant, missingId);
        });
    }
}

