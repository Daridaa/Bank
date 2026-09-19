public class TransferService {

    private final CommissionPolicy commissionPolicy;
    public TransferService(CommissionPolicy commissionPolicy) {
        if (commissionPolicy == null) {
            throw new IllegalArgumentException("Политика комиссии не может быть null");
        }
        this.commissionPolicy = commissionPolicy;
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
        return true;
    }
}