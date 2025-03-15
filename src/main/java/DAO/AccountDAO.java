package DAO;

import Model.Account;
import Util.ConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDAO {

    /**
     * Creates a new account and inserts it into the database.
     * Faced an issue where auto-generated account IDs weren’t being retrieved properly, so I had to use RETURN_GENERATED_KEYS.
     */
    public Account createAccount(Account account) {
        String query = "INSERT INTO Account (username, password) VALUES (?, ?)";
        
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            // Setting the username and password for the new account
            stmt.setString(1, account.getUsername());
            stmt.setString(2, account.getPassword());

            // Execute update and check if rows were affected
            int rowsInserted = stmt.executeUpdate();

            // Initially forgot to check affected rows, which led to unexpected null returns
            if (rowsInserted > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                
                // Retrieve and set the generated account ID
                if (generatedKeys.next()) {
                    account.setAccount_id(generatedKeys.getInt(1));
                    return account; // Account successfully created
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Logging error (Need to improve logging instead of using printStackTrace)
        }

        return null; // Account creation failed
    }

    /**
     * Retrieves an account by username.
     * Originally forgot to handle the case where no results were found, causing NullPointerException.
     */
    public Account getAccountByUsername(String username) {
        String query = "SELECT * FROM Account WHERE username = ?";
        
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            ResultSet result = stmt.executeQuery();

            // If a matching account is found, create an Account object
            if (result.next()) {
                return new Account(
                        result.getInt("account_id"),
                        result.getString("username"),
                        result.getString("password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Need to replace this with proper logging
        }

        return null; // No account found with that username
    }

    /**
     * Retrieves an account by account ID.
     * Initially had an issue where I was accidentally setting the wrong column index in setInt.
     */
    public Account getAccountById(int accountId) {
        String query = "SELECT * FROM Account WHERE account_id = ?";
        
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, accountId);
            ResultSet result = stmt.executeQuery();

            // If a matching account is found, create an Account object
            if (result.next()) {
                return new Account(
                        result.getInt("account_id"),
                        result.getString("username"),
                        result.getString("password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Consider logging errors properly
        }

        return null; // No account found with that ID
    }
}
