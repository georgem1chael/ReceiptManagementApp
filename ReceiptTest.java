package src.project;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReceiptTest {
   private User manager;
   private User accountant;
    private User salesperson;
    private Receipt receipt;

    @BeforeEach
    void setUp() {
        manager = new User("anna", Role.MANAGER);
        accountant = new User("charlie", Role.ACCOUNTANT);
        salesperson = new User("bob", Role.SALESPERSON);
        receipt = new Receipt(salesperson, 120.0, LocalDate.now(), "first receipt", "photo 1");
    }

    @Test
    void testApproveChangesStatus() {
        receipt.approve(manager);
        assertEquals(Status.APPROVED, receipt.getStatus());
    }

    @Test
    void testRejectChangesStatus() {
        receipt.reject(manager, "rejected...");
        assertEquals(Status.REJECTED, receipt.getStatus());
    }

    @Test
    void testNewReceiptsStartPending() {
        assertEquals(Status.PENDING, receipt.getStatus());
    }

    @Test
    void restHandle()  {
        receipt.handle(accountant);
        assertEquals(receipt.getAccountant(), accountant);
    }
}