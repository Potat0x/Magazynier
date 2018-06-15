package magazynier.worker;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import magazynier.Message;
import magazynier.MessageNotification;
import org.hibernate.Hibernate;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ChatController {

    public Label workerNameLabel;
    public ScrollPane scrollPane;
    public TextArea messageArea;
    public TextFlow chatArea;

    private Worker recipient;
    private Worker thisWorker;

    private ChatModel model;
    private String previousMsgDay;
    private Timer timer;
    private Thread refreshThread;

    public ChatController(Worker recipient, Worker thisWorker) {
        this.recipient = recipient;
        this.thisWorker = thisWorker;

        model = new ChatModel(recipient, thisWorker);
        refreshThread = new Thread(this::refresh);
        refreshThread.setDaemon(true);
    }

    public void initialize() {
        messageArea.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && event.isControlDown()) {
                sendMessage();
            }
        });

        chatArea.heightProperty().addListener(el -> scrollPane.setVvalue(1.0));
        workerNameLabel.setText(recipient.getFullName());
        workerNameLabel.setStyle("-fx-text-fill: rgb(24, 0, 163);");
        refresh();

        int REFRESHING_PERIOD = 1000;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                MessageNotification msgNotification = model.isConversationOutdated(thisWorker, recipient);

                if (msgNotification != null) {
                    if (msgNotification.getAck() == 'N') {
                        Platform.runLater(refreshThread);
                    }
                    model.consumeNotification(msgNotification);
                }
            }
        }, 0, REFRESHING_PERIOD);
        //System.out.println("messageArea: " + messageArea.getParent().getScene().getWindow());
    }

    private void insertMessage(Message msg) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String messageDate = timeFormat.format(msg.getDate());

        SimpleDateFormat dayFormat = new SimpleDateFormat("dd.MM.yyyy");
        String currentMsgDay = dayFormat.format(msg.getDate());

        if (previousMsgDay == null || !previousMsgDay.equals(currentMsgDay)) {
            Text dateTxt = new Text(currentMsgDay + "\n");
            dateTxt.setStyle("-fx-fill: rgba(137, 66, 0, 0.9);");
            chatArea.getChildren().add(dateTxt);
        }
        previousMsgDay = currentMsgDay;

        Text timeTxt = new Text(messageDate + " ");
        timeTxt.setStyle("-fx-fill: rgba(137, 66, 0, 0.5);");

        Text authorTxt = new Text(msg.getSender().toString() + ": ");
        //if (msg.getSender().getId().equals(thisWorker.getId())) {
        Worker sender = msg.getSender();
        Hibernate.initialize(sender);
        Hibernate.initialize(thisWorker);
//        if (msg.getSender().getId().equals(thisWorker.getId())) {//todo: ??
        if (Hibernate.unproxy(sender).equals(Hibernate.unproxy(thisWorker))) {
            authorTxt.setStyle("-fx-fill: rgb(10, 86, 0);");
        } else {
            authorTxt.setStyle("-fx-fill: rgb(24, 0, 163);");
        }

        Text msgTxt = new Text(msg.getMessage());
        msgTxt.setStyle("-fx-fill: rgb(45, 6, 0);");

        chatArea.getChildren().add(timeTxt);
        chatArea.getChildren().add(authorTxt);
        chatArea.getChildren().add(msgTxt);
        chatArea.getChildren().add(new Text("\n"));
        chatArea.getChildren().forEach(node -> {
            if (node instanceof Text) {
                node.setStyle(node.getStyle() + " -fx-font-size: 13px; -fx-font-weight:normal;");
            }
        });
    }

    @FXML
    public void sendMessage() {
        String messageText = messageArea.getText().trim();

        if (!messageText.isEmpty()) {
            System.out.println("send: " + messageText);

            Message newMessage = new Message(Timestamp.valueOf(LocalDateTime.now()), messageText, thisWorker, recipient);
            model.sendMessage(newMessage);
            messageArea.clear();
            messageArea.requestFocus();
            insertMessage(newMessage);
        }
    }

    @FXML
    public void refresh() {
        chatArea.getChildren().clear();
        ArrayList<Message> messages = model.getMessagesList();
        //noinspection ComparatorMethodParameterNotUsed
        messages.sort((m1, m2) -> (m1.getDate().before(m2.getDate())) ? -1 : 1);
        previousMsgDay = null;
        messages.forEach(this::insertMessage);
    }

    @FXML
    public void switchFullscreen() {
        getStage().setFullScreen(!getStage().isFullScreen());
    }

    @FXML
    public void closeChat() {
        cancelRefreshing();
        getStage().close();
    }

    private Stage getStage() {
        return ((Stage) chatArea.getScene().getWindow());
    }

    @FXML
    public void cancelRefreshing() {
        timer.cancel();
        timer.purge();
    }
}
