import java.io.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Scanner;

class Account {
    private String username;
    private double balance;

    // DecimalFormat for custom formatting without default currency symbols
    private static final DecimalFormat decimalFormatter = new DecimalFormat("#,##0.00");

    public Account(String username, double initialBalance) {
        this.username = username;
        this.balance = initialBalance;
    }

    public String getUsername() {
        return username;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposit successful. Current balance: " + formatBalance(balance));
        } else {
            System.out.println("Invalid deposit amount.");
        }
    }

    public void withdraw(double amount) {
        if (amount > balance) {
            System.out.println("Insufficient balance for this withdrawal.");
        } else if (amount <= 0) {
            System.out.println("Invalid withdrawal amount.");
        } else {
            balance -= amount;
            System.out.println("Withdrawal successful. Current balance: " + formatBalance(balance));
        }
    }

    public void checkBalance() {
        System.out.println("Current balance: " + formatBalance(balance));
    }

    private String formatBalance(double amount) {
        // Use DecimalFormat and prepend "Rs. " for consistency
        return "Rs. " + decimalFormatter.format(amount);
    }

    @Override
    public String toString() {
        return username + "," + balance;
    }
}

class AccountManager {
    private HashMap<String, Account> accounts;
    private Scanner scanner;
    private Account currentUser;
    private final String FILE_NAME = "accounts.txt";

    public AccountManager() {
        accounts = new HashMap<>();
        scanner = new Scanner(System.in);
        currentUser = null;
        loadAccountsFromFile();
    }

    private void loadAccountsFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 2) {
                    String username = data[0];
                    double balance = Double.parseDouble(data[1]);
                    accounts.put(username, new Account(username, balance));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Account file not found. Starting with no accounts.");
        } catch (IOException e) {
            System.out.println("Error reading account file.");
        }
    }

    private void saveAccountsToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Account account : accounts.values()) {
                bw.write(account.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to account file.");
        }
    }

    public void createAccount() {
        System.out.print("Enter a unique username: ");
        String username = scanner.nextLine();

        if (accounts.containsKey(username)) {
            System.out.println("Username already exists. Please try a different username.");
        } else {
            System.out.print("Enter initial balance: ");
            double initialBalance = scanner.nextDouble();
            scanner.nextLine(); 

            Account newAccount = new Account(username, initialBalance);
            accounts.put(username, newAccount);
            saveAccountsToFile(); 
            System.out.println("Account created successfully.");
        }
    }

    public boolean login() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        if (accounts.containsKey(username)) {
            currentUser = accounts.get(username);
            System.out.println("Login successful. Welcome, " + currentUser.getUsername() + "!");
            return true;
        } else {
            System.out.println("Username not found. Please try again or create a new account.");
            return false;
        }
    }

    public void logout() {
        if (currentUser != null) {
            System.out.println("Logged out successfully. Goodbye, " + currentUser.getUsername() + "!");
            currentUser = null;
        } else {
            System.out.println("You are not logged in.");
        }
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public Account getCurrentUser() {
        return currentUser;
    }

    public void updateAccountData() {
        saveAccountsToFile(); 
    }
}

public class ATM {
    private AccountManager accountManager;
    private Scanner scanner;

    public ATM() {
        accountManager = new AccountManager();
        scanner = new Scanner(System.in);
    }

    public void showMenu() {
        int choice;
        do {
            System.out.println("\nATM Menu:");
            System.out.println("1. Create Account");
            System.out.println("2. Login to Account");
            System.out.println("3. Check Balance");
            System.out.println("4. Deposit");
            System.out.println("5. Withdraw");
            System.out.println("6. Logout");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    accountManager.createAccount();
                    break;
                case 2:
                    accountManager.login();
                    break;
                case 3:
                    if (accountManager.isLoggedIn()) {
                        accountManager.getCurrentUser().checkBalance();
                    } else {
                        System.out.println("You must be logged in to check your balance.");
                    }
                    break;
                case 4:
                    if (accountManager.isLoggedIn()) {
                        handleDeposit();
                    } else {
                        System.out.println("You must be logged in to deposit money.");
                    }
                    break;
                case 5:
                    if (accountManager.isLoggedIn()) {
                        handleWithdraw();
                    } else {
                        System.out.println("You must be logged in to withdraw money.");
                    }
                    break;
                case 6:
                    accountManager.logout();
                    break;
                case 7:
                    System.out.println("Thank you for using the ATM. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 7);
    }

    private void handleDeposit() {
        System.out.print("Enter amount to deposit: ");
        double amount = scanner.nextDouble();
        accountManager.getCurrentUser().deposit(amount);
        accountManager.updateAccountData(); 
    }

    private void handleWithdraw() {
        System.out.print("Enter amount to withdraw: ");
        double amount = scanner.nextDouble();
        accountManager.getCurrentUser().withdraw(amount);
        accountManager.updateAccountData(); 
    }

    public static void main(String[] args) {
        ATM atm = new ATM();
        atm.showMenu();
    }
}
