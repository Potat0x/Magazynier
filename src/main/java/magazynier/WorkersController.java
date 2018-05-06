package magazynier;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javassist.NotFoundException;
import magazynier.entities.Worker;
import magazynier.utils.*;
import magazynier.utils.validators.PeselValidator;

import javax.persistence.PersistenceException;
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
    public Label formTitle;

    private PeselValidator peselValidator;
    private WorkersModel model;
    private boolean userAdding;

    private final int MAX_TEXT_FIELD_LENGTH = 25;

    public WorkersController() {
        model = new WorkersModel();
        peselValidator = new PeselValidator();
        userAdding = false;
    }

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        firstNameCol.setCellValueFactory(new PropertyValueFactory<Worker, String>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<Worker, String>("lastName"));
        warehouseCol.setCellValueFactory(new PropertyValueFactory<Worker, Integer>("warehouseId"));

        refreshTable();

        TextFieldOverflowIndicator.set(firstName, MAX_TEXT_FIELD_LENGTH);
        TextFieldOverflowIndicator.set(lastName, MAX_TEXT_FIELD_LENGTH);
        TextFieldOverflowIndicator.set(phone, MAX_TEXT_FIELD_LENGTH);
        TextFieldOverflowIndicator.set(email, MAX_TEXT_FIELD_LENGTH);
        TextFieldOverflowIndicator.set(pesel, MAX_TEXT_FIELD_LENGTH);
        TextFieldOverflowIndicator.set(street, MAX_TEXT_FIELD_LENGTH);
        TextFieldOverflowIndicator.set(city, MAX_TEXT_FIELD_LENGTH);
        //TextFieldOverflowIndicator.set(pesel, MAX_TEXT_FIELD_LENGTH_PESEL);

        pesel.textProperty().addListener(new TextFieldCorrectnessIndicator(new PeselValidator()));

        workersTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            deleteButton.setDisable(newValue == null);
            form.setDisable(newValue == null);
        });
        deleteButton.setDisable(true);
        form.setDisable(true);
        formTitle.setText("Informacje o pracowniku:");
    }

    private void refreshTable() {
        ArrayList workers = model.getWorkersList();
        workersTable.getItems().clear();
        workersTable.getItems().addAll(workers);
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

        saveButton.setDisable(true);
        cancelButton.setDisable(true);
        FormCleaner.clearStyles(form);//--todo: delete this line before release
    }

    @FXML
    public void updateSelectedWorker() {
        Worker selectedWorker = (Worker) workersTable.getSelectionModel().getSelectedItem();

        if (selectedWorker != null) {
            updateWorkerFromForm(selectedWorker);
            try {
                model.updateWorker(selectedWorker);
            } catch (Exception e) {


                if (e instanceof NotFoundException) {
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

        Worker selectedWorker = (Worker) workersTable.getSelectionModel().getSelectedItem();
        if (selectedWorker != null) {
            try {
                model.deleteWorker(selectedWorker);
                workersTable.getItems().remove(selectedWorker);
                FormCleaner.clearForm(form);
                FormCleaner.clearStyles(form);
                workersTable.getSelectionModel().select(null);
            } catch (Exception e) {

                String failInfo;
                Worker w = null;

                if (e instanceof PersistenceException) {
                    failInfo = "Pracownik występuje na dokumentach.";
                    w = (Worker) workersTable.getSelectionModel().getSelectedItem();
                } else if (e instanceof NotFoundException) {
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
}
