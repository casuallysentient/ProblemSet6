import java.io.IOException;
import java.util.Scanner;

public class ATM {

  private Scanner in;
  private BankAccount activeAccount;
  private Bank bank;
  
  public static final int VIEW = 1;
  public static final int DEPOSIT = 2;
  public static final int WITHDRAW = 3;
  public static final int LOGOUT = 4;

  public static final int INVALID = 0;
  public static final int INSUFFICIENT = 1;
  public static final int SUCCESS = 2;

  ////////////////////////////////////////////////////////////////////////////
  //                                                                        //
  // Refer to the Simple ATM tutorial to fill in the details of this class. //
  // You'll need to implement the new features yourself.                    //
  //                                                                        //
  ////////////////////////////////////////////////////////////////////////////

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

  public static void main(String[] args) {
    ATM atm = new ATM();
  }
}
