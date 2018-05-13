package magazynier;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import magazynier.contractor.Contractor;
import magazynier.item.Item;
import magazynier.utils.TextFieldCorrectnessIndicator;
import magazynier.utils.TextFieldOverflowIndicator;
import magazynier.worker.Worker;

import java.sql.Date;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class DocumentPropertiesController {


    public enum Mode {
        ADD_ITEM,
        EDIT_ITEM
    }

    public enum ActionResult {
        CONFIRM,
        CANCEL,
        FAIL
    }

    public DatePicker date;
    public TextField name;
    public ComboBox docType;
    public ComboBox contractorCmbox;
    public ComboBox workerCmbox;
    public TableView documentItemsTable;

    public TableColumn nameCol;
    public TableColumn eanCol;
    public TableColumn quantityCol;
    public TableColumn measurmntUnitCol;
    public TableColumn taxCol;
    public TableColumn priceGrossCol;
    public TableColumn priceNetCol;
    public TableColumn valueGrossCol;
    public TableColumn valueNetCol;

    public Label netDocVal;
    public Label grossDocVal;

    public TableView allItems;
    public TableColumn allItemsNameCol;
    public TableColumn allItemsEanCol;
    public TableColumn allItemsModelNumberCol;
    public TableColumn allItemsDescrCol;
    public TableColumn allItemsPriceCol;
    public TableColumn allItemsAvailableQuantityCol;

    private Document document;
    private Mode mode;
    private DocumentPropertiesModel model;
    private ActionResult actionResult;

    public DocumentPropertiesController(Document document, Mode mode) {
        System.out.println("DocumentPropertiesController");
        this.document = document;
        this.mode = mode;
        model = new DocumentPropertiesModel();
        actionResult = ActionResult.CANCEL;
    }

    @FXML
    public void initialize() {

        quantityCol.setCellValueFactory(new PropertyValueFactory<Double, Document>("quantity"));
        taxCol.setCellValueFactory(new PropertyValueFactory<Double, Document>("tax"));
        priceGrossCol.setCellValueFactory(new PropertyValueFactory<Double, Document>("price"));
        nameCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, String>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper(c.getValue().getItem().getName())
        );
        eanCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, String>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper(c.getValue().getItem().getEan()));
        measurmntUnitCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, String>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper(c.getValue().getItem().getMeasurementUnit()));
        priceNetCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, String>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper(netValue(c.getValue().getPrice(), c.getValue().getTax())));

        valueGrossCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, String>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper(c.getValue().getPrice() * c.getValue().getQuantity()));
        valueNetCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, String>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper(netValue(c.getValue().getPrice() * c.getValue().getQuantity(), c.getValue().getTax())));


        allItemsNameCol.setCellValueFactory(new PropertyValueFactory<String, Item>("name"));
        allItemsEanCol.setCellValueFactory(new PropertyValueFactory<String, Item>("ean"));
        allItemsPriceCol.setCellValueFactory(new PropertyValueFactory<String, Item>("currentPrice"));
        allItemsModelNumberCol.setCellValueFactory(new PropertyValueFactory<String, Item>("itemModelNumber"));
        allItems.getItems().addAll(model.getItemsList());

        allItemsDescrCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Item, String>, ObservableValue>) param -> {
            String description = Optional.ofNullable(param.getValue().getDescription()).orElse("<brak opisu>");
            return new ReadOnlyObjectWrapper(description.replaceAll("\n", "|"));
        });
        
        if (mode == Mode.EDIT_ITEM) {

            Date docDate = document.getDate();
            date.setValue((date != null) ? docDate.toLocalDate() : null);
            name.setText(document.getName());

            workerCmbox.getItems().addAll(model.getWorkersList());
            Worker worker = document.getWorker();
            workerCmbox.setValue((worker != null) ? worker.getFullName() : null);

            contractorCmbox.getItems().addAll(model.getContractorsList());
            Contractor contractor = document.getContractor();
            contractorCmbox.setValue(((contractor != null) ? contractor.getContractorName() : null));

            documentItemsTable.getItems().addAll(document.getItems());

            for (DocumentType t : DocumentType.values())
                docType.getItems().add(t.getType());

            double grossVal = document.getItems().stream().mapToDouble(di -> di.getQuantity() * di.getPrice()).sum();
            grossDocVal.setText(Double.toString(grossVal) + " zł");

            double netVal = document.getItems().stream().mapToDouble(di -> netValue(di.getQuantity() * di.getPrice(), di.getTax())).sum();
            netDocVal.setText(Double.toString(netVal) + " zł");
        }
    }

    @FXML
    public void cancelDocumentAddOrEdit() {
        actionResult = ActionResult.CANCEL;
        documentItemsTable.getScene().getWindow().hide();
    }

    private double netValue(double gross, double tax) {
        return gross * (100.0 - tax) / 100.0;
    }
}
