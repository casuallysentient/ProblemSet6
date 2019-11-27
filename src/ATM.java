import java.io.IOException;
import java.util.Scanner;

public class ATM {

  private Scanner in;
  private BankAccount activeAccount;
  private Bank bank;

  public static final int VIEW = 1;
  public static final int DEPOSIT = 2;
  public static final int WITHDRAW = 3;
  public static final int TRANSFER = 4;
  public static final int LOGOUT = 5;

  public static final int INVALID = 0;
  public static final int INSUFFICIENT = 1;
  public static final int SUCCESS = 2;
  public static final int ACCOUNT_NOT_FOUND = 3;
  public static final int RECURSIVE_TRANSFER = 4;
  public static final int OVERFLOW = 5;

  public static final int FIRST_NAME_WIDTH = 20;
  public static final int LAST_NAME_WIDTH = 30;

  /**
  * Constructs a new instance of the ATM class.
  */

  public ATM() {
    this.in = new Scanner(System.in);
    try {
    	this.bank = new Bank();
    } catch (IOException e) {
  	  // cleanup any resources (i.e., the Scanner) and exit
    }
  }

  /*
  * Application execution begins here.
  */

  public void startup() {
      System.out.println("Welcome to the AIT ATM!\n");

      while (true) {
          System.out.print("Account No.: ");
          String newOrReturningUser = in.nextLine();
          if (newOrReturningUser.equals("+")) {
            newAccount();
            if (activeAccount != null) {
              System.out.printf("\nThank you. Your account number is %d.\nPlease log in to access your newly created account.\n", activeAccount.getAccountNo());
            }
          } else {
            long accountNo = Long.valueOf(newOrReturningUser);

            System.out.print("PIN        : ");
            int pin = in.nextInt();

            if (isValidLogin(accountNo, pin)) {
                System.out.println("\nHello, again, " + activeAccount.getAccountHolder().getFirstName() + "!\n");

                boolean validLogin = true;
                while (validLogin) {
                    switch (getSelection()) {
                        case VIEW: showBalance(); break;
                        case DEPOSIT: deposit(); break;
                        case WITHDRAW: withdraw(); break;
                        case TRANSFER: transfer(); break;
                        case LOGOUT: validLogin = false; break;
                        default: System.out.println("\nInvalid selection.\n"); break;
                    }
                }
            } else {
                if (accountNo == -1 && pin == -1) {
                    shutdown();
                } else {
                    System.out.println("\nInvalid account number and/or PIN.\n");
                }
            }
          }
      }
  }

  public void newAccount() {
    boolean validAccount = false;
    String newFirstName;
    String newLastName;
    int newPin;

    while (!validAccount) {
      System.out.print("\nFirst name: ");
      newFirstName = in.next().strip();

      System.out.print("\nLast name: ");
      newLastName = in.next().strip();

      if ((newFirstName.length() < 1 || newFirstName.length() > 20 || newLastName.length() < 1 || newLastName.length() > 30) && !newFirstName.equals("-1") && !newLastName.equals("-1")) {
        System.out.println("Invalid first or last name. Your first name must be between 1 and 20 characters");
        System.out.println("and your last name must be between 1 and 30 characters. Enter -1 in either of the name fields to exit.");
      } else if (newFirstName.equals("-1") || newLastName.equals("-1")) {
        System.out.println("\nExiting account creator...");
        return;
      } else {
        validAccount = true;
      }
    }
    validAccount = false;
    while (!validAccount) {
      System.out.print("PIN: ");
      if(newPin == -1) {
        return;
      } else if (newPin < 1000 || newPin > 9999) {
        System.out.println("Invalid pin entered. The pin must be numerically smaller than 10,000 and numerically greater than 999.");
        System.out.println("If you would like to exit the account creator, enter -1.");
        validAccount = false;
      } else {
        validAccount = true;
      }
    }
    activeAccount = bank.createAccount(newPin, new User(newFirstName, newLastName));
    bank.update(activeAccount);
    bank.save();
  }

  public boolean isValidLogin(long accountNo, int pin) {
    try {
      return accountNo == activeAccount.getAccountNo() && pin == activeAccount.getPin();
    } catch (Exception e) {
      return false;
    }
  }

  public int getSelection() {
      System.out.println("[1] View balance");
      System.out.println("[2] Deposit money");
      System.out.println("[3] Withdraw money");
      System.out.println("[4] Transfer money");
      System.out.println("[5] Logout");

      return in.nextInt();
  }

  public void showBalance() {
      System.out.println("\nCurrent balance: " + activeAccount.getBalance());
  }

  public void deposit() {
      System.out.print("\nEnter amount: ");
      double amount = in.nextDouble();

      int status = activeAccount.deposit(amount);
      if (status == ATM.INVALID) {
          System.out.println("\nDeposit rejected. Amount must be greater than $0.00.\n");
      } else if (status == ATM.SUCCESS) {
          System.out.println("\nDeposit accepted.\n");
      }
  }

  public void withdraw() {
      System.out.print("\nEnter amount: ");
      double amount = in.nextDouble();

      int status = activeAccount.withdraw(amount);
      if (status == ATM.INVALID) {
          System.out.println("\nWithdrawal rejected. Amount must be greater than $0.00.\n");
      } else if (status == ATM.INSUFFICIENT) {
          System.out.println("\nWithdrawal rejected. Insufficient funds.\n");
      } else if (status == ATM.SUCCESS) {
          System.out.println("\nWithdrawal accepted.\n");
      }
  }

  public void transfer() {
    System.out.print("\nEnter account: ");
    long destination = in.nextLong();
    System.out.print("\nEnter amount: ");
    double amount = in.nextDouble();

    int status = activeAccount.transfer(activeAccount, bank.getAccount(destination), amount);
    if (status == ATM.ACCOUNT_NOT_FOUND) {
      System.out.println("\nTransfer rejected. Destination account not found.");
    } else if (status == ATM.RECURSIVE_TRANSFER) {
      System.out.println("\nTransfer rejected. You can't transfer money to yourself.");
    } else if (status == ATM.INVALID) {
      System.out.println("\nTransfer rejected. Amount must be greater than $0.00.");
    } else if (status == ATM.OVERFLOW) {
      System.out.println("\nTransfer rejected. Amount would cause balance to exceed $999,999,999,999.99.");
    } else if (status == ATM.INSUFFICIENT) {
      System.out.println("\nTransfer rejected. Insufficient funds.");
    } else if (status == ATM.SUCCESS) {
      System.out.println("\nTransfer accepted.");
    }
    bank.update(activeAccount);
    try {
        bank.update(bank.getAccount(destination));
    } catch (Exception e) {}
    bank.save();
  }

  public void shutdown() {
      if (in != null) {
          in.close();
      }

      System.out.println("\nGoodbye!");
      System.exit(0);
  }

  public static void main(String[] args) {
    ATM atm = new ATM();
    atm.startup();
  }
}
