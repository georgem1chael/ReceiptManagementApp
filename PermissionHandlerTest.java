import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PermissionHandlerTest {
    private PermissionHandler handler = new PermissionHandler();
    private User manager;
    private User accountant;
    private User salesperson;
    private Receipt receipt;

    @BeforeEach
    void setUp() {
        manager = new User("anna", Role.MANAGER, "abc");
        accountant = new User("charlie", Role.ACCOUNTANT, "abc");
        salesperson = new User("bob", Role.SALESPERSON, "abc");
        receipt = new Receipt(salesperson, 120.0, LocalDate.now(), "first receipt", "photo 1");
    }

    @Test
    void testManagerCanApprove() {
        assertTrue(handler.canApprove(manager, receipt));
    }

    @Test
    void testSalespersonCannotApprove() {
        assertFalse(handler.canApprove(salesperson, receipt));
    }

    @Test
    void testAccountantCannotApprove() {
        assertFalse(handler.canApprove(accountant, receipt));
    }

    @Test
    void testSubmitterCannotApproveOwn() {
        Receipt rec = new Receipt(manager, 100, LocalDate.now(), "second receipt", "photo 2");
        assertFalse(handler.canApprove(manager, rec));
    }

    
}
