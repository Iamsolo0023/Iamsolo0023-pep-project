package Controller;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class SocialMediaController {
    private AccountService accountService;
    private MessageService messageService;

    public SocialMediaController() {
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }

    public Javalin startAPI() {
        Javalin app = Javalin.create();

        // User Endpoints
        app.post("/register", this::registerUser);
        app.post("/login", this::loginUser);

        // Message Endpoints
        app.post("/messages", this::createMessage);
        app.get("/messages", this::getAllMessages);
        app.get("/messages/{id}", this::getMessageById);
        app.delete("/messages/{id}", this::deleteMessage);
        app.patch("/messages/{id}", this::updateMessageText);
        app.get("/accounts/{id}/messages", this::getMessagesByUser);

        return app;
    }

    private void registerUser(Context ctx) {
        Account account = ctx.bodyAsClass(Account.class);
        Account createdAccount = accountService.registerAccount(account);
        if (createdAccount != null) {
            ctx.status(200).json(createdAccount); // Fix: Should be 200 instead of 201
        } else {
            ctx.status(400).result("");
        }
    }

    private void loginUser(Context ctx) {
        Account account = ctx.bodyAsClass(Account.class);
        Account existingAccount = accountService.login(account.getUsername(), account.getPassword());
        if (existingAccount != null) {
            ctx.status(200).json(existingAccount);
        } else {
            ctx.status(401).result("");
        }
    }

    private void createMessage(Context ctx) {
        Message message = ctx.bodyAsClass(Message.class);
        Message createdMessage = messageService.createMessage(message);
        if (createdMessage != null) {
            ctx.status(200).json(createdMessage); // Fix: Should be 200 instead of 201
        } else {
            ctx.status(400).result("");
        }
    }

    private void getAllMessages(Context ctx) {
        List<Message> messages = messageService.getAllMessages();
        ctx.status(200).json(messages);
    }

    private void getMessageById(Context ctx) {
        int messageId = Integer.parseInt(ctx.pathParam("id"));
        Message message = messageService.getMessageById(messageId);
        if (message != null) {
            ctx.status(200).json(message);
        } else {
            ctx.status(200).result(""); // Fix: Should return 200 with empty body
        }
    }

    private void deleteMessage(Context ctx) {
        int messageId = Integer.parseInt(ctx.pathParam("id"));
        Message deletedMessage = messageService.deleteMessage(messageId);
        if (deletedMessage != null) {
            ctx.status(200).json(deletedMessage);
        } else {
            ctx.status(200).result(""); // Fix: Should return 200 even if not found
        }
    }

    private void updateMessageText(Context ctx) {
        int messageId = Integer.parseInt(ctx.pathParam("id"));
        Message existingMessage = messageService.getMessageById(messageId);
        if (existingMessage == null) {
            ctx.status(400).result("");
            return;
        }

        Message updatedMessage = ctx.bodyAsClass(Message.class);
        if (updatedMessage.getMessage_text().trim().isEmpty() || updatedMessage.getMessage_text().length() > 255) {
            ctx.status(400).result("");
            return;
        }

        existingMessage.setMessage_text(updatedMessage.getMessage_text());
        ctx.status(200).json(existingMessage);
    }

    private void getMessagesByUser(Context ctx) {
        int userId = Integer.parseInt(ctx.pathParam("id"));
        List<Message> messages = messageService.getAllMessages();
        messages.removeIf(msg -> msg.getPosted_by() != userId);
        ctx.status(200).json(messages);
    }
}
