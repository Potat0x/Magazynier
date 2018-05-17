package magazynier;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import magazynier.contractor.Contractor;
import magazynier.item.Item;
import magazynier.utils.AlertLauncher;
import magazynier.utils.PropertyTableFilter;
import magazynier.utils.TextFieldCorrectnessIndicator;
import magazynier.utils.validators.LengthValidator;
import magazynier.worker.Worker;
import org.hibernate.PropertyValueException;

import javax.persistence.OptimisticLockException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

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
    public TableColumn<DocumentItem, String> quantityCol;
    public TableColumn measurmntUnitCol;
    public TableColumn taxCol;
    public TableColumn priceGrossCol;
    public TableColumn priceNetCol;
    public TableColumn valueGrossCol;
    public TableColumn valueNetCol;
    public TableColumn<DocumentItem, String> margin;
    public TableColumn marginType;

    public Label netDocVal;
    public Label grossDocVal;

    public TableView allItemsTable;
    public TableColumn allItemsNameCol;
    public TableColumn allItemsEanCol;
    public TableColumn allItemsModelNumberCol;
    public TableColumn allItemsDescrCol;
    public TableColumn allItemsPriceCol;
    public TableColumn allItemsAvailableQuantityCol;

    public TextField nameFilterField;
    public TextField eanFilterField;
    public TextField modelFilterField;
    public TextField descriptionFilterField;

    final int MAX_DOC_NAME_LEN = 30;

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

        PropertyTableFilter<Item> allItemsFilter = new PropertyTableFilter<Item>(model.getItemsList(), allItemsTable);
        allItemsFilter.tie(nameFilterField, Item::getName);
        allItemsFilter.tie(eanFilterField, Item::getEan);
        allItemsFilter.tie(modelFilterField, Item::getItemModelNumber);
        allItemsFilter.tie(descriptionFilterField, Item::getDescription);

        measurmntUnitCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, String>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper(c.getValue().getQuantity()));
        taxCol.setCellValueFactory(new PropertyValueFactory<Double, DocumentItem>("tax"));
        priceGrossCol.setCellValueFactory(new PropertyValueFactory<Double, DocumentItem>("price"));

        measurmntUnitCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, String>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper(c.getValue().getItem().getMeasurementUnit().getUnitName()));

        nameCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, String>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper(c.getValue().getItem().getName()));

        eanCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, String>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper(c.getValue().getItem().getEan()));


//        measurmntUnitCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, String>, ObservableValue<String>>) c ->
//                new ReadOnlyObjectWrapper(c.getValue().getItem().getMeasurementUnit()));

        priceNetCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, Double>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper(netValue(c.getValue().getPrice(), c.getValue().getTax())));

        valueGrossCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, Double>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper(multiplyNullable(c.getValue().getPrice(), c.getValue().getQuantity())));

        valueNetCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, Double>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper(netValue(multiplyNullable(c.getValue().getPrice(), c.getValue().getQuantity()), c.getValue().getTax())));

        allItemsNameCol.setCellValueFactory(new PropertyValueFactory<String, Item>("name"));
        allItemsEanCol.setCellValueFactory(new PropertyValueFactory<String, Item>("ean"));
        allItemsPriceCol.setCellValueFactory(new PropertyValueFactory<Double, Item>("currentPrice"));
        allItemsModelNumberCol.setCellValueFactory(new PropertyValueFactory<String, Item>("itemModelNumber"));

        allItemsDescrCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Item, String>, ObservableValue>) param ->
        {
            String description = Optional.ofNullable(param.getValue().getDescription()).orElse("<brak opisu>");
            return new ReadOnlyObjectWrapper(description.replaceAll("\n", " "));
        });

