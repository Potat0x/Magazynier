package magazynier;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import magazynier.entities.Contractor;

import java.util.ArrayList;

public class ContractorsController {

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

    private ContractorsModel model;

    public ContractorsController() {
        model = new ContractorsModel();
    }

    private void refreshTable() {
        ArrayList workers = model.getContractorsList();
        contractorsTable.getItems().clear();
        contractorsTable.getItems().addAll(workers);
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
                boolean isCompany = newValue.equals(company);
                contractorName.setDisable(!isCompany);
                nip.setDisable(!isCompany);
                pesel.setDisable(isCompany);
            }
        });

        firstNameCol.setCellValueFactory(new PropertyValueFactory<Contractor, String>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<Contractor, String>("lastName"));

        typeCol.setCellValueFactory(new PropertyValueFactory<Contractor, String>("entityType"));

        nameCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Contractor, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Contractor, String> p) {
                if (p.getValue().getEntityType().equals(naturalPerson))
                    return new ReadOnlyObjectWrapper(p.getValue().getFirstName() + " " + p.getValue().getLastName());
                else {
                    //System.out.println("ELSE: " + p.getValue().getContractorType());
                    return new ReadOnlyObjectWrapper(p.getValue().getContractorName());
                }
            }
        });

        refreshTable();
        form.setDisable(true);
    }

    public void updateSelectedContractor() {
    }

    public void beginContractorAdding() {
    }
}
