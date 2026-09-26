
import org.example.DebitAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DebitAccountTest {

    private DebitAccount account;
    private static final String ACCOUNT_NUMBER = "D-100";
    private static final String ACCOUNT_OWNER = "Alice";
    private static final double INITIAL_BALANCE = 1000.0;

    @BeforeEach
    void setUp() {
        account = new DebitAccount(ACCOUNT_NUMBER, ACCOUNT_OWNER, INITIAL_BALANCE);
    }

    @Test
    void initialBalanceIsPreserved() {

        // Act & Assert
        assertEquals(INITIAL_BALANCE, account.getBalance(), 0.001,
                "Начальный баланс должен сохраняться сразу после создания счёта");
        assertEquals(ACCOUNT_NUMBER, account.getNumber(),
                "Номер счёта должен соответствовать переданному в конструкторе");
        assertEquals(ACCOUNT_OWNER, account.getOwner(),
                "Владелец счёта должен соответствовать переданному в конструкторе");
    }

    @Test
    void depositIncreasesBalance() {
        // Arrange
        double depositAmount = 500.0;
        double expectedBalance = INITIAL_BALANCE + depositAmount;

        // Act
        account.deposit(depositAmount);

        // Assert
        assertEquals(expectedBalance, account.getBalance(), 0.001,
                "Баланс должен увеличиться на сумму депозита");
    }

    @Test
    void zeroDepositDoesNotChangeBalance() {
        // Arrange
        double depositAmount = 0.0;
        double initialBalance = account.getBalance();

        // Act
        account.deposit(depositAmount);

        // Assert
        assertEquals(initialBalance, account.getBalance(), 0.001,
                "Нулевой депозит не должен изменять баланс");
    }

    @Test
    void negativeDepositDoesNotChangeBalance() {
        // Arrange
        double depositAmount = -200.0;
        double initialBalance = account.getBalance();

        // Act
        account.deposit(depositAmount);

        // Assert
        assertEquals(initialBalance, account.getBalance(), 0.001,
                "Отрицательный депозит не должен изменять баланс");
    }

    @Test
    void withdrawDecreasesBalance() {
        // Arrange
        double withdrawAmount = 300.0;
        double expectedBalance = INITIAL_BALANCE - withdrawAmount;

        // Act
        boolean result = account.withdraw(withdrawAmount);

        // Assert
        assertTrue(result, "Снятие должно быть успешным, если средств достаточно");
        assertEquals(expectedBalance, account.getBalance(), 0.001,
                "Баланс должен уменьшиться на сумму снятия");
    }

    @Test
    void cannotWithdrawMoreThanBalance() {
        // Arrange
        double withdrawAmount = INITIAL_BALANCE + 100.0; // Больше, чем есть на счёте
        double initialBalance = account.getBalance();

        // Act
        boolean result = account.withdraw(withdrawAmount);

        // Assert
        assertFalse(result, "Снятие суммы, превышающей баланс, должно быть отклонено");
        assertEquals(initialBalance, account.getBalance(), 0.001,
                "Баланс не должен измениться при неудачном снятии");
    }

    @Test
    void zeroWithdrawalIsNotAllowed() {
        // Arrange
        double withdrawAmount = 0.0;
        double initialBalance = account.getBalance();

        // Act
        boolean result = account.withdraw(withdrawAmount);

        // Assert
        assertFalse(result, "Нулевое снятие должно быть запрещено");
        assertEquals(initialBalance, account.getBalance(), 0.001,
                "Баланс не должен измениться при попытке нулевого снятия");
    }

    @Test
    void negativeWithdrawalIsNotAllowed() {
        // Arrange
        double withdrawAmount = -50.0;
        double initialBalance = account.getBalance();

        // Act
        boolean result = account.withdraw(withdrawAmount);

        // Assert
        assertFalse(result, "Отрицательное снятие должно быть запрещено");
        assertEquals(initialBalance, account.getBalance(), 0.001,
                "Баланс не должен измениться при попытке отрицательного снятия");
    }
}


