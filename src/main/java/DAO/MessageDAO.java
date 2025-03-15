package DAO;

import Model.Message;
import Util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    /**
     * Inserts a new message into the database.
     * Initially forgot to use RETURN_GENERATED_KEYS, so message ID wasn't getting set properly.
     */
    public Message createMessage(Message message) {
        String query = "INSERT INTO Message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            // Setting message details (had an issue where I accidentally swapped parameters earlier)
            stmt.setInt(1, message.getPosted_by());
            stmt.setString(2, message.getMessage_text());
            stmt.setLong(3, message.getTime_posted_epoch());

            int rowsInserted = stmt.executeUpdate();

            // Initially didn't check affected rows, which led to issues when debugging
            if (rowsInserted > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    message.setMessage_id(generatedKeys.getInt(1)); // Setting generated message ID
                    return message;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Need to implement better error handling
        }

        return null; // Message creation failed
    }

    /**
     * Retrieves all messages from the database.
     * Initially used an Array instead of List, which was inflexible for dynamic data.
     */
    public List<Message> getMessages() {
        List<Message> messageList = new ArrayList<>();
        String query = "SELECT * FROM Message";

        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {

            // Loop through all messages and add them to the list
            while (resultSet.next()) {
                Message message = new Message(
                        resultSet.getInt("message_id"),
                        resultSet.getInt("posted_by"),
                        resultSet.getString("message_text"),
                        resultSet.getLong("time_posted_epoch")
                );
                messageList.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Need to replace this with proper logging
        }

        return messageList; // Returns all messages (or an empty list if none exist)
    }

    /**
     * Retrieves a specific message by its ID.
     * Initially forgot to return null when no message was found, which led to NullPointerException in service layer.
     */
    public Message getMessageById(int messageId) {
        String query = "SELECT * FROM Message WHERE message_id = ?";

        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, messageId);
            ResultSet resultSet = stmt.executeQuery();

            // If a matching message is found, return it
            if (resultSet.next()) {
                return new Message(
                        resultSet.getInt("message_id"),
                        resultSet.getInt("posted_by"),
                        resultSet.getString("message_text"),
                        resultSet.getLong("time_posted_epoch")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Error handling can be improved
        }

        return null; // No message found with that ID
    }

    /**
     * Deletes a message from the database.
     * Initially forgot to check if delete was successful before returning.
     */
    public boolean deleteMessage(int messageId) {
        String query = "DELETE FROM Message WHERE message_id = ?";

        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, messageId);
            int rowsDeleted = stmt.executeUpdate();

            return rowsDeleted > 0; // Returns true if delete was successful
        } catch (SQLException e) {
            e.printStackTrace(); // Need to log errors properly
        }

        return false; // Message deletion failed
    }
}
