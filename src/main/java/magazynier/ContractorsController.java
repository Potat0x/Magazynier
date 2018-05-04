package magazynier;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javassist.NotFoundException;
import magazynier.entities.Contractor;
import magazynier.utils.*;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class ContractorsController {

    private final int MAX_COMPANY_NAME_LENGTH = 50;
    private final int MAX_TEXT_FIELD_LENGTH = 25;

    public TextField firstName;
    public TextField lastName;
    public TextField contractorName;
    public TextField street;
    public TextField email;
    public TextField phone;
    public TextField nip;
    public ComboBox type;
    public TextField pesel;
    public TextField city;
    public Button addButton;
    public TableColumn typeCol;
    public TableColumn nameCol;
    public TableColumn firstNameCol;
    public TableColumn lastNameCol;
    public TableView contractorsTable;
    public GridPane form;
    public Button editButton;
    public Button cancelButton;
    public Button saveButton;
    public Button deleteButton;

    private ContractorsModel model;

    private boolean contractorAdding;
    private boolean contractorEditing;

    public ContractorsController() {
        model = new ContractorsModel();
        contractorAdding = false;
        contractorEditing = false;
    }

    private void refreshTable() {
        ArrayList contractors = model.getContractorsList();
        contractorsTable.getItems().clear();
        contractorsTable.getItems().addAll(contractors);
    }

    @FXML
    public void initialize() {
        final String company = "Firma";
        final String naturalPerson = "Osoba fizyczna";
        type.getItems().addAll(company, naturalPerson);

        contractorName.setDisable(true);
        nip.setDisable(true);
        pesel.setDisable(false);

        type.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                boolean isCompany = false;
                if (newValue != null) {
                    isCompany = newValue.equals(company);
                }
                contractorName.setDisable(!isCompany);
                nip.setDisable(!isCompany);
                pesel.setDisable(isCompany);
                //setEditMode(false);
            }
        });

        firstNameCol.setCellValueFactory(new PropertyValueFactory<Contractor, String>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<Contractor, String>("lastName"));

        typeCol.setCellValueFactory(new PropertyValueFactory<Contractor, String>("entityType"));

        nameCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Contractor, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Contractor, String> p) {
                if (p.getValue().getEntityType() != null && p.getValue().getEntityType().equals(naturalPerson))
                    return new ReadOnlyObjectWrapper(Optional.ofNullable(p.getValue().getFirstName()).orElse("") + " " + Optional.ofNullable(p.getValue().getLastName()).orElse(""));
                else {
                    return new ReadOnlyObjectWrapper(p.getValue().getContractorName());
                }
            }
        });

        contractorsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //deleteButton.setDisable(newValue == null);
            boolean rowNotSelected = newValue == null;
            form.setDisable(rowNotSelected);
            editButton.setDisable(rowNotSelected);
            deleteButton.setDisable(rowNotSelected);
