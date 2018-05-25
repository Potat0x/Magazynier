package magazynier.contractor;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import magazynier.Message;
import magazynier.worker.Worker;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ChatController {
    public TextArea chatArea;
    public TextArea messageArea;
    public Label workerNameLabel;

    private Worker recipient;
    private Worker sender;

    private ChatModel model;

    public ChatController(Worker recipient, Worker sender) {
        this.recipient = recipient;
        this.sender = sender;

        model = new ChatModel(recipient, sender);
    }

    private void insertMessage(Message msg) {
        chatArea.appendText(msg.getDate() + " " + msg.getSender() + "> " + msg.getMessage() + "\n");
    }

    public void initialize() {
        messageArea.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && event.isControlDown()) {
                sendMessage();
            }
        });

        workerNameLabel.setText(recipient.getFullName());
        refresh();
    }

    @FXML
    public void sendMessage() {
        String messageText = messageArea.getText();

        if (!messageText.isEmpty()) {
            System.out.println("send: " + messageText);

            Message newMessage = new Message(Timestamp.valueOf(LocalDateTime.now()), messageText, sender, recipient);
            model.sendMessage(newMessage);
            messageArea.clear();
            insertMessage(newMessage);
        }
    }

    @FXML
    public void refresh() {
        chatArea.clear();
        ArrayList<Message> messages = model.getMessagesList();
        //noinspection ComparatorMethodParameterNotUsed
        messages.sort((m1, m2) -> (m1.getDate().before(m2.getDate())) ? -1 : 1);
        messages.forEach(this::insertMessage);
    }
}
