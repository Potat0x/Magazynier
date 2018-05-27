package magazynier.contractor;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import magazynier.ContractorType;
import magazynier.RowNotFoundException;
import magazynier.utils.AlertLauncher;
import magazynier.utils.FormCleaner;
import magazynier.utils.TextFieldCorrectnessIndicator;
import magazynier.utils.validators.LengthValidator;
import magazynier.utils.validators.NipValidator;
import magazynier.utils.validators.PeselValidator;

import javax.persistence.PersistenceException;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Optional;

public class ContractorsController {

    public TextField firstName;
    public TextField lastName;
    public TextField contractorName;
    public TextField street;
    public TextField email;
    public TextField phone;
    public TextField nip;
    public ComboBox<ContractorType> type;
    public TextField pesel;
    public TextField city;
    public Button addButton;
    public TableView<Contractor> contractorsTable;
    public TableColumn<Contractor, ContractorType> typeCol;
    public TableColumn<Contractor, String> nameCol;
    public TableColumn<Contractor, String> firstNameCol;
    public TableColumn<Contractor, String> lastNameCol;
    public GridPane form;
    public Button editButton;
    public Button cancelButton;
    public Button saveButton;
    public Button deleteButton;
    public Button emailButton;

    private ContractorsModel model;

    private boolean contractorAdding;
    private boolean contractorEditing;

    private PeselValidator peselValidator;
    private NipValidator nipValidator;

    private final int MAX_COMPANY_NAME_LENGTH = 100;
    private final int MAX_TEXT_FIELD_LENGTH = 32;

    private final String COMPANY = "Firma";
    private final String NATURAL_PERSON = "Osoba fizyczna";

    public ContractorsController() {
        model = new ContractorsModel();
        peselValidator = new PeselValidator();
        nipValidator = new NipValidator();
        contractorAdding = false;
        contractorEditing = false;
    }

    private void refreshTable() {
        ArrayList<Contractor> contractors = model.getContractorsList();
        contractorsTable.getItems().clear();
        contractorsTable.getItems().addAll(contractors);
    }

    private void initializeContractorTypeCmbox() {
        if (type.getItems().size() > 0) {
            if (type.getSelectionModel().getSelectedItem() == null) {
                type.getSelectionModel().select(type.getItems().get(0));
            }
        }
    }

    @FXML
    public void initialize() {

        type.getItems().addAll(model.getContractorTypesList());
        initializeContractorTypeCmbox();

        contractorName.setDisable(true);
        nip.setDisable(true);
        pesel.setDisable(false);

        type.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            boolean isCompany = false;
            if (newValue != null) {
                isCompany = newValue.toString().equals(COMPANY);
            }
            contractorName.setDisable(!isCompany);
            nip.setDisable(!isCompany);
            pesel.setDisable(isCompany);
            //setEditMode(false);
        });

        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        typeCol.setCellValueFactory(new PropertyValueFactory<>("contractorType"));

        nameCol.setCellValueFactory(p -> {
            if (p.getValue().getEntityType() != null && p.getValue().getEntityType().equals(NATURAL_PERSON))
                return new ReadOnlyObjectWrapper<>(Optional.ofNullable(p.getValue().getFirstName()).orElse("") + " " + Optional.ofNullable(p.getValue().getLastName()).orElse(""));
            else {
                return new ReadOnlyObjectWrapper<>(p.getValue().getContractorName());
            }
        });

        contractorsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //deleteButton.setDisable(newValue == null);
            boolean rowNotSelected = newValue == null;
            form.setDisable(rowNotSelected);
            editButton.setDisable(rowNotSelected);
            deleteButton.setDisable(rowNotSelected);
            emailButton.setDisable(rowNotSelected);
//            saveButton.setDisable(rowNotSelected);
//            cancelButton.setDisable(rowNotSelected);
        });

        pesel.textProperty().addListener(new TextFieldCorrectnessIndicator(new PeselValidator()));
        nip.textProperty().addListener(new TextFieldCorrectnessIndicator(new NipValidator()));

        TextField[] fieldsToValidate = {
                contractorName,
                firstName,
                lastName,
                phone,
                email,
                street,
                city
        };

        for (TextField textField : fieldsToValidate) {
            textField.textProperty().addListener(new TextFieldCorrectnessIndicator(new LengthValidator(MAX_TEXT_FIELD_LENGTH)));
        }
        refreshTable();
        form.setDisable(true);
    }

    @FXML
    public void updateForm() {

        Contractor sc = contractorsTable.getSelectionModel().getSelectedItem();

        if (sc != null) {
            type.getSelectionModel().select(sc.getContractorType());//todo: sc.contractorname?
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

        Contractor selectedContractor = contractorsTable.getSelectionModel().getSelectedItem();
        if (selectedContractor != null) {

            updateContractorFromForm(selectedContractor);
            try {
                model.updateContractor(selectedContractor);
                refreshTable();
                contractorsTable.getSelectionModel().select(selectedContractor);
            } catch (Exception e) {
                if (e instanceof RowNotFoundException) {
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

        Contractor selectedContractor = contractorsTable.getSelectionModel().getSelectedItem();
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
                    c = contractorsTable.getSelectionModel().getSelectedItem();
                } else if (e instanceof RowNotFoundException) {
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

    @FXML
    public void openEmailClient() {

        if (email.getText() != null && email.getText().contains("@")) {
            try {
                Desktop.getDesktop().mail(URI.create("mailto:" + email.getText()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void showDocs() {
    }

    public void saveContractor() {
        boolean formLengthValid = validFormMaxLength();
        boolean peselValid = peselValidator.check(pesel.getText()) || pesel.isDisabled();
        boolean nipValid = nipValidator.check(nip.getText()) || nip.isDisabled();

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
                        "Maksymalna długość nazwy firmy: " + MAX_COMPANY_NAME_LENGTH + " znaków.\nPozostałe pola: " + MAX_TEXT_FIELD_LENGTH + " znaków");
            } else if (!peselValid) {
                AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Wprowadzony numer PESEL jest niepoprawny.");
            } else {
                AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Wprowadzony numer NIP jest niepoprawny.");
            }//todo: enum with alert messages(?)
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
        initializeContractorTypeCmbox();
        //type.getSelectionModel().select(COMPANY);
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
        contractor.setNip(nip.getText().replaceAll("[- ]", ""));
        contractor.setStreet(street.getText());
        contractor.setCity(city.getText());
        contractor.setContractorType(type.getSelectionModel().getSelectedItem());
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
            emailButton.setDisable(editMode);
        }

        contractorsTable.setDisable(editMode);
    }

}
