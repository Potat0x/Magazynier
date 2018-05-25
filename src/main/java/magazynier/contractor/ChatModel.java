package magazynier.contractor;

import magazynier.DAO;
import magazynier.Message;
import magazynier.worker.Worker;

import java.util.ArrayList;

public class ChatModel {

    private Worker recipient;
    private Worker sender;

    public ChatModel(Worker recipient, Worker sender) {
        this.recipient = recipient;
        this.sender = sender;
    }

    public void sendMessage(Message msg) {
        DAO.add(msg);
    }

    ArrayList<Message> getMessagesList() {
        return DAO.getMessagesBetween(recipient, sender);
    }

}
