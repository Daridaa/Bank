package org.example;

public abstract  class BankAccount {
    private final String number;
    private final String owner;
    private double balance;

    protected BankAccount(String number, String owner, double initialBalance){
        this.number = number;
        this.owner = owner;
        this.balance = initialBalance;
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Баланс не может быть отрицательным");
        }
    }
    public void deposit(double amount){
        if (amount <= 0) {
            return;
        }
        balance += amount;

    }
    public abstract boolean withdraw(double amount);

    public double getBalance(){
        return balance;
    }
    protected void setBalance(double balance) {
        this.balance = balance;
    }

    protected double calculateBalanceAfterWithdrawal(double amount) {
        return this.balance - amount;
    }

}