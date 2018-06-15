package magazynier;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
import magazynier.warehouse.Warehouse;
import magazynier.worker.Worker;
import org.hibernate.PropertyValueException;

import javax.persistence.OptimisticLockException;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import static javafx.scene.control.cell.TextFieldTableCell.forTableColumn;
import static magazynier.ActionMode.*;
import static magazynier.ActionResult.CONFIRM;
import static magazynier.utils.WindowCreator.createWindowFromFxml;


@SuppressWarnings("WeakerAccess")
public class DocumentPropertiesController {

    public Label docPropertiesHeader;
    public Button saveButton;
    public DatePicker date;
    public TextField name;
    public ComboBox<DocumentType> docType;
    public ComboBox<Contractor> contractorCmbox;
    public ComboBox<Worker> workerCmbox;
    public GridPane propertiesForm;
    public SplitPane splitPane;
    public TableView<DocumentItem> documentItemsTable;

    public TableColumn<DocumentItem, String> nameCol;
    public TableColumn<DocumentItem, String> eanCol;
    public TableColumn quantityCol;
    public TableColumn<DocumentItem, Double> quantityAvailableCol;
    public TableColumn<DocumentItem, String> measurmntUnitCol;
    public TableColumn<DocumentItem, Double> taxCol;
    public TableColumn priceGrossCol;
    public TableColumn<DocumentItem, Double> priceNetCol;
    public TableColumn<DocumentItem, Double> valueGrossCol;
    public TableColumn<DocumentItem, Double> valueNetCol;
    public TableColumn margin;
    public TableColumn<DocumentItem, MarginType> marginType;
    public TableColumn<DocumentItem, Warehouse> warehouseCol;

    public Label netDocVal;
    public Label grossDocVal;

    public VBox allItemsVbox;
    public TableView<Item> allItemsTable;
    public TableColumn<Item, String> allItemsNameCol;
    public TableColumn<Item, String> allItemsEanCol;
    public TableColumn<Item, String> allItemsModelNumberCol;
    public TableColumn<Item, String> allItemsDescrCol;
    public TableColumn<Item, Double> allItemsPriceCol;
    public TableColumn<Item, Double> allItemsAvailableQuantityCol;

    public TextField nameFilterField;
    public TextField eanFilterField;
    public TextField modelFilterField;
    public TextField descriptionFilterField;

    private final int MAX_DOC_NAME_LEN = 32;

    private Document document;
    private ActionMode mode;
    private DocumentPropertiesModel model;
    private ActionResult actionResult;
    private PropertyTableFilter<Item> allItemsFilter;
    private MoneyValueFormat moneyFormat;

    private Set<DocumentItem> deletedItems;

    public DocumentPropertiesController(Document document, ActionMode mode) {
        System.out.println("DocumentPropertiesController");
        this.document = document;
        this.mode = mode;
        model = new DocumentPropertiesModel();
        actionResult = ActionResult.CANCEL;
        moneyFormat = new MoneyValueFormat();
        deletedItems = new HashSet<>();
    }

