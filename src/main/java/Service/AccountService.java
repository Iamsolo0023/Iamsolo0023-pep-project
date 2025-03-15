package Service;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {
    private AccountDAO accountDAO;

    public AccountService() {
        // Initializing AccountDAO so we can interact with the database
        // Initially forgot to initialize this, which caused a NullPointerException while calling DAO methods
        this.accountDAO = new AccountDAO();
    }

    /**
     * Registers a new account after validating username and password.
     * Faced issues with handling duplicate usernames earlier, so added a check for existing usernames.
     */
    public Account registerAccount(Account account) {
        // Check if username is empty or null
        // Initially, I only checked for null, but that didn't catch cases where users entered just spaces.
        if (account.getUsername() == null || account.getUsername().trim().equals("")) {
            return null; // Invalid username (preventing blank spaces)
        }

        // Check if password is at least 4 characters long
        // Had a bug where I was checking for a minimum of 5 instead of 4, fixed that.
        if (account.getPassword() == null || account.getPassword().length() < 4) {
            return null; // Password is too short (was causing issues in login validation)
        }

        // Check if username already exists in the database
        // Faced an issue where duplicate usernames were allowed initially, which caused login problems.
        if (accountDAO.getAccountByUsername(account.getUsername()) != null) {
            return null; // Username is taken, preventing duplicate accounts
        }

        // If all checks pass, create the account
        // Initially forgot to return the newly created account, causing issues when displaying user info after registration.
        return accountDAO.createAccount(account);
    }

    /**
     * Authenticates a user by checking if the username exists and password matches.
     * Had an issue where passwords were case-sensitive, which was intended but confused some users.
     */
    public Account login(String username, String password) {
        // Retrieve account details from database
        Account account = accountDAO.getAccountByUsername(username);

        // Check if account exists and password matches
        // Initially, I was comparing passwords using == instead of .equals(), which caused login failures.
        if (account != null && account.getPassword().equals(password)) {
            return account; // Successful login
        }

        return null; // Login failed (was returning an empty Account object before, now fixed)
    }
}
