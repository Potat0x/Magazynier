package magazynier.contractor;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class ChatController {
    public TextArea chatArea;
    public TextArea messageArea;


    public void initialize() {

    }

    @FXML
    public void sendMessage() {
        System.out.println("send: " + messageArea.getText());
        chatArea.appendText(messageArea.getText());
    }

}
