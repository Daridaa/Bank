package org.example;

public class TransferService {

    private final CommissionPolicy commissionPolicy;
    private final NotificationService notificationService;
    public TransferService(CommissionPolicy commissionPolicy, NotificationService notificationService) {
        if (commissionPolicy == null) {
            throw new IllegalArgumentException("Политика комиссии не может быть null");
        }
        if (notificationService == null) {
            throw new IllegalArgumentException("Сервис уведомлений не может быть null");
        }
        this.commissionPolicy = commissionPolicy;
        this.notificationService = notificationService;
    }
    public boolean transfer(BankAccount from, BankAccount to, double amount) {
        if (amount <= 0) {
            return false;
        }
        if (from == to) {
            return false;
        }
        double commission = commissionPolicy.calculate(amount);
        double totalDeduction = amount + commission;

        if (!from.withdraw(totalDeduction)) {
            return false;
        }
        to.deposit(amount);
        String message = String.format("Transfer %.1f completed", amount);
        notificationService.notify(message);
        return true;
    }
}