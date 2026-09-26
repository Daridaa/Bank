import org.example.CreditAccount;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CreditAccountTest {

    @Test
    void accountCanGoIntoNegativeBalance() {
        // Arrange
        String number = "C-001";
        String owner = "Liam";
        double initialBalance = 1_000.0;
        double creditLimit = 5_000.0;
        double withdrawAmount = 3_000.0; // баланс станет -2000

        // Act
        CreditAccount account = new CreditAccount(number, owner, initialBalance, creditLimit);
        boolean result = account.withdraw(withdrawAmount);

        // Assert
        assertTrue(result);
        assertEquals(-2_000.0, account.getBalance(), 0.001);
    }

    @Test
    void creditLimitCanBeUsed() {
        // Arrange
        String number = "C-002";
        String owner = "Mia";
        double initialBalance = 0.0;
        double creditLimit = 10_000.0;
        double withdrawAmount = 10_000.0; // баланс станет -10000, ровно лимит

        // Act
        CreditAccount account = new CreditAccount(number, owner, initialBalance, creditLimit);
        boolean result = account.withdraw(withdrawAmount);

        // Assert
        assertTrue(result);
        assertEquals(-10_000.0, account.getBalance(), 0.001);
    }

    @Test
    void cannotExceedCreditLimit() {
        // Arrange
        String number = "C-003";
        String owner = "Noah";
        double initialBalance = 0.0;
        double creditLimit = 5_000.0;
        double withdrawAmount = 6_000.0; // баланс станет -6000 > -5000

        // Act
        CreditAccount account = new CreditAccount(number, owner, initialBalance, creditLimit);
        boolean result = account.withdraw(withdrawAmount);

        // Assert
        assertFalse(result);
        assertEquals(0.0, account.getBalance(), 0.001);
    }

    @Test
    void failedWithdrawalDoesNotChangeBalance() {
        // Arrange
        String number = "C-004";
        String owner = "Olivia";
        double initialBalance = 2_000.0;
        double creditLimit = 3_000.0;
        double withdrawAmount = 6_000.0; // баланс станет -4000 < -3000

        // Act
        CreditAccount account = new CreditAccount(number, owner, initialBalance, creditLimit);
        boolean result = account.withdraw(withdrawAmount);

        // Assert
        assertFalse(result);
        assertEquals(2_000.0, account.getBalance(), 0.001);
    }
}