//        for (DocumentType t : model.getDocTypesList())
//            docType.getItems().add(t.getType());

        docType.getItems().addAll(model.getDocTypesList());

        date.setValue(LocalDate.now());
        workerCmbox.getItems().addAll(model.getWorkersList());
        contractorCmbox.getItems().addAll(model.getContractorsList());

        name.textProperty().addListener(new TextFieldCorrectnessIndicator(new LengthValidator(MAX_DOC_NAME_LEN)));


        if (mode == Mode.EDIT_ITEM) {
            updateFormFromDocument();

            documentItemsTable.getItems().addAll(document.getItems());

            double grossVal = document.getItems().stream().mapToDouble(di -> multiplyNullable(di.getQuantity(), di.getPrice())).sum();
            grossDocVal.setText(Double.toString(grossVal) + " zł");

            double netVal = document.getItems().stream().mapToDouble(di -> netValue(multiplyNullable(di.getQuantity(), di.getPrice()), di.getTax())).sum();
            netDocVal.setText(Double.toString(netVal) + " zł");
        }


        documentItemsTable.setRowFactory(new Callback<TableView<DocumentItem>, TableRow<DocumentItem>>() {
            @Override
            public TableRow<DocumentItem> call(TableView<DocumentItem> tableView) {
                TableRow<DocumentItem> row = new TableRow<>();

                ContextMenu cmenu = new ContextMenu();
                MenuItem del = new MenuItem("Usuń");
                del.setOnAction(event -> {

                    if (row.getItem() != null) {
                        documentItemsTable.getItems().remove(row.getItem());
                    }
                });

                cmenu.getItems().add(del);
                row.setContextMenu(cmenu);
                return row;
            }
        });

        marginType.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, String>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper(String.valueOf(c.getValue().getMarginType())));
        marginType.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList(model.getMarginTypesList())));
        marginType.setOnEditCommit(
                new EventHandler<CellEditEvent<DocumentItem, MarginType>>() {
                    @Override
                    public void handle(CellEditEvent<DocumentItem, MarginType> t) {
                        DocumentItem di = ((DocumentItem) t.getTableView().getItems().get(t.getTablePosition().getRow()));
                        di.setMarginType(t.getNewValue());
                    }
                }
        );

        quantityCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, String>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper(String.valueOf(c.getValue().getQuantity())));
        quantityCol.setCellFactory(TextFieldTableCell.forTableColumn());//todo!!!
        quantityCol.setOnEditCommit(new EventHandler<CellEditEvent<DocumentItem, String>>() {
            @Override
            public void handle(CellEditEvent<DocumentItem, String> event) {
                DocumentItem di = event.getTableView().getItems().get(event.getTablePosition().getRow());
                di.setQuantity(Double.parseDouble(event.getNewValue()));
                documentItemsTable.refresh();
            }
        });

        margin.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, String>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper(String.valueOf(Optional.ofNullable(c.getValue().getMargin()).orElse(0.0))));
        margin.setCellFactory(TextFieldTableCell.forTableColumn());//todo!!!
        margin.setOnEditCommit(new EventHandler<CellEditEvent<DocumentItem, String>>() {
            @Override
            public void handle(CellEditEvent<DocumentItem, String> event) {
                DocumentItem di = event.getTableView().getItems().get(event.getTablePosition().getRow());
                di.setMargin(Double.valueOf(event.getNewValue()));
                documentItemsTable.refresh();
            }
        });
        //todo: dodać zmiane wartosci sprzedaży (ceny netto)/lub dodatkowej kolumny na własną cene, żeby był podgląd na starą

        documentItemsTable.setStyle("-fx-text-alignment: CENTER-LEFT; -fx-background-color: #afff6d;");

        ///////
        allItemsTable.setRowFactory(new Callback<TableView<Item>, TableRow<Item>>() {
            @Override
            public TableRow<Item> call(TableView<Item> tableView) {
                TableRow<Item> row = new TableRow<>();
                ContextMenu cmenu = new ContextMenu();
                MenuItem add = new MenuItem("Dodaj");

                add.setOnAction(event -> {
                    DocumentItem newDocItem = new DocumentItem(row.getItem(), document);
                    newDocItem.setMarginType((MarginType) model.getMarginTypesList().iterator().next());
                    documentItemsTable.getItems().add(newDocItem);
                });
                cmenu.getItems().add(add);
                row.setContextMenu(cmenu);
                return row;
            }
        });

        refreshTable();
    }

    private void refreshTable() {
        documentItemsTable.getItems().clear();
        documentItemsTable.getItems().addAll(document.getItems());
    }

    @FXML
    public void clearAllFilters() {
        nameFilterField.clear();
        eanFilterField.clear();
        modelFilterField.clear();
        descriptionFilterField.clear();
    }

    @FXML
    public void save() {

        ArrayList ar = new ArrayList<>(Arrays.asList(documentItemsTable.getItems().toArray()));
        Set<DocumentItem> newItemsSet = new HashSet<DocumentItem>(ar);//todo: clean it with Java 9
        //document.setItems(newItemsSet); /*cause bug!*/
        document.getItems().clear();
        document.getItems().addAll(newItemsSet);

        if (isFormValid()) {
            updateDocumentFromForm();

            try {
                if (mode == Mode.EDIT_ITEM) {
                    model.updateDocument(document);
                    actionResult = ActionResult.CONFIRM;
                } else if (mode == Mode.ADD_ITEM) {
                    model.addDocument(document);
                    actionResult = ActionResult.CONFIRM;
                    AlertLauncher.showAndWait(Alert.AlertType.INFORMATION, "Nowy dokument", null, "Dokument został dodany.");
                    mode = Mode.EDIT_ITEM;
                }

            } catch (RowNotFoundException e) {
                AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie można zaktualizować dokumentu.", "Nie znalaziono dokumentu. Mógł zostać usunięty z bazy.");
                closeWindowWithFail();
            } catch (OptimisticLockException e) {
                AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie można zaktualizować dokumentu.", "Dokument został w międzyczasie zaktualizowany przez innego użytkownika.");
                closeWindowWithFail();
            } catch (PropertyValueException e) {
                System.out.println("PVE");//todo
            } catch (Exception e) {
                System.out.println();
                e.printStackTrace();
                AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie można zaktualizować dokumentu.", "Nieznany błąd.");
                closeWindowWithFail();
            }
        } else {
            AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Wypełnij prawidłowo wszystkie pola formularza.\nJeżeli numer dokumentu będzie pusty, zostanie wygenerowany automatycznie.");
        }//todo: add doc name generating
    }

    private boolean isFormValid() {
        return docType.getValue() != null && date.getValue() != null && workerCmbox.getSelectionModel().getSelectedItem() != null && contractorCmbox.getSelectionModel().getSelectedItem() != null && (name.getText() == null || name.getText().length() <= MAX_DOC_NAME_LEN);
    }

    private void updateDocumentFromForm() {
        document.setName(name.getText());
        document.setDate(Date.valueOf(date.getValue()));
        document.setWorker((Worker) workerCmbox.getSelectionModel().getSelectedItem());
        document.setContractor((Contractor) contractorCmbox.getSelectionModel().getSelectedItem());
        document.setDocumentType((DocumentType) docType.getSelectionModel().getSelectedItem());
    }

    private void updateFormFromDocument() {
        Date docDate = document.getDate();
        date.setValue((date != null) ? docDate.toLocalDate() : null);

        name.setText(document.getName());
        docType.getSelectionModel().select(document.getDocumentType());

        Worker worker = document.getWorker();
        workerCmbox.getSelectionModel().select(worker);

        Contractor contractor = document.getContractor();
        contractorCmbox.getSelectionModel().select(contractor);
    }

    @FXML
    public void closeWindowWithFail() {
        actionResult = ActionResult.FAIL;
        documentItemsTable.getScene().getWindow().hide();
    }

    @FXML
    public void cancelDocumentAddOrEdit() {
        actionResult = ActionResult.CANCEL;
        documentItemsTable.getScene().getWindow().hide();
    }

    public ActionResult getActionResult() {
        return actionResult;
    }

    private Double multiplyNullable(Double a, Double b) {
        if (a != null && b != null) {
            return a * b;
        }
        return 0.0;
    }

    private double netValue(Double gross, Double tax) {
        if (gross != null && tax != null) {
            return gross * (100.0 - tax) / 100.0;
        }
        return 0;
    }
}
