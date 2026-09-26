import org.example.SavingsAccount;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SavingsAccountTest {

    @Test
    void withdrawAllowedIfMinimumBalanceIsMaintained() {
        // Arrange
        String number = "S-001";
        String owner = "Ivan";
        double initialBalance = 5_000.0;
        double minimumBalance = 1_000.0;
        double withdrawAmount = 3_000.0; // после снятия будет 2000 >= 1000

        // Act
        SavingsAccount account = new SavingsAccount(number, owner, initialBalance, minimumBalance);
        boolean result = account.withdraw(withdrawAmount);

        // Assert
        assertTrue(result);
        assertEquals(2_000.0, account.getBalance(), 0.001);
    }

    @Test
    void withdrawNotAllowedIfBalanceGoesBelowMinimum() {
        // Arrange
        String number = "S-002";
        String owner = "Judy";
        double initialBalance = 5_000.0;
        double minimumBalance = 1_000.0;
        double withdrawAmount = 4_500.0; // после снятия будет 500 < 1000

        // Act
        SavingsAccount account = new SavingsAccount(number, owner, initialBalance, minimumBalance);
        boolean result = account.withdraw(withdrawAmount);

        // Assert
        assertFalse(result);
        assertEquals(5_000.0, account.getBalance(), 0.001);
    }

    @Test
    void failedWithdrawalDoesNotChangeBalance() {
        // Arrange
        String number = "S-003";
        String owner = "Kevin";
        double initialBalance = 3_000.0;
        double minimumBalance = 2_000.0;
        double withdrawAmount = 2_000.0; // после снятия будет 1000 < 2000

        // Act
        SavingsAccount account = new SavingsAccount(number, owner, initialBalance, minimumBalance);
        boolean result = account.withdraw(withdrawAmount);

        // Assert
        assertFalse(result);
        assertEquals(3_000.0, account.getBalance(), 0.001);
    }
}