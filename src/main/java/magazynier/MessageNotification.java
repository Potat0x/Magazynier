package magazynier;


import magazynier.worker.Worker;

import java.io.Serializable;

public class MessageNotification implements Serializable {

    private Worker sender;
    private Worker recipient;
    private Character ack;

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

    public Character getAck() {
        return ack;
    }

    public void setAck(Character ack) {
        this.ack = ack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageNotification)) return false;

        MessageNotification that = (MessageNotification) o;

        if (sender != null ? !sender.equals(that.sender) : that.sender != null) return false;
        //noinspection SimplifiableIfStatement
        if (recipient != null ? !recipient.equals(that.recipient) : that.recipient != null) return false;
        return ack != null ? ack.equals(that.ack) : that.ack == null;
    }

    @Override
    public int hashCode() {
        int result = sender != null ? sender.hashCode() : 0;
        result = 31 * result + (recipient != null ? recipient.hashCode() : 0);
        result = 31 * result + (ack != null ? ack.hashCode() : 0);
        return result;
    }
}
