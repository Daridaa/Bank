import org.example.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransferServiceTest {

    private TransferService transferService;
    private static final double COMMISSION = 10.0;
    private static final double TRANSFER_AMOUNT = 1000.0;

    // Счета-заглушки (предполагается, что у вас есть конструкторы, принимающие баланс)
    private DebitAccount debitSender;
    private DebitAccount debitReceiver;
    private SavingsAccount savingsSender;
    private SavingsAccount savingsReceiver;
    private CreditAccount creditSender;

    private static class MockCommissionPolicy implements CommissionPolicy {
        @Override
        public double calculate(double amount) {
            return 10.0;
        }
    }

    // Заглушка для уведомлений: ничего не делает
    private static class MockNotificationService implements NotificationService {
        @Override
        public void notify(String message) {
            // В тестах уведомления не нужны, просто игнорируем
        }
    }
    @BeforeEach
    void setUp() {
        transferService = new TransferService(
                new MockCommissionPolicy(),
                new MockNotificationService()
        );

        // Инициализация счетов с достаточным балансом для успешных переводов
        debitSender = new DebitAccount("D-1", "Alice", 5_000.0);
        debitReceiver = new DebitAccount("D-2", "Bob", 1_000.0);

        savingsSender = new SavingsAccount("S-1", "Alice", 5_000.0, 1_000.0);
        savingsReceiver = new SavingsAccount("S-2", "Bob", 1_000.0, 500.0);

        creditSender = new CreditAccount("C-1", "Alice", 1_000.0, 5_000.0);
    }
    @Test
    void shouldRejectNegativeAmount() {
        // Arrange
        double amount = -100.0;
        double initialSenderBalance = debitSender.getBalance();
        double initialReceiverBalance = debitReceiver.getBalance();

        // Act
        boolean result = transferService.transfer(debitSender, debitReceiver, amount);

        // Assert
        assertFalse(result, "Перевод отрицательной суммы должен быть отклонён");
        assertEquals(initialSenderBalance, debitSender.getBalance(), 0.001,
                "Баланс отправителя не должен измениться");
        assertEquals(initialReceiverBalance, debitReceiver.getBalance(), 0.001,
                "Баланс получателя не должен измениться");
    }

    @Test
    void shouldRejectZeroAmount() {
        // Arrange
        double amount = 0.0;
        double initialSenderBalance = debitSender.getBalance();
        double initialReceiverBalance = debitReceiver.getBalance();

        // Act
        boolean result = transferService.transfer(debitSender, debitReceiver, amount);

        // Assert
        assertFalse(result, "Перевод нулевой суммы должен быть отклонён");
        assertEquals(initialSenderBalance, debitSender.getBalance(), 0.001,
                "Баланс отправителя не должен измениться");
        assertEquals(initialReceiverBalance, debitReceiver.getBalance(), 0.001,
                "Баланс получателя не должен измениться");
    }

    @Test
    void shouldRejectTransferToSelf() {
        // Arrange
        double initialBalance = debitSender.getBalance();

        // Act
        boolean result = transferService.transfer(debitSender, debitSender, TRANSFER_AMOUNT);

        // Assert
        assertFalse(result, "Перевод самому себе должен быть отклонён");
        assertEquals(initialBalance, debitSender.getBalance(), 0.001,
                "Баланс не должен измениться при переводе самому себе");
    }
    @Test
    void shouldDeductCommissionFromSenderAndCreditFullAmountToReceiver() {
        // Arrange
        double initialSenderBalance = debitSender.getBalance();
        double initialReceiverBalance = debitReceiver.getBalance();

        // Act
        boolean result = transferService.transfer(debitSender, debitReceiver, TRANSFER_AMOUNT);

        // Assert
        assertTrue(result, "Перевод должен быть успешным");

        // Отправитель теряет сумму перевода + комиссию
        assertEquals(initialSenderBalance - TRANSFER_AMOUNT - COMMISSION,
                debitSender.getBalance(), 0.001,
                "Баланс отправителя должен уменьшиться на сумму перевода и комиссию");

        // Получатель получает ровно сумму перевода
        assertEquals(initialReceiverBalance + TRANSFER_AMOUNT,
                debitReceiver.getBalance(), 0.001,
                "Баланс получателя должен увеличиться ровно на сумму перевода");
    }
    @Test
    void shouldFailIfInsufficientFundsForTransferPlusCommission() {
        // Arrange: Создадим счёт, где баланс ровно равен сумме перевода (без комиссии)
        DebitAccount tightBalanceAccount = new DebitAccount("D-3", "Tight", TRANSFER_AMOUNT);
        double initialSenderBalance = tightBalanceAccount.getBalance();
        double initialReceiverBalance = debitReceiver.getBalance();

        // Act
        boolean result = transferService.transfer(tightBalanceAccount, debitReceiver, TRANSFER_AMOUNT);

        // Assert
        assertFalse(result, "Перевод должен быть отклонён, так как не хватает средств на комиссию");
        assertEquals(initialSenderBalance, tightBalanceAccount.getBalance(), 0.001,
                "Баланс отправителя не должен измениться");
        assertEquals(initialReceiverBalance, debitReceiver.getBalance(), 0.001,
                "Баланс получателя не должен измениться");
    }
    //DebitAccount → DebitAccount
    @Test
    void shouldTransferBetweenTwoDebitAccounts() {
        // Arrange
        double initialSenderBalance = debitSender.getBalance();
        double initialReceiverBalance = debitReceiver.getBalance();

        // Act
        boolean result = transferService.transfer(debitSender, debitReceiver, TRANSFER_AMOUNT);

        // Assert
        assertTrue(result);
        assertEquals(initialSenderBalance - TRANSFER_AMOUNT - COMMISSION, debitSender.getBalance(), 0.001);
        assertEquals(initialReceiverBalance + TRANSFER_AMOUNT, debitReceiver.getBalance(), 0.001);
    }
    //DebitAccount → SavingsAccount
    @Test
    void shouldTransferFromDebitToSavings() {
        // Arrange
        double initialSenderBalance = debitSender.getBalance();
        double initialReceiverBalance = savingsReceiver.getBalance();

        // Act
        boolean result = transferService.transfer(debitSender, savingsReceiver, TRANSFER_AMOUNT);

        // Assert
        assertTrue(result);
        assertEquals(initialSenderBalance - TRANSFER_AMOUNT - COMMISSION, debitSender.getBalance(), 0.001);
        assertEquals(initialReceiverBalance + TRANSFER_AMOUNT, savingsReceiver.getBalance(), 0.001);
    }
    //CreditAccount → DebitAccount
    @Test
    void shouldTransferFromCreditToDebit() {
        // Arrange
        // У CreditAccount баланс 1000, лимит 5000. Можем списать много.
        double initialSenderBalance = creditSender.getBalance();
        double initialReceiverBalance = debitReceiver.getBalance();

        // Act
        boolean result = transferService.transfer(creditSender, debitReceiver, TRANSFER_AMOUNT);

        // Assert
        assertTrue(result);
        // Баланс станет: 1000 - 1000 - 10 = -10.0
        assertEquals(initialSenderBalance - TRANSFER_AMOUNT - COMMISSION, creditSender.getBalance(), 0.001);
        assertEquals(initialReceiverBalance + TRANSFER_AMOUNT, debitReceiver.getBalance(), 0.001);
    }
    // SavingsAccount → DebitAccount
    @Test
    void shouldTransferFromSavingsToDebit() {
        // Arrange
        // SavingsSender имеет баланс 5000, minBalance 1000.
        // Переводим 1000. Остаток будет 4000. Это > 1000. Всё ок.
        double initialSenderBalance = savingsSender.getBalance();
        double initialReceiverBalance = debitReceiver.getBalance();

        // Act
        boolean result = transferService.transfer(savingsSender, debitReceiver, TRANSFER_AMOUNT);

        // Assert
        assertTrue(result);
        assertEquals(initialSenderBalance - TRANSFER_AMOUNT - COMMISSION, savingsSender.getBalance(), 0.001);
        assertEquals(initialReceiverBalance + TRANSFER_AMOUNT, debitReceiver.getBalance(), 0.001);
    }
}
