package magazynier;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import magazynier.contractor.Contractor;
import magazynier.item.Item;
import magazynier.item.ItemController;
import magazynier.utils.*;
import magazynier.utils.validators.LengthValidator;
import magazynier.worker.Worker;
import org.hibernate.PropertyValueException;

import javax.persistence.OptimisticLockException;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static magazynier.ActionMode.*;
import static magazynier.ActionResult.CONFIRM;

@SuppressWarnings("unchecked")
public class DocumentPropertiesController {

    public Label docPropertiesHeader;
    public Button saveButton;
    public DatePicker date;
    public TextField name;
    public ComboBox docType;
    public ComboBox contractorCmbox;
    public ComboBox workerCmbox;
    public GridPane propertiesForm;
    public SplitPane splitPane;
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

    public VBox allItemsVbox;
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
    private ActionMode mode;
    private DocumentPropertiesModel model;
    private ActionResult actionResult;
    private PropertyTableFilter<Item> allItemsFilter;
    private MoneyValueFormat moneyFormat;

    public DocumentPropertiesController(Document document, ActionMode mode) {
        System.out.println("DocumentPropertiesController");
        this.document = document;
        this.mode = mode;
        model = new DocumentPropertiesModel();
        actionResult = ActionResult.CANCEL;
        moneyFormat = new MoneyValueFormat();
    }

    @FXML
    public void initialize() {

        setHeaderText();

        allItemsFilter = new PropertyTableFilter<Item>(model.getItemsList(), allItemsTable);
        allItemsFilter.tie(nameFilterField, Item::getName);
        allItemsFilter.tie(eanFilterField, Item::getEan);
        allItemsFilter.tie(modelFilterField, Item::getItemModelNumber);
        allItemsFilter.tie(descriptionFilterField, Item::getDescription);

        taxCol.setCellValueFactory(new PropertyValueFactory<Double, DocumentItem>("tax"));
        priceGrossCol.setCellValueFactory(new PropertyValueFactory<Double, DocumentItem>("price"));

        measurmntUnitCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, String>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper(c.getValue().getQuantity()));

        measurmntUnitCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, String>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper(c.getValue().getItem().getMeasurementUnit().getUnitName()));

        nameCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, String>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper(c.getValue().getItem().getName()));

        eanCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, String>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper(c.getValue().getItem().getEan()));

        priceNetCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, Double>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper(moneyFormat.format(NullableCalc.netValue(c.getValue().getPrice(), c.getValue().getTax()))));

        valueGrossCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, Double>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper(moneyFormat.format(NullableCalc.multiplyNullable(c.getValue().getPrice(), c.getValue().getQuantity()))));

        valueNetCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, Double>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper(moneyFormat.format(NullableCalc.netValue(NullableCalc.multiplyNullable(c.getValue().getPrice(), c.getValue().getQuantity()), c.getValue().getTax()))));

        allItemsNameCol.setCellValueFactory(new PropertyValueFactory<String, Item>("name"));
        allItemsEanCol.setCellValueFactory(new PropertyValueFactory<String, Item>("ean"));
        allItemsPriceCol.setCellValueFactory(new PropertyValueFactory<Double, Item>("currentPrice"));
        allItemsModelNumberCol.setCellValueFactory(new PropertyValueFactory<String, Item>("itemModelNumber"));

        allItemsDescrCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Item, String>, ObservableValue>) param ->
        {
            String description = Optional.ofNullable(param.getValue().getDescription()).orElse("<brak opisu>");
            return new ReadOnlyObjectWrapper(description.replaceAll("\n", " "));
        });

        docType.getItems().addAll(model.getDocTypesList());

        date.valueProperty().addListener((observable, oldValue, newValue) -> {
            setGeneratedDocumentName();
        });

        date.setValue(LocalDate.now());
        workerCmbox.getItems().addAll(model.getWorkersList());
        contractorCmbox.getItems().addAll(model.getContractorsList());

        name.textProperty().addListener(new TextFieldCorrectnessIndicator(new LengthValidator(MAX_DOC_NAME_LEN)));
        name.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                setGeneratedDocumentName();
                workerCmbox.requestFocus();
            }
        });

        if (mode == EDIT || mode == PREVIEW) {
            updateFormFromDocument();
            documentItemsTable.getItems().addAll(document.getItems());
            refreshDocValLabels();
        }
        
        if (mode == EDIT || mode == ADD) {
            documentItemsTable.setRowFactory((Callback<TableView<DocumentItem>, TableRow<DocumentItem>>) tableView -> {
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
            });
        }

        priceGrossCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, String>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper(moneyFormat.format(c.getValue().getPrice())));
        priceGrossCol.setCellFactory(TextFieldTableCell.forTableColumn());
        priceGrossCol.setOnEditCommit(
                new EventHandler<CellEditEvent<DocumentItem, String>>() {
                    @Override
                    public void handle(CellEditEvent<DocumentItem, String> t) {
                        DocumentItem di = t.getTableView().getItems().get(t.getTablePosition().getRow());
                        di.setPrice(Double.parseDouble(t.getNewValue()));
                        refreshDocValLabels();
                        documentItemsTable.refresh();
                    }
                }
        );

        marginType.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, String>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper(String.valueOf(c.getValue().getMarginType())));
        marginType.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList(model.getMarginTypesList())));
        marginType.setOnEditCommit(
                new EventHandler<CellEditEvent<DocumentItem, MarginType>>() {
                    @Override
                    public void handle(CellEditEvent<DocumentItem, MarginType> t) {
                        DocumentItem di = t.getTableView().getItems().get(t.getTablePosition().getRow());
                        di.setMarginType(t.getNewValue());
                        refreshDocValLabels();
                    }
                }
        );

        quantityCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, String>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper(String.valueOf(c.getValue().getQuantity())));
        quantityCol.setCellFactory(TextFieldTableCell.forTableColumn());
        quantityCol.setOnEditCommit(new EventHandler<CellEditEvent<DocumentItem, String>>() {
            @Override
            public void handle(CellEditEvent<DocumentItem, String> event) {
                DocumentItem di = event.getTableView().getItems().get(event.getTablePosition().getRow());
                di.setQuantity(Double.parseDouble(event.getNewValue()));
                refreshDocValLabels();
                documentItemsTable.refresh();
            }
        });

        margin.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, String>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper(String.valueOf(Optional.ofNullable(c.getValue().getMargin()).orElse(0.0))));
        margin.setCellFactory(TextFieldTableCell.forTableColumn());
        margin.setOnEditCommit(new EventHandler<CellEditEvent<DocumentItem, String>>() {
            @Override
            public void handle(CellEditEvent<DocumentItem, String> event) {
                DocumentItem di = event.getTableView().getItems().get(event.getTablePosition().getRow());
                di.setMargin(Double.valueOf(event.getNewValue()));
                refreshDocValLabels();
                documentItemsTable.refresh();
            }
        });
        //documentItemsTable.setStyle("-fx-text-alignment: CENTER-LEFT; -fx-background-color: #afff6d;");
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
        if (mode == PREVIEW) {
            disableForm(propertiesForm);
            saveButton.setVisible(false);
            documentItemsTable.setEditable(false);//todo: remove context menu
        }
    }

    private void refreshDocValLabels() {
        double grossVal = document.getItems().stream().mapToDouble(di -> NullableCalc.multiplyNullable(di.getQuantity(), di.getPrice())).sum();
        grossDocVal.setText(moneyFormat.format(grossVal) + " zł");

        double netVal = document.getItems().stream().mapToDouble(di -> NullableCalc.netValue(NullableCalc.multiplyNullable(di.getQuantity(), di.getPrice()), di.getTax())).sum();
        netDocVal.setText(moneyFormat.format(netVal) + " zł");
    }

    private void disableForm(Pane formPane) {

        splitPane.getItems().remove(allItemsVbox);
        for (Node n : formPane.getChildren()) {
            if (n instanceof TextInputControl) {
                ((TextInputControl) n).setEditable(false);
            } else if (n instanceof ComboBox) {
                ComboBox cmbox = (ComboBox) n;
                Object cmval = cmbox.getSelectionModel().getSelectedItem();
                cmbox.setEditable(true);
                cmbox.getEditor().setEditable(false);
                cmbox.getItems().clear();
                cmbox.getItems().add(cmval);
                cmbox.setValue(cmval);
            } else if (n instanceof DatePicker) {
                ((DatePicker) n).setEditable(false);
            }
        }
    }

    private void setHeaderText() {
        if (mode == ADD) {
            docPropertiesHeader.setText("Nowy dokument");
        } else if (mode == EDIT) {
            docPropertiesHeader.setText("Edycja dokumentu");
        } else {
            docPropertiesHeader.setText("Szczegóły dokumentu dokumentu");
        }
    }

    private void setGeneratedDocumentName() {
        name.setText(1 + model.countDocumentsByDay(date.getValue()) + LocalDate.now().format(DateTimeFormatter.ofPattern("/dd-MM-yyyy")));
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
                if (mode == ActionMode.EDIT || mode == ActionMode.PREVIEW) {
                    model.updateDocument(document);
                    actionResult = CONFIRM;
                } else if (mode == ADD) {
                    model.addDocument(document);
                    mode = ActionMode.EDIT;
                    actionResult = CONFIRM;
                    setHeaderText();
                    AlertLauncher.showAndWait(Alert.AlertType.INFORMATION, "Nowy dokument", null, "Dokument został dodany.");
                }

            } catch (RowNotFoundException e) {
                AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie można zaktualizować dokumentu.", "Nie znalaziono dokumentu. Mógł zostać usunięty z bazy.");
                closeWindowWithFail();
            } catch (OptimisticLockException e) {
                AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie można zaktualizować dokumentu.", "Dokument został w międzyczasie zaktualizowany przez innego użytkownika.");
                closeWindowWithFail();
            } catch (PropertyValueException e) {
                System.out.println("PVE");
            } catch (Exception e) {
                System.out.println();
                e.printStackTrace();
                AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie można zaktualizować dokumentu.", "Nieznany błąd.");
                closeWindowWithFail();
            }
        } else {
            AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Wypełnij prawidłowo wszystkie pola formularza.\nJeżeli numer dokumentu będzie pusty, zostanie wygenerowany automatycznie.");
        }
    }

    @FXML
    public void showItem(MouseEvent event) {

        if (event.getClickCount() == 2) {
            FXMLLoader itemStageLoader = new FXMLLoader(getClass().getResource("/fxml/item_window.fxml"));

            Item item;
            if (documentItemsTable.isFocused()) {
                item = ((DocumentItem) documentItemsTable.getSelectionModel().getSelectedItem()).getItem();
            } else {
                item = (Item) allItemsTable.getSelectionModel().getSelectedItem();
            }

            if (item != null) {
                ItemController ic = new ItemController(item, ActionMode.EDIT);
                itemStageLoader.setController(ic);
                Stage itemStage = new Stage();
                itemStage.setTitle("Towar");
                Parent parent = null;
                try {
                    parent = itemStageLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                itemStage.setScene(new Scene(parent));
                itemStage.showAndWait();

                if (ic.getActionResult() == CONFIRM) {
                    try {
                        model.refreshDocument(document);
                    } catch (RowNotFoundException e) {
                        AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie znaleziono dokumentu.", "Nie znalaziono dokumentu. Mógł zostać usunięty z bazy.");
                        closeWindowWithFail();
                    } catch (Exception e) {
                        e.printStackTrace();
                        AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie można znaleźć dokumentu.", "Nieznany błąd.");
                        closeWindowWithFail();
                    }
                    allItemsFilter.setItems(model.getItemsList());
                    allItemsTable.refresh();
                    documentItemsTable.refresh();
                }
            }
        }
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

        if (date != null) {
            date.setValue(docDate.toLocalDate());
        }

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

}