    @FXML
    public void initialize() {

        setHeaderText();

        allItemsFilter = new PropertyTableFilter<>(model.getItemsList(), allItemsTable);
        allItemsFilter.tie(nameFilterField, Item::getName);
        allItemsFilter.tie(eanFilterField, Item::getEan);
        allItemsFilter.tie(modelFilterField, Item::getItemModelNumber);
        allItemsFilter.tie(descriptionFilterField, Item::getDescription);

        taxCol.setCellValueFactory(new PropertyValueFactory<>("tax"));

        measurmntUnitCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getItem().getMeasurementUnit().getUnitName()));
        nameCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getItem().getName()));
        eanCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getItem().getEan()));

        priceNetCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(Double.parseDouble(moneyFormat.format(NullableCalc.netValue(c.getValue().getPrice(), c.getValue().getTax())))));
        valueGrossCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(Double.parseDouble(moneyFormat.format(NullableCalc.multiplyNullable(c.getValue().getPrice(), c.getValue().getQuantity())))));
        valueNetCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(Double.parseDouble(moneyFormat.format(NullableCalc.netValue(NullableCalc.multiplyNullable(c.getValue().getPrice(), c.getValue().getQuantity()), c.getValue().getTax())))));

        allItemsNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        allItemsEanCol.setCellValueFactory(new PropertyValueFactory<>("ean"));
        allItemsPriceCol.setCellValueFactory(new PropertyValueFactory<>("currentPrice"));
        allItemsModelNumberCol.setCellValueFactory(new PropertyValueFactory<>("itemModelNumber"));

        allItemsAvailableQuantityCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(Double.parseDouble(moneyFormat.
                format(model.getAvailableQuantity(c.getValue())))));

        allItemsDescrCol.setCellValueFactory(param ->
        {
            String description = Optional.ofNullable(param.getValue().getDescription()).orElse("<brak opisu>");
            return new ReadOnlyObjectWrapper<>(description.replaceAll("\n", " "));
        });

        docType.getItems().addAll(model.getDocTypesList());
        docType.setDisable(mode != ActionMode.ADD);

        if (mode == ActionMode.PREVIEW) {
            date.setDisable(true);
            date.setStyle("-fx-opacity: 1");
            date.getEditor().setStyle("-fx-opacity: 1");
        }

        date.valueProperty().addListener((observable, oldValue, newValue) -> setGeneratedDocumentName());

        date.setValue(LocalDate.now());
        workerCmbox.getItems().addAll(model.getWorkersList());
        contractorCmbox.getItems().addAll(model.getContractorsList());

        name.textProperty().addListener(new TextFieldCorrectnessIndicator(new LengthValidator(MAX_DOC_NAME_LEN)));
        name.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
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
            documentItemsTable.setRowFactory(tableView -> {
                TableRow<DocumentItem> row = new TableRow<>();

                ContextMenu cmenu = new ContextMenu();
                MenuItem del = new MenuItem("Usuń");
                del.setOnAction(event -> {

                    if (row.getItem() != null) {
                        deletedItems.add(row.getItem());
                        documentItemsTable.getItems().remove(row.getItem());
                    }
                });

                cmenu.getItems().add(del);
                row.setContextMenu(cmenu);
                return row;
            });
        }

        priceGrossCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, String>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper<>(moneyFormat.format(c.getValue().getPrice())));
        priceGrossCol.setCellFactory(TextFieldTableCell.forTableColumn());
        priceGrossCol.setOnEditCommit((EventHandler<CellEditEvent<DocumentItem, String>>) t -> {
                    DocumentItem di = t.getTableView().getItems().get(t.getTablePosition().getRow());
                    di.setPrice(Double.parseDouble(t.getNewValue()));
                    refreshDocValLabels();
                    documentItemsTable.refresh();
                }
        );

        marginType.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getMarginType()));
        marginType.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList(model.getMarginTypesList())));
        marginType.setOnEditCommit(t -> {
                    DocumentItem di = t.getTableView().getItems().get(t.getTablePosition().getRow());
                    di.setMarginType(t.getNewValue());
                    refreshDocValLabels();
                }
        );

        warehouseCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getWarehouse()));
        warehouseCol.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList(model.getWarehousesList())));
        warehouseCol.setOnEditCommit(t -> {
                    DocumentItem di = t.getTableView().getItems().get(t.getTablePosition().getRow());
                    di.setWarehouse(t.getNewValue());
                    //refreshDocValLabels();
                    documentItemsTable.refresh();
                }
        );

        quantityAvailableCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(Double.parseDouble(moneyFormat.
                format(model.getAvailableQuantityInWarehouse(c.getValue(), c.getValue().getWarehouse())))));

        quantityCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, String>, ObservableValue<String>>) c -> new ReadOnlyObjectWrapper<>(String.valueOf(c.getValue().getQuantity())));
        quantityCol.setCellFactory(forTableColumn());
        quantityCol.setOnEditCommit((EventHandler<CellEditEvent<DocumentItem, String>>) event -> {
            DocumentItem di = event.getTableView().getItems().get(event.getTablePosition().getRow());
            di.setQuantity(Double.parseDouble(event.getNewValue()));
            refreshDocValLabels();
            documentItemsTable.refresh();
        });

        margin.setCellValueFactory((Callback<TableColumn.CellDataFeatures<DocumentItem, String>, ObservableValue<String>>) c ->
                new ReadOnlyObjectWrapper<>(String.valueOf(Optional.ofNullable(c.getValue().getMargin()).orElse(0.0))));
        margin.setCellFactory(forTableColumn());
        margin.setOnEditCommit((EventHandler<CellEditEvent<DocumentItem, String>>) event -> {
            DocumentItem di = event.getTableView().getItems().get(event.getTablePosition().getRow());
            di.setMargin(Double.valueOf(event.getNewValue()));
            DocumentPropertiesController.this.refreshDocValLabels();
            documentItemsTable.refresh();
        });

        allItemsTable.setRowFactory(tableView -> {
            TableRow<Item> row = new TableRow<>();
            ContextMenu cmenu = new ContextMenu();
            MenuItem add = new MenuItem("Dodaj");

            add.setOnAction(event -> {
                DocumentItem newDocItem = new DocumentItem(row.getItem(), document);
                newDocItem.setMarginType(model.getMarginTypesList().iterator().next());
                documentItemsTable.getItems().add(newDocItem);
            });
            cmenu.getItems().add(add);
            row.setContextMenu(cmenu);
            return row;
        });

        refreshTable();
        if (mode == PREVIEW) {
            disableForm(propertiesForm);
            saveButton.setVisible(false);
            documentItemsTable.setEditable(false);
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
            docPropertiesHeader.setText("Szczegóły dokumentu");
        }
    }

    private void setGeneratedDocumentName() {
        name.setText(model.getNextDocName(date.getValue()));
    }

    private void refreshTable() {
        documentItemsTable.getItems().clear();
        document.getItems().forEach(di -> {
            model.getWarehousesList().stream().filter(w -> w.getId().equals(model.findWarehouse(di))).findAny().ifPresent(di::setWarehouse);
        });
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

        ArrayList<DocumentItem> ar = new ArrayList(Arrays.asList(documentItemsTable.getItems().toArray()));
        Set<DocumentItem> newItemsSet = new HashSet<>(ar);//todo_ clean it with Java 9
        //document.setItems(newItemsSet); /*cause bug!*/
        document.getItems().clear();
        document.getItems().addAll(newItemsSet);

        if (isFormValid()) {
            updateDocumentFromForm();

            if (document.getItems().stream().allMatch(i -> i.getWarehouse() != null)) {
                try {
                    if (mode == ActionMode.EDIT || mode == ActionMode.PREVIEW) {

                        model.updateDocument(document, deletedItems);
                        deletedItems.clear();
                        actionResult = CONFIRM;
                        documentItemsTable.refresh();
                        allItemsTable.refresh();
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
                int tag = docType.getSelectionModel().getSelectedItem().getTag();

                if (tag > 0) {
                    AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Wybierz magazyny, do których zostanie przyjęty asortyment.");
                } else if (tag < 0) {
                    AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Wybierz magazyny, z których zostanie wydany asortyment.");
                }
            }

        } else {
            AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Wypełnij prawidłowo wszystkie pola formularza.\nJeżeli numer dokumentu będzie pusty, zostanie wygenerowany automatycznie.");
        }
    }

    @FXML
    public void showItem(MouseEvent event) {
        if (event.getClickCount() == 2) {

            Item item;
            if (documentItemsTable.isFocused()) {
                item = documentItemsTable.getSelectionModel().getSelectedItem().getItem();
            } else {
                item = allItemsTable.getSelectionModel().getSelectedItem();
            }

            if (item != null) {
                ItemController ic = new ItemController(item, ActionMode.EDIT);

                try {
                    Stage itemStage = createWindowFromFxml("/fxml/item_window.fxml", ic, "Towar");
                    itemStage.showAndWait();
                } catch (IOException e) {
                    e.printStackTrace();
                }

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
        document.setWorker(workerCmbox.getSelectionModel().getSelectedItem());
        document.setContractor(contractorCmbox.getSelectionModel().getSelectedItem());
        document.setDocumentType(docType.getSelectionModel().getSelectedItem());
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
