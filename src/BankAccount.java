public class BankAccount {

    private int pin;
    private long accountNo;
    private double balance;
    private User accountHolder;

    public BankAccount(int pin, long accountNo, User accountHolder) {
        this.pin = pin;
        this.accountNo = accountNo;
        this.balance = 0.0;
        this.accountHolder = accountHolder;
    }

    public int getPin() {
        return pin;
    }

    public long getAccountNo() {
        return accountNo;
    }

    public String getBalance() {
        NumberFormat currency = NumberFormat.getCurrencyInstance();

        return currency.format(balance);
    }

    public User getAccountHolder() {
        return accountHolder;
    }

    public void deposit(double amount) {
        balance = balance + amount;
    }

    public void withdraw(double amount) {
        balance = balance - amount;
    }

    public void transfer(BankAccount destination, double amount) {
      if (destination == null) {
        return ATM.ACCOUNT_NOT_FOUND;
      } else if (amount <= 0) {
        return ATM.INVALID;
      } else if (amount > balance) {
        return ATM.INSUFFICIENT;
      } else if (amount + destination.balance > Bank.MAX_BALANCE) {
        return ATM.OVERFLOW;
      } else {
        balance = balance - amount;
        destination.balance = destination.balance + amount;
        return ATM.SUCCESS;
      }
    }

    /*
     * Formats the account balance in preparation to be written to the data file.
     *
     * @return a fixed-width string in line with the data file specifications.
     */

    private String formatBalance() {
    	return String.format("%.2f", balance);
    }

    /*
     * Converts this BankAccount object to a string of text in preparation to
     * be written to the data file.
     *
     * @return a string of text formatted for the data file
     */

    @Override
    public String toString() {
        return String.valueOf(accountNo) +
            String.valueOf(pin) +
            accountHolder.serialize() +
            formatBalance();
    }
}
