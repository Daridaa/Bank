import org.example.CreditAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CreditAccountTest {

    private CreditAccount account;
    private static final String ACCOUNT_NUMBER = "C-100";
    private static final String ACCOUNT_OWNER = "Charlie";
    private static final double INITIAL_BALANCE = 1000.0;
    private static final double CREDIT_LIMIT = 5000.0;
    // Максимально допустимый отрицательный баланс = -5000

    @BeforeEach
    void setUp() {
        // Создаём счёт с начальным балансом и кредитным лимитом
        account = new CreditAccount(ACCOUNT_NUMBER, ACCOUNT_OWNER, INITIAL_BALANCE, CREDIT_LIMIT);
    }

    @Test
    void accountCanGoIntoNegativeBalance() {
        // Arrange: снимаем 2000. Баланс станет -1000. Это в пределах лимита (-5000).
        double withdrawAmount = 2000.0;
        double expectedBalance = INITIAL_BALANCE - withdrawAmount; // -1000.0

        // Act
        boolean result = account.withdraw(withdrawAmount);

        // Assert
        assertTrue(result, "Снятие, уводящее в минус, должно быть успешным");
        assertEquals(expectedBalance, account.getBalance(), 0.001,
                "Баланс должен корректно стать отрицательным");
    }

    @Test
    void canUseCreditLimit() {
        // Arrange: используем лимит почти полностью.
        // Текущий баланс 1000. Лимит 5000. Можем снять максимум 6000, чтобы уйти в -5000.
        // Снимем 5990, чтобы остаться чуть выше лимита.
        double withdrawAmount = 5990.0;
        double expectedBalance = INITIAL_BALANCE - withdrawAmount; // -4990.0

        // Act
        boolean result = account.withdraw(withdrawAmount);

        // Assert
        assertTrue(result, "Снятие, использующее кредитный лимит, должно пройти");
        assertEquals(expectedBalance, account.getBalance(), 0.001,
                "Баланс должен быть близок к пределу лимита");
    }

    @Test
    void cannotExceedCreditLimit() {
        // Arrange: пытаемся снять 6001. Баланс станет -5001, что > 5000 (лимит).
        double withdrawAmount = 6001.0;
        double initialBalance = account.getBalance();

        // Act
        boolean result = account.withdraw(withdrawAmount);

        // Assert
        assertFalse(result, "Снятие, превышающее кредитный лимит, должно быть отклонено");
        assertEquals(initialBalance, account.getBalance(), 0.001,
                "Баланс не должен измениться при попытке превысить лимит");
    }

    @Test
    void failedWithdrawalDoesNotChangeBalance() {
        // Arrange: ещё один кейс на превышение лимита (снять ровно на 1 рубль больше лимита)
        double withdrawAmount = INITIAL_BALANCE + CREDIT_LIMIT + 1.0; // 1000 + 5000 + 1 = 6001
        double initialBalance = account.getBalance();

        // Act
        boolean result = account.withdraw(withdrawAmount);

        // Assert
        assertFalse(result, "Операция должна быть отклонена");
        assertEquals(initialBalance, account.getBalance(), 0.001,
                "Баланс обязан остаться неизменным");
    }
}