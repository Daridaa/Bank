import org.example.SavingsAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SavingsAccountTest {

    private SavingsAccount account;
    private static final String ACCOUNT_NUMBER = "S-100";
    private static final String ACCOUNT_OWNER = "Bob";
    private static final double INITIAL_BALANCE = 5000.0;
    private static final double MINIMUM_BALANCE = 1000.0;

    @BeforeEach
    void setUp() {
        // Arrange: создаём объект SavingsAccount с начальным балансом 5000 и минимальным остатком 1000
        account = new SavingsAccount(ACCOUNT_NUMBER, ACCOUNT_OWNER, INITIAL_BALANCE, MINIMUM_BALANCE);
    }

    @Test
    void canWithdrawIfMinimumBalanceIsPreserved() {
        // Arrange: снимаем 3000. Остаток будет 2000, что >= 1000 (мин. остаток)
        double withdrawAmount = 3000.0;
        double expectedBalance = INITIAL_BALANCE - withdrawAmount;

        // Act
        boolean result = account.withdraw(withdrawAmount);

        // Assert
        assertTrue(result, "Снятие должно быть успешным, так как минимальный остаток не нарушен");
        assertEquals(expectedBalance, account.getBalance(), 0.001,
                "Баланс должен уменьшиться на сумму снятия");
    }

    @Test
    void cannotWithdrawBelowMinimumBalance() {
        // Arrange: пытаемся снять 4500. Остаток будет 500, что < 1000 (мин. остаток)
        double withdrawAmount = 4_500.0;
        double initialBalance = account.getBalance();

        // Act
        boolean result = account.withdraw(withdrawAmount);

        // Assert
        assertFalse(result, "Снятие должно быть отклонено, так как нарушает минимальный остаток");
        assertEquals(initialBalance, account.getBalance(), 0.001,
                "Баланс не должен измениться при неудачном снятии");
    }

    @Test
    void failedWithdrawalDoesNotChangeBalance() {
        // Arrange: ещё один кейс на нарушение лимита (снять всё до копейки)

        double initialBalance = account.getBalance();

        // Act
        boolean result = account.withdraw(INITIAL_BALANCE);

        // Assert
        assertFalse(result, "Снятие всей суммы должно быть отклонено из-за минимального остатка");
        assertEquals(initialBalance, account.getBalance(), 0.001,
                "Баланс обязан остаться неизменным при ошибке");
    }
}