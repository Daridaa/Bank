package org.example;

import java.util.Objects;

public abstract class BankAccount {
    private final String number;
    private final String owner;
    private double balance;

    protected BankAccount(String number, String owner, double initialBalance) {
        this.number = number;
        this.owner = owner;
        this.balance = initialBalance;
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Баланс не может быть отрицательным");
        }
    }

    public String getNumber() {
        return number;
    }

    public String getOwner() {
        return owner;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            return;
        }
        balance += amount;
    }

    public abstract boolean withdraw(double amount);

    protected void setBalance(double balance) {
        this.balance = balance;
    }

    // ✅ ДОБАВЛЕНО: Аннотация подавляет предупреждение "Method is never used"
    @SuppressWarnings("unused")
    protected double calculateBalanceAfterWithdrawal(double amount) {
        return this.balance - amount;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "number='" + number + '\'' +
                ", owner='" + owner + '\'' +
                ", balance=" + balance +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof BankAccount other)) {
            return false;
        }

        return Objects.equals(this.number, other.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.number);
    }
}
