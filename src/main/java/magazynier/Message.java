package magazynier;


import magazynier.worker.Worker;

import java.sql.Timestamp;

public class Message {

    private Integer id;
    private Timestamp date;
    private String message;
    private Worker sender;
    private Worker recipient;

    public Message() {
    }

    public Message(Timestamp date, String message, Worker sender, Worker recipient) {
        this.date = date;
        this.message = message;
        this.sender = sender;
        this.recipient = recipient;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Worker getSender() {
        return sender;
    }

    public void setSender(Worker sender) {
        this.sender = sender;
    }

    public Worker getRecipient() {
        return recipient;
    }

    public void setRecipient(Worker recipient) {
        this.recipient = recipient;
    }
}
