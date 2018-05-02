package magazynier;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import magazynier.entities.Worker;

import java.util.ArrayList;

public class WorkersController {

    public TableView workersTable;
    public TableColumn firstNameCol;
    public TableColumn lastNameCol;
    public TableColumn warehouseCol;
    public TextField firstName;
    public TextField lastName;
    public TextField phone;
    public TextField email;
    public TextField pesel;
    public TextField street;
    public TextField city;
    public GridPane form;
    public Button cancelButton;
    public Button addButton;
    public Button deleteButton;
    public Button saveButton;

    private WorkersModel model;
    private boolean userAdding;

    private final int MAX_TEXT_FIELD_LENGTH = 25;
    private final int MAX_TEXT_FIELD_LENGTH_PESEL = 11;

    public WorkersController() {
        model = new WorkersModel();
        userAdding = false;
    }

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        firstNameCol.setCellValueFactory(new PropertyValueFactory<Worker, String>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<Worker, String>("lastName"));
        warehouseCol.setCellValueFactory(new PropertyValueFactory<Worker, Integer>("warehouseId"));

        ArrayList workers = model.getWorkersList();
        workersTable.getItems().addAll(workers);

        TextFieldOverflowIndicator.set(firstName, MAX_TEXT_FIELD_LENGTH);
        TextFieldOverflowIndicator.set(lastName, MAX_TEXT_FIELD_LENGTH);
        TextFieldOverflowIndicator.set(phone, MAX_TEXT_FIELD_LENGTH);
        TextFieldOverflowIndicator.set(email, MAX_TEXT_FIELD_LENGTH);
        TextFieldOverflowIndicator.set(pesel, MAX_TEXT_FIELD_LENGTH);
        TextFieldOverflowIndicator.set(street, MAX_TEXT_FIELD_LENGTH);
        TextFieldOverflowIndicator.set(city, MAX_TEXT_FIELD_LENGTH);
        TextFieldOverflowIndicator.set(pesel, MAX_TEXT_FIELD_LENGTH_PESEL);
    }

    @FXML
    public void updateForm() {

        Worker selectedWorker = (Worker) workersTable.getSelectionModel().getSelectedItem();

        if (selectedWorker != null) {
            firstName.setText(selectedWorker.getFirstName());
            lastName.setText(selectedWorker.getLastName());
            phone.setText(selectedWorker.getPhone());
            email.setText(selectedWorker.getEmail());

            pesel.setText(selectedWorker.getPesel());
            street.setText(selectedWorker.getStreet());
            city.setText(selectedWorker.getCity());
        }
    }

    @FXML
    public void updateSelectedWorker() {
        Worker selectedWorker = (Worker) workersTable.getSelectionModel().getSelectedItem();

        if (selectedWorker != null) {
            updateWorkerFromForm(selectedWorker);
            model.updateWorker(selectedWorker);
            workersTable.refresh();
        }
    }

    @FXML
    @SuppressWarnings("unchecked")
    public void saveUser() {

        if (validFormMaxLength()) {
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
            deleteButton.setDisable(false);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Wprowadzono nieprawidłowe dane.");
            alert.setContentText("Maksymalna dlugosc pola: 25.");
            alert.showAndWait();
        }
    }

    @FXML
    public void beginUserAdding() {
        userAdding = true;
        workersTable.setDisable(true);
        cancelButton.setDisable(false);
        addButton.setDisable(true);
        deleteButton.setDisable(true);
        //noinspection unchecked
        workersTable.getSelectionModel().select(null);
        clearForm();
    }

    @FXML
    public void cancelUserAddOrEdit() {

        if (userAdding) {
            clearForm();
        } else {
            updateForm();
        }

        userAdding = false;

        workersTable.setDisable(false);
        cancelButton.setDisable(true);
        saveButton.setDisable(true);
        addButton.setDisable(false);
        deleteButton.setDisable(false);
    }

    @FXML
    public void deleteWorker() {

        Worker selectedWorker = (Worker) workersTable.getSelectionModel().getSelectedItem();
        if (selectedWorker != null) {
            workersTable.getItems().remove(selectedWorker);
            model.deleteWorker(selectedWorker);
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

        if (firstName.getText().length() <= MAX_TEXT_FIELD_LENGTH &&
                lastName.getText().length() <= MAX_TEXT_FIELD_LENGTH &&
                city.getText().length() <= MAX_TEXT_FIELD_LENGTH &&
                street.getText().length() <= MAX_TEXT_FIELD_LENGTH &&
                email.getText().length() <= MAX_TEXT_FIELD_LENGTH &&
                phone.getText().length() <= MAX_TEXT_FIELD_LENGTH &&
                pesel.getText().length() <= MAX_TEXT_FIELD_LENGTH_PESEL
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

    private void clearForm() {
        for (Node n : form.getChildren()) {
            if (n instanceof TextField) {
                ((TextField) n).clear();
            }
        }
    }
}
