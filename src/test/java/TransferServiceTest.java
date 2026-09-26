import org.example.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransferServiceTest {

    private TransferService transferService;
    private static final double COMMISSION_PERCENT = 1.0; // 1%

    @BeforeEach
    void setUp() {

        transferService = new TransferService(new PercentCommission(COMMISSION_PERCENT), new ConsoleNotificationService());
    }


    @Test
    void successfulTransferChangesBothBalances() {
        // Arrange
        DebitAccount sender = new DebitAccount("D-100", "Alice", 10000.0);
        DebitAccount receiver = new DebitAccount("D-200", "Bob", 5000.0);
        double amount = 1000.0;

        // Act
        boolean result = transferService.transfer(sender, receiver, amount);

        // Assert
        assertTrue(result, "Перевод должен быть успешным");
        assertEquals(8990.0, sender.getBalance(), 0.001, "Баланс отправителя: 10000 - 1000 - 1% комиссия (10) = 8990");
        assertEquals(6000.0, receiver.getBalance(), 0.001, "Баланс получателя: 5000 + 1000 = 6000");
    }

    @Test
    void failedTransferDoesNotChangeBalances() {
        // Arrange
        DebitAccount sender = new DebitAccount("D-300", "Carol", 100.0);
        DebitAccount receiver = new DebitAccount("D-400", "Dave", 0.0);
        double amount = 200.0; // Больше, чем есть у отправителя

        double initialSenderBalance = sender.getBalance();
        double initialReceiverBalance = receiver.getBalance();

        // Act
        boolean result = transferService.transfer(sender, receiver, amount);

        // Assert
        assertFalse(result, "Перевод должен быть отклонён из-за нехватки средств");
        assertEquals(initialSenderBalance, sender.getBalance(), 0.001, "Баланс отправителя не должен измениться");
        assertEquals(initialReceiverBalance, receiver.getBalance(), 0.001, "Баланс получателя не должен измениться");
    }

    @Test
    void cannotTransferNegativeAmount() {
        // Arrange
        DebitAccount sender = new DebitAccount("D-500", "Eve", 1000.0);
        DebitAccount receiver = new DebitAccount("D-600", "Frank", 1000.0);
        double amount = -100.0;

        double initialSenderBalance = sender.getBalance();
        double initialReceiverBalance = receiver.getBalance();

        // Act
        boolean result = transferService.transfer(sender, receiver, amount);

        // Assert
        assertFalse(result, "Отрицательная сумма должна быть отклонена");
        assertEquals(initialSenderBalance, sender.getBalance(), 0.001);
        assertEquals(initialReceiverBalance, receiver.getBalance(), 0.001);
    }

    @Test
    void cannotTransferZeroAmount() {
        // Arrange
        DebitAccount sender = new DebitAccount("D-700", "Grace", 1000.0);
        DebitAccount receiver = new DebitAccount("D-800", "Heidi", 1000.0);
        double amount = 0.0;

        double initialSenderBalance = sender.getBalance();
        double initialReceiverBalance = receiver.getBalance();

        // Act
        boolean result = transferService.transfer(sender, receiver, amount);

        // Assert
        assertFalse(result, "Нулевая сумма должна быть отклонена");
        assertEquals(initialSenderBalance, sender.getBalance(), 0.001);
        assertEquals(initialReceiverBalance, receiver.getBalance(), 0.001);
    }

    @Test
    void cannotTransferToSelf() {
        // Arrange
        DebitAccount account = new DebitAccount("D-900", "Ivan", 1000.0);
        double amount = 100.0;

        double initialBalance = account.getBalance();

        // Act
        boolean result = transferService.transfer(account, account, amount);

        // Assert
        assertFalse(result, "Перевод самому себе должен быть отклонён");
        assertEquals(initialBalance, account.getBalance(), 0.001);
    }

    @Test
    void commissionIsDeductedFromSender() {
        // Arrange
        DebitAccount sender = new DebitAccount("D-950", "Judy", 10000.0);
        DebitAccount receiver = new DebitAccount("D-960", "Kevin", 0.0);
        double amount = 1000.0;
        double expectedCommission = amount * (COMMISSION_PERCENT / 100.0); // 10.0

        // Act
        transferService.transfer(sender, receiver, amount);

        // Assert
        assertEquals(10000.0 - 1000.0 - expectedCommission, sender.getBalance(), 0.001,
                "Баланс отправителя должен уменьшиться на сумму перевода + комиссию");
    }

    @Test
    void receiverGetsExactlyTransferAmount() {
        // Arrange
        DebitAccount sender = new DebitAccount("D-970", "Liam", 10000.0);
        DebitAccount receiver = new DebitAccount("D-980", "Mia", 0.0);
        double amount = 1000.0;

        // Act
        transferService.transfer(sender, receiver, amount);

        // Assert
        assertEquals(amount, receiver.getBalance(), 0.001,
                "Получатель должен получить ровно сумму перевода, комиссия его не касается");
    }

    @Test
    void transferFailsIfInsufficientFundsWithCommission() {
        // Arrange
        DebitAccount sender = new DebitAccount("D-990", "Noah", 1000.0);
        DebitAccount receiver = new DebitAccount("D-991", "Olivia", 0.0);
        double amount = 1000.0;
        // Комиссия 1% = 10.0. Итого нужно 1010.0, а есть только 1000.0

        double initialSenderBalance = sender.getBalance();
        double initialReceiverBalance = receiver.getBalance();

        // Act
        boolean result = transferService.transfer(sender, receiver, amount);

        // Assert
        assertFalse(result, "Перевод должен быть отклонён, так как средств не хватает даже с учётом комиссии");
        assertEquals(initialSenderBalance, sender.getBalance(), 0.001);
        assertEquals(initialReceiverBalance, receiver.getBalance(), 0.001);
    }



    @Test
    void debitToDebit() {
        // Arrange
        DebitAccount sender = new DebitAccount("DD-1", "Sender", 5000.0);
        DebitAccount receiver = new DebitAccount("DD-2", "Receiver", 1000.0);
        double amount = 2000.0;

        // Act
        boolean result = transferService.transfer(sender, receiver, amount);

        // Assert
        assertTrue(result);
        assertEquals(2980.0, sender.getBalance(), 0.001); // 5000 - 2000 - 20 (комиссия)
        assertEquals(3000.0, receiver.getBalance(), 0.001); // 1000 + 2000
    }

    @Test
    void debitToSavings() {
        // Arrange
        DebitAccount sender = new DebitAccount("DS-1", "Sender", 5000.0);
        // Минимальный остаток 500, начальный 1000
        SavingsAccount receiver = new SavingsAccount("SS-1", "Receiver", 1000.0, 500.0);
        double amount = 2000.0;

        // Act
        boolean result = transferService.transfer(sender, receiver, amount);

        // Assert
        assertTrue(result);
        assertEquals(2980.0, sender.getBalance(), 0.001);
        assertEquals(3000.0, receiver.getBalance(), 0.001);
    }

    @Test
    void creditToDebit() {
        // Arrange
        // Начальный баланс 1000, лимит 5000. Можем уйти в минус до -4000
        CreditAccount sender = new CreditAccount("CD-1", "Sender", 1000.0, 5000.0);
        DebitAccount receiver = new DebitAccount("DD-2", "Receiver", 1000.0);
        double amount = 3000.0;

        // Act
        boolean result = transferService.transfer(sender, receiver, amount);

        // Assert
        assertTrue(result);
        // Баланс отправителя: 1000 - 3000 - 30 (комиссия) = -2030. Это в пределах лимита (-5000)
        assertEquals(-2030.0, sender.getBalance(), 0.001);
        assertEquals(4000.0, receiver.getBalance(), 0.001);
    }

    @Test
    void savingsToDebit() {
        // Arrange
        // Начальный 5000, мин. остаток 1000. Можем снять максимум 4000
        SavingsAccount sender = new SavingsAccount("SV-1", "Sender", 5000.0, 1000.0);
        DebitAccount receiver = new DebitAccount("DB-2", "Receiver", 1000.0);
        double amount = 3000.0; // Допустимо, останется 2000 >= 1000

        // Act
        boolean result = transferService.transfer(sender, receiver, amount);

        // Assert
        assertTrue(result);
        assertEquals(1970.0, sender.getBalance(), 0.001); // 5000 - 3000 - 30
        assertEquals(4000.0, receiver.getBalance(), 0.001);
    }

    @Test
    void savingsToDebit_FailsIfViolatesMinimumBalance() {
        // Arrange
        SavingsAccount sender = new SavingsAccount("SV-2", "Sender", 5000.0, 1000.0);
        DebitAccount receiver = new DebitAccount("DB-3", "Receiver", 1000.0);
        double amount = 4500.0; // После снятия будет 500 < 1000 (нарушение)

        double initialSenderBalance = sender.getBalance();
        double initialReceiverBalance = receiver.getBalance();

        // Act
        boolean result = transferService.transfer(sender, receiver, amount);

        // Assert
        assertFalse(result, "Перевод должен быть отклонён, так как нарушает минимальный остаток на сберегательном счёте");
        assertEquals(initialSenderBalance, sender.getBalance(), 0.001);
        assertEquals(initialReceiverBalance, receiver.getBalance(), 0.001);
    }
}