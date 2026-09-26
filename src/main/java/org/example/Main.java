package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        DebitAccount sender = new DebitAccount("D-001", "Alice", 15000.0);
        SavingsAccount receiver = new SavingsAccount("S-001", "Bob", 5000.0, 1000.0);


        CommissionPolicy commissionPolicy = new PercentCommission(1.0);
        NotificationService notificationService = new ConsoleNotificationService();


        TransferService transferService = new TransferService(commissionPolicy, notificationService);

        System.out.println("=== Проверка 1: Успешный перевод ===");
        System.out.println("Баланс отправителя до: " + sender.getBalance());
        System.out.println("Баланс получателя до: " + receiver.getBalance());


        boolean success = transferService.transfer(sender, receiver, 5_000.0);

        // Assert: проверяем результаты
        System.out.println("Перевод успешен: " + success);
        System.out.println("Баланс отправителя после: " + sender.getBalance());
        System.out.println("Баланс получателя после: " + receiver.getBalance());

        System.out.println("\n=== Проверка 2: Неуспешный перевод (не хватает средств с комиссией) ===");
        double amountTooBig = 12_000.0;
        System.out.println("Пробуем перевести: " + amountTooBig);
        boolean successTooBig = transferService.transfer(sender, receiver, amountTooBig);
        System.out.println("Перевод успешен: " + successTooBig);
        System.out.println("Баланс отправителя (не должен измениться): " + sender.getBalance());

        System.out.println("\n=== Проверка 3: Неуспешный перевод (нарушает минимальный остаток на сберегательном счёте) ===");

        double amountViolatingMin = 9500.0;
        System.out.println("Пробуем перевести: " + amountViolatingMin);
        boolean successViolating = transferService.transfer(sender, receiver, amountViolatingMin);
        System.out.println("Перевод успешен: " + successViolating);
        System.out.println("Баланс отправителя (не должен измениться): " + sender.getBalance());
        System.out.println("Баланс получателя (не должен измениться): " + receiver.getBalance());

        System.out.println("\n=== Проверка 4: Перевод на тот же счёт (должен отклониться) ===");
        boolean successSameAccount = transferService.transfer(sender, sender, 1000.0);
        System.out.println("Перевод успешен: " + successSameAccount);

        System.out.println("\n=== Проверка 5: Нулевая сумма (должна отклониться) ===");
        boolean successZero = transferService.transfer(sender, receiver, 0.0);
        System.out.println("Перевод успешен: " + successZero);

        BankAccount account = new DebitAccount("001", "Ivan", 10000.0);
        System.out.println(account);
        // Вывод: DebitAccount{number='001', owner='Ivan', balance=10000.0}


        System.out.println(new SavingsAccount("002", "Maria", 5000.0, 1000.0));
        // Вывод: SavingsAccount{number='002', owner='Maria', balance=5000.0}

        // Ещё один пример (для CreditAccount, если он есть)
        System.out.println(new CreditAccount("003", "John", 2000.0, 5000.0));
        // Вывод: CreditAccount{number='003', owner='John', balance=2000.0}


    }
}
