package Service;

import DAO.AccountDAO;
import DAO.MessageDAO;
import Model.Message;

import java.util.List;

public class MessageService {
    private MessageDAO messageDAO;
    private AccountDAO accountDAO;

    public MessageService() {
        // Initializing DAOs to interact with the database
        this.messageDAO = new MessageDAO();
        this.accountDAO = new AccountDAO();
    }

    /**
     * Creates a new message after validating its content and ensuring the user exists.
     * Initially, I forgot to check if the user exists, which caused foreign key errors.
     * Fixed that by verifying the user before inserting the message.
     */
    public Message createMessage(Message message) {
        // Message text must not be empty and should be within 255 characters
        if (message.getMessage_text() == null || message.getMessage_text().trim().isEmpty()) {
            return null; // Preventing blank messages
        }
        if (message.getMessage_text().length() > 255) {
            return null; // Limiting message length (DB constraint issue faced earlier)
        }

        // Check if the user exists before allowing them to post a message
        if (accountDAO.getAccountById(message.getPosted_by()) == null) {
            return null; // User not found, cannot post a message
        }

        // Everything looks good, saving the message to the database
        return messageDAO.createMessage(message);
    }

    /**
     * Fetches all messages from the database.
     * Originally, I tried returning a null list when no messages existed, but that caused issues in the frontend.
     * Now, it always returns an empty list instead.
     */
    public List<Message> getAllMessages() {
        return messageDAO.getMessages(); // Returning an empty list if no messages exist
    }

    /**
     * Fetch a specific message by its ID.
     * Had a bug where invalid IDs were not handled properly—now returning null if message not found.
     */
    public Message getMessageById(int id) {
        return messageDAO.getMessageById(id);
    }

    /**
     * Deletes a message and returns the deleted message.
     * Had to make sure to first fetch the message before deleting it, so we can return its details.
     */
    public Message deleteMessage(int id) {
        Message message = messageDAO.getMessageById(id);
        if (message != null) {
            messageDAO.deleteMessage(id); // Deleting the message from DB
            return message; // Returning the deleted message for confirmation
        }
        return null; // Message not found, nothing to delete
    }
}
