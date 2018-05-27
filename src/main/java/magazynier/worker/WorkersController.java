package magazynier.worker;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import magazynier.MessageNotification;
import magazynier.RowNotFoundException;
import magazynier.contractor.ChatController;
import magazynier.utils.AlertLauncher;
import magazynier.utils.FormCleaner;
import magazynier.utils.TextFieldCorrectnessIndicator;
import magazynier.utils.validators.LengthValidator;
import magazynier.utils.validators.PeselValidator;

import javax.persistence.PersistenceException;
import java.io.IOException;
import java.util.ArrayList;

import static magazynier.utils.WindowCreator.createWindowFromFxml;

public class WorkersController {

    public TableView<Worker> workersTable;
    public TableColumn<Worker, String> firstNameCol;
    public TableColumn<Worker, String> lastNameCol;
    public TableColumn<Worker, Integer> warehouseCol;

    public GridPane form;
    public TextField firstName;
    public TextField lastName;
    public TextField phone;
    public TextField email;
    public TextField pesel;
    public TextField street;
    public TextField city;

    public Button cancelButton;
    public Button addButton;
    public Button chatButton;
    public Button deleteButton;
    public Button saveButton;
    public Label formTitle;
    public ComboBox<Worker> appUserTmpCmbox;
    public ListView<MessageNotification> msgNotificationsList;

    private PeselValidator peselValidator;
    private WorkersModel model;
    private boolean userAdding;

    private final int MAX_TEXT_FIELD_LENGTH = 32;

    public WorkersController() {
        model = new WorkersModel();
        peselValidator = new PeselValidator();
        userAdding = false;
    }

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        warehouseCol.setCellValueFactory(new PropertyValueFactory<>("warehouseId"));

        refreshTable();

        TextField[] fieldsToValidate = {
                firstName,
                lastName,
                phone,
                email,
                pesel,
                street,
                city
        };
        for (TextField textField : fieldsToValidate) {
            textField.textProperty().addListener(new TextFieldCorrectnessIndicator(new LengthValidator(MAX_TEXT_FIELD_LENGTH)));
        }

        pesel.textProperty().addListener(new TextFieldCorrectnessIndicator(new PeselValidator()));

        workersTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            deleteButton.setDisable(newValue == null);
            chatButton.setDisable(newValue == null);
            form.setDisable(newValue == null);
        });
        deleteButton.setDisable(true);
        chatButton.setDisable(true);
        form.setDisable(true);
        formTitle.setText("Informacje o pracowniku:");

        //chat
        appUserTmpCmbox.valueProperty().addListener(a -> refreshNotifications());

        msgNotificationsList.setCellFactory(new Callback<ListView<MessageNotification>, ListCell<MessageNotification>>() {
            @Override
            public ListCell<MessageNotification> call(ListView<MessageNotification> param) {

                ContextMenu cm = new ContextMenu();
                MenuItem hideNotification = new MenuItem("Ok, ukryj");
                MenuItem showConversation = new MenuItem("Otwórz rozmowę");

                cm.getItems().addAll(hideNotification, showConversation);
                ListCell<MessageNotification> lc = new ListCell<MessageNotification>() {
                    @Override
                    protected void updateItem(MessageNotification msgNtf, boolean bln) {

                        if (msgNtf != null) {
                            super.updateItem(msgNtf, bln);
                            setText(msgNtf.getSender().getFullName());
                            showConversation.setOnAction(e -> {
                                consumeNotification(msgNtf);
                                showChatWindow(msgNtf.getSender(), msgNtf.getRecipient());
                            });
                            hideNotification.setOnAction(e -> consumeNotification(msgNtf));
                        } else {
                            setGraphic(null);
                            setText(null);
                        }
                    }
                };

                lc.setContextMenu(cm);
                return lc;
            }
        });

        refreshTmpCmbox();
    }

    private void refreshTable() {
        ArrayList<Worker> workers = model.getWorkersList();
        workersTable.getItems().clear();
        workersTable.getItems().addAll(workers);
    }

    @FXML
    public void updateForm() {

        Worker selectedWorker = workersTable.getSelectionModel().getSelectedItem();

        if (selectedWorker != null) {
            firstName.setText(selectedWorker.getFirstName());
            lastName.setText(selectedWorker.getLastName());
            phone.setText(selectedWorker.getPhone());
            email.setText(selectedWorker.getEmail());

            pesel.setText(selectedWorker.getPesel());
            street.setText(selectedWorker.getStreet());
            city.setText(selectedWorker.getCity());
        }

        saveButton.setDisable(true);
        cancelButton.setDisable(true);
        FormCleaner.clearStyles(form);//--todo: delete this line before release
    }

    @FXML
    public void updateSelectedWorker() {
        Worker selectedWorker = workersTable.getSelectionModel().getSelectedItem();

        if (selectedWorker != null) {
            updateWorkerFromForm(selectedWorker);
            try {
                model.updateWorker(selectedWorker);
            } catch (Exception e) {


                if (e instanceof RowNotFoundException) {
                    AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie można zaktualizować pracownika.", "Nie znalaziono pracownika. Mógł zostać usunięty z bazy.");
                } else {
                    AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Nieznany błąd.");
                    e.printStackTrace();
                    System.out.println(e.getClass().getSimpleName());
                }

                refreshTable();
                FormCleaner.clearForm(form);
                FormCleaner.clearStyles(form);
            }
        }
    }

    @FXML
    @SuppressWarnings("unchecked")
    public void saveUser() {

        boolean formLengthValid = validFormMaxLength();
        boolean peselValid = peselValidator.check(pesel.getText());

        if (formLengthValid && peselValid) {
            if (userAdding) {
                Worker newWorker = new Worker();
                updateWorkerFromForm(newWorker);

                model.addWorker(newWorker);
                workersTable.getItems().add(newWorker);
                workersTable.getSelectionModel().select(newWorker);
                userAdding = false;
            } else {
                updateSelectedWorker();
            }

            workersTable.setDisable(false);
            addButton.setDisable(false);
            cancelButton.setDisable(true);
            saveButton.setDisable(true);
            //deleteButton.setDisable(false);
            workersTable.refresh();
            refreshTmpCmbox();
            formTitle.setText("Informacje o pracowniku:");
        } else {
            if (!formLengthValid) {
                AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Maksymalna dlugosc pola: " + MAX_TEXT_FIELD_LENGTH + ".");
            } else {
                AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Wprowadzony numer PESEL jest niepoprawny.");
            }
        }
    }

    @FXML
    public void beginUserAdding() {
        userAdding = true;
        workersTable.setDisable(true);
        cancelButton.setDisable(false);
        addButton.setDisable(true);
        //noinspection unchecked
        workersTable.getSelectionModel().select(null);
        FormCleaner.clearForm(form);
        FormCleaner.clearStyles(form);
        form.setDisable(false);
        formTitle.setText("Nowy pracownik:");
    }

    @FXML
    public void cancelUserAddOrEdit() {

        formTitle.setText("Informacje o pracowniku:");

        if (userAdding) {
            FormCleaner.clearForm(form);
        } else {
            updateForm();
        }

        userAdding = false;
        workersTable.setDisable(false);
        cancelButton.setDisable(true);
        saveButton.setDisable(true);
        addButton.setDisable(false);
        //form.setDisable(true);
        //deleteButton.setDisable(false);
        FormCleaner.clearStyles(form);
    }

    @FXML
    public void deleteWorker() {

        Worker selectedWorker = workersTable.getSelectionModel().getSelectedItem();
        if (selectedWorker != null) {
            try {
                model.deleteWorker(selectedWorker);
                workersTable.getItems().remove(selectedWorker);
                FormCleaner.clearForm(form);
                FormCleaner.clearStyles(form);
                workersTable.getSelectionModel().select(null);
                refreshTmpCmbox();
            } catch (Exception e) {

                String failInfo;
                Worker w = null;

                if (e instanceof PersistenceException) {
                    failInfo = "Pracownik występuje na dokumentach.";
                    w = workersTable.getSelectionModel().getSelectedItem();
                } else if (e instanceof RowNotFoundException) {
                    failInfo = "Nie znaleziono pracownika. Mógł zostać wcześniej usunięty z bazy.";
                    FormCleaner.clearForm(form);
                    FormCleaner.clearStyles(form);
                } else {
                    failInfo = "Nieznany błąd";
                    e.printStackTrace();
                    System.out.println(e.getClass().getSimpleName());
                }
                AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie można usunąć pracownika.", failInfo);
                refreshTable();
                workersTable.getSelectionModel().select(w);
            }
        }
    }

    @FXML
    public void setFormOutOfDate() {

        if (userAdding || workersTable.getSelectionModel().getSelectedItem() != null) {
            saveButton.setDisable(false);
            cancelButton.setDisable(false);
        }
    }

    private boolean validFormMaxLength() {
        //noinspection RedundantIfStatement
        if ((firstName.getText() == null || firstName.getText().length() <= MAX_TEXT_FIELD_LENGTH) &&
                (lastName.getText() == null || lastName.getText().length() <= MAX_TEXT_FIELD_LENGTH) &&
                (city.getText() == null || city.getText().length() <= MAX_TEXT_FIELD_LENGTH) &&
                (street.getText() == null || street.getText().length() <= MAX_TEXT_FIELD_LENGTH) &&
                (email.getText() == null || email.getText().length() <= MAX_TEXT_FIELD_LENGTH) &&
                (phone.getText() == null || phone.getText().length() <= MAX_TEXT_FIELD_LENGTH)
                ) {
            return true;
        }
        return false;
    }

    private void updateWorkerFromForm(Worker worker) {
        worker.setFirstName(firstName.getText());
        worker.setLastName(lastName.getText());
        worker.setPhone(phone.getText());
        worker.setEmail(email.getText());
        worker.setPesel(pesel.getText());
        worker.setStreet(street.getText());
        worker.setCity(city.getText());
    }

    //chat = = = = =
    private void refreshTmpCmbox() {
        ArrayList<Worker> workers = model.getWorkersList();

        if (!model.getWorkersList().isEmpty()) {
            appUserTmpCmbox.getItems().clear();
            appUserTmpCmbox.getItems().addAll(workers);
            appUserTmpCmbox.setValue(workers.get(0));
        }

        refreshNotifications();
    }

    private void showChatWindow(Worker selectedWorker, Worker currentWorker) {
        if (selectedWorker != null && currentWorker != null) {
            ChatController chatController = new ChatController(selectedWorker, currentWorker);
            try {
                Stage chatStage = createWindowFromFxml("/fxml/chat.fxml", chatController, "Komunikator");
                chatStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void startChat() {

        Worker selectedWorker = workersTable.getSelectionModel().getSelectedItem();
        Worker currentWorker = appUserTmpCmbox.getSelectionModel().getSelectedItem();

        showChatWindow(selectedWorker, currentWorker);
    }

    public void refreshNotifications() {
        Worker currentWorker = appUserTmpCmbox.getSelectionModel().getSelectedItem();
        msgNotificationsList.getItems().clear();
        msgNotificationsList.getItems().addAll(model.getNotificationsList(currentWorker));
    }

    private void consumeNotification(MessageNotification msgNtf) {
        model.consumeNotification(msgNtf);
        msgNotificationsList.getItems().remove(msgNtf);
        msgNotificationsList.refresh();
    }
}
