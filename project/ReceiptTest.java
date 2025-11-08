package project;

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

    @Test
    void testGettersAndTransitionDatesAndReason() {
        // basic getters
        assertEquals(120.0, receipt.getAmount());
        assertEquals("first receipt", receipt.getDescription());
        assertEquals("photo 1", receipt.getPhotoPath());
        assertEquals(salesperson, receipt.getSubmitter());

        // before any transition some fields should be null
        assertNull(receipt.getReason());
        assertNull(receipt.getAccountantDate());
        assertNull(receipt.getStatusByManagerAt());

        // after handling by accountant
        receipt.handle(accountant);
        assertNotNull(receipt.getAccountant());
        assertNotNull(receipt.getAccountantDate());

        // after manager decision (approve)
        receipt.approve(manager);
        assertEquals(Status.APPROVED, receipt.getStatus());
        assertEquals(manager, receipt.getStatusChangedBy());
        assertNotNull(receipt.statusChangedAt());

        // new receipt to test rejection reason
        Receipt r2 = new Receipt(salesperson, 10.0, LocalDate.now(), "r2", "p2");
        r2.handle(accountant);
        r2.reject(manager, "nope");
        assertEquals("nope", r2.getReason());
        assertEquals(Status.REJECTED, r2.getStatus());
    }
}