//            saveButton.setDisable(rowNotSelected);
//            cancelButton.setDisable(rowNotSelected);
        });

        pesel.textProperty().addListener((observable, oldValue, newValue) -> {
            String textFieldErrorStyle = "-fx-text-box-border: rgb(255,117,0); -fx-focus-color: rgb(255,117,0);";
            if (!PeselValidator.check(pesel.getText())) {
                pesel.setStyle(textFieldErrorStyle);

            } else {
                pesel.setStyle(null);
            }
        });//todo: remove duplicated code

        nip.textProperty().addListener((observable, oldValue, newValue) -> {
            String textFieldErrorStyle = "-fx-text-box-border: rgb(255,117,0); -fx-focus-color: rgb(255,117,0);";
            if (!NipValidator.check(nip.getText())) {
                nip.setStyle(textFieldErrorStyle);
            } else {
                nip.setStyle(null);
                nip.setText(newValue.replaceAll("[- ]", ""));
            }
        });

        TextFieldOverflowIndicator.set(contractorName, MAX_COMPANY_NAME_LENGTH);
        TextFieldOverflowIndicator.set(firstName, MAX_TEXT_FIELD_LENGTH);
        TextFieldOverflowIndicator.set(lastName, MAX_TEXT_FIELD_LENGTH);
        TextFieldOverflowIndicator.set(phone, MAX_TEXT_FIELD_LENGTH);
        TextFieldOverflowIndicator.set(email, MAX_TEXT_FIELD_LENGTH);
        TextFieldOverflowIndicator.set(street, MAX_TEXT_FIELD_LENGTH);
        TextFieldOverflowIndicator.set(city, MAX_TEXT_FIELD_LENGTH);

        refreshTable();
        form.setDisable(true);
    }

    @FXML
    public void updateForm() {

        Contractor sc = (Contractor) contractorsTable.getSelectionModel().getSelectedItem();

        if (sc != null) {
            type.getSelectionModel().select(sc.getEntityType());
            firstName.setText(sc.getFirstName());
            lastName.setText(sc.getLastName());
            contractorName.setText(sc.getContractorName());
            phone.setText(sc.getPhone());
            email.setText(sc.getEmail());
            nip.setText(sc.getNip());
            pesel.setText(sc.getPesel());
            street.setText(sc.getStreet());
            city.setText(sc.getCity());
        }
/*        saveButton.setDisable(true);
        cancelButton.setDisable(true);*/
        FormCleaner.clearStyles(form);
    }

    public void updateSelectedContractor() {

        Contractor selectedContractor = (Contractor) contractorsTable.getSelectionModel().getSelectedItem();
        if (selectedContractor != null) {

            updateContractorFromForm(selectedContractor);
            try {
                model.updateContractor(selectedContractor);
                refreshTable();
                contractorsTable.getSelectionModel().select(selectedContractor);
            } catch (Exception e) {
                if (e instanceof NotFoundException) {
                    AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie można zaktualizować kontrahenta.", "Nie znalaziono kontrahenta. Mógł zostać usunięty z bazy.");
                } else {
                    AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie można zaktualizować kontrahenta.", "Nieznany błąd.");
                    e.printStackTrace();
                    System.out.println(e.getClass().getSimpleName());
                }
                refreshTable();
                FormCleaner.clearForm(form);
                updateForm();
            }
        }
    }

    public void deleteContractor() {

        Contractor selectedContractor = (Contractor) contractorsTable.getSelectionModel().getSelectedItem();
        if (selectedContractor != null) {
            try {
                model.deleteContractor(selectedContractor);
                contractorsTable.getItems().remove(selectedContractor);
                FormCleaner.clearForm(form);
                //clearFormStyles();
                contractorsTable.getSelectionModel().select(null);
            } catch (Exception e) {

                String failInfo;
                Contractor c = null;

                if (e instanceof PersistenceException) {
                    failInfo = "Kontrahent występuje na dokumentach.";
                    c = (Contractor) contractorsTable.getSelectionModel().getSelectedItem();
                } else if (e instanceof NotFoundException) {
                    failInfo = "Nie znaleziono kontrahenta. Mógł zostać wcześniej usunięty z bazy.";
                    FormCleaner.clearForm(form);
                    //clearFormStyles();
                } else {
                    failInfo = "Nieznany błąd";
                    e.printStackTrace();
                    System.out.println(e.getClass().getSimpleName());
                }
                AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie można usunąć kontrahenta.", failInfo);
                refreshTable();
                contractorsTable.getSelectionModel().select(c);
                FormCleaner.clearForm(form);
                updateForm();

            }
        }
        FormCleaner.clearStyles(form);
    }

    public void openEmailClient() {
    }

    public void showDocs() {
    }

    public void saveContractor() {
        boolean formLengthValid = validFormMaxLength();
        boolean peselValid = PeselValidator.check(pesel.getText()) || pesel.isDisabled();
        boolean nipValid = NipValidator.check(nip.getText()) || nip.isDisabled();

        if (pesel.isDisabled()) {
            pesel.clear();
        }
        if (nip.isDisabled()) {
            nip.clear();
        }

       /* peselValid = true;
        nipValid = true;*/

        if (formLengthValid && nipValid && peselValid) {

            if (contractorAdding) {
                Contractor newContractor = new Contractor();
                updateContractorFromForm(newContractor);

                model.addContractor(newContractor);
                contractorsTable.getItems().add(newContractor);
                contractorsTable.getSelectionModel().select(newContractor);
                contractorAdding = false;
            } else if (contractorEditing) {
                updateSelectedContractor();
                contractorEditing = false;
            }

            setEditMode(false);
            setFormActive(false);
            FormCleaner.clearStyles(form);
        } else {
            if (!formLengthValid) {
                AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Wprowadzony tekst jest za długi.\n" +
                        "Maksymalna długość nazwy firmy: 50 znaków.\nPozostałe pola: 25 znaków");
            } else if (!peselValid) {
                AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Wprowadzony numer PESEL jest niepoprawny.");
            } else {
                AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Wprowadzony numer NIP jest niepoprawny.");
            }//todo: enum with alert messages
        }
    }

    public void beginContractorAdding() {
        contractorAdding = true;
        setEditMode(true);
        setFormActive(true);
        form.setDisable(false);
        //contractorsTable.getSelectionModel().select(contractorsTable.getSelectionModel().getSelectedItem());
        FormCleaner.clearForm(form);
        FormCleaner.clearStyles(form);
    }

    public void beginContractorEditing() {
        contractorEditing = true;
        setEditMode(true);
        setFormActive(true);
        contractorsTable.getSelectionModel().select(contractorsTable.getSelectionModel().getSelectedItem());
    }

    public void cancelContractorAddOrEdit() {
        contractorAdding = false;
        contractorEditing = false;
        setEditMode(false);
        setFormActive(false);
        FormCleaner.clearForm(form);
        updateForm();
    }

    private void updateContractorFromForm(Contractor contractor) {
        contractor.setContractorName(contractorName.getText());
        contractor.setFirstName(firstName.getText());
        contractor.setLastName(lastName.getText());
        contractor.setPhone(phone.getText());
        contractor.setEmail(email.getText());
        contractor.setPesel(pesel.getText());
        contractor.setNip(nip.getText());
        contractor.setStreet(street.getText());
        contractor.setCity(city.getText());
        contractor.setEntityType((String) type.getSelectionModel().getSelectedItem());
    }

    private boolean validFormMaxLength() {

        if ((firstName.getText() == null || firstName.getText().length() <= MAX_TEXT_FIELD_LENGTH) &&
                (lastName.getText() == null || lastName.getText().length() <= MAX_TEXT_FIELD_LENGTH) &&
                (contractorName.getText() == null || contractorName.getText().length() <= MAX_COMPANY_NAME_LENGTH) &&
                (city.getText() == null || city.getText().length() <= MAX_TEXT_FIELD_LENGTH) &&
                (street.getText() == null || street.getText().length() <= MAX_TEXT_FIELD_LENGTH) &&
                (email.getText() == null || email.getText().length() <= MAX_TEXT_FIELD_LENGTH) &&
                (phone.getText() == null || phone.getText().length() <= MAX_TEXT_FIELD_LENGTH)
                ) {
            return true;
        }
        return false;
    }

    private void setFormActive(boolean active) {
        for (Node n : form.getChildren()) {
            if (n instanceof ComboBox) {
                n.setDisable(!active);
            } else if (n instanceof TextField) {
                ((TextField) n).setEditable(active);
            }
        }
    }

    private void setEditMode(boolean editMode) {
        addButton.setDisable(editMode);
        saveButton.setDisable(!editMode);
        cancelButton.setDisable(!editMode);

        if (contractorsTable.getSelectionModel().getSelectedItem() != null) {
            editButton.setDisable(editMode);
            deleteButton.setDisable(editMode);
        }

        contractorsTable.setDisable(editMode);
    }

}
