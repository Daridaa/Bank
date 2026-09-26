

import org.example.DebitAccount;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DebitAccountTest {

    @Test
    void initialBalanceIsPreserved() {
        // Arrange
        String number = "D-001";
        String owner = "Alice";
        double initialBalance = 10000.0;

        // Act
        DebitAccount account = new DebitAccount(number, owner, initialBalance);

        // Assert
        assertEquals(initialBalance, account.getBalance(), 0.001);
        assertEquals(number, account.getNumber()); // если есть геттер
        assertEquals(owner, account.getOwner());   // если есть геттер
    }

    @Test
    void depositIncreasesBalance() {
        // Arrange
        DebitAccount account = new DebitAccount("D-002", "Bob", 5_000.0);
        double depositAmount = 2_000.0;

        // Act
        account.deposit(depositAmount);

        // Assert
        assertEquals(7_000.0, account.getBalance(), 0.001);
    }

    @Test
    void zeroDepositDoesNotChangeBalance() {
        // Arrange
        DebitAccount account = new DebitAccount("D-003", "Carol", 3_000.0);
        double depositAmount = 0.0;

        // Act
        account.deposit(depositAmount);

        // Assert
        assertEquals(3_000.0, account.getBalance(), 0.001);
    }

    @Test
    void negativeDepositDoesNotChangeBalance() {
        // Arrange
        DebitAccount account = new DebitAccount("D-004", "Dave", 4_000.0);
        double depositAmount = -500.0;

        // Act
        account.deposit(depositAmount);

        // Assert
        assertEquals(4_000.0, account.getBalance(), 0.001);
    }

    @Test
    void withdrawDecreasesBalance() {
        // Arrange
        DebitAccount account = new DebitAccount("D-005", "Eve", 8_000.0);
        double withdrawAmount = 3_000.0;

        // Act
        boolean result = account.withdraw(withdrawAmount);

        // Assert
        assertTrue(result);
        assertEquals(5_000.0, account.getBalance(), 0.001);
    }

    @Test
    void cannotWithdrawMoreThanBalance() {
        // Arrange
        DebitAccount account = new DebitAccount("D-006", "Frank", 2_000.0);
        double withdrawAmount = 3_000.0;

        // Act
        boolean result = account.withdraw(withdrawAmount);

        // Assert
        assertFalse(result);
        assertEquals(2_000.0, account.getBalance(), 0.001);
    }

    @Test
    void zeroWithdrawalIsNotAllowed() {
        // Arrange
        DebitAccount account = new DebitAccount("D-007", "Grace", 1_000.0);
        double withdrawAmount = 0.0;

        // Act
        boolean result = account.withdraw(withdrawAmount);

        // Assert
        assertFalse(result);
        assertEquals(1_000.0, account.getBalance(), 0.001);
    }

    @Test
    void negativeWithdrawalIsNotAllowed() {
        // Arrange
        DebitAccount account = new DebitAccount("D-008", "Heidi", 7_000.0);
        double withdrawAmount = -1_000.0;

        // Act
        boolean result = account.withdraw(withdrawAmount);

        // Assert
        assertFalse(result);
        assertEquals(7_000.0, account.getBalance(), 0.001);
    }
}

