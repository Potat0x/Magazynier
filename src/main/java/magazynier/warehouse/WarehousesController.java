package magazynier.warehouse;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import magazynier.RowNotFoundException;
import magazynier.utils.AlertLauncher;
import magazynier.utils.MoneyValueFormat;
import magazynier.utils.TextFieldCorrectnessIndicator;
import magazynier.utils.validators.LengthValidator;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.Optional;

public class WarehousesController {

    private static final int MAX_NAME_LEN = 30;
    @FXML
    public TableView<Warehouse> warehousesTable;
    @FXML
    public TableColumn<Warehouse, String> nameCol;
    @FXML
    public TableColumn itemsCol;
    public TableColumn<Warehouse, Double> valueNetCol;
    public TableColumn<Warehouse, Double> valueGrossCol;
    private WarehousesModel model;

    public WarehousesController() {
        model = new WarehousesModel();
    }

    private void refreshTable() {
        ArrayList<Warehouse> wl = model.getWarehousesList();
        warehousesTable.getItems().clear();
        warehousesTable.getItems().addAll(wl);
    }

    @FXML
    public void initialize() {
        warehousesTable.setRowFactory(
                tableView -> {
                    TableRow<Warehouse> row = new TableRow<>();

                    ContextMenu cmenu = new ContextMenu();
                    MenuItem del = new MenuItem("Usuń");
                    MenuItem add = new MenuItem("Dodaj");
                    del.setOnAction(event -> {

                        if (row.getItem() != null) {
                            try {
                                model.deleteWarehouse(row.getItem());
                                warehousesTable.getItems().remove(row.getItem());
                            } catch (RowNotFoundException e) {
                                AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Nie można usunąć magazynu \"" + row.getItem().getName() + "\".\nMógł zostać wcześniej usunięty przez innego użytkownika.");
                                WarehousesController.this.refreshTable();
                            } catch (PersistenceException e) {
                                AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Nie można usunąć magazynu \"" + row.getItem().getName() + "\".\nJest do niego przypisany asortyment.");
                            } catch (Exception e) {
                                AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Nie można usunąć magazynu \"" + row.getItem().getName() + "\".\nNieznany błąd.");
                                WarehousesController.this.refreshTable();
                            }
                        }
                    });

                    add.setOnAction(event -> WarehousesController.this.addWarehouse());

                    cmenu.getItems().add(add);
                    cmenu.getItems().add(del);
                    //row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty())).then(cmenu).otherwise((ContextMenu) null));
                    row.setContextMenu(cmenu);
                    return row;
                });


        MoneyValueFormat moneyFormat = new MoneyValueFormat();
        valueGrossCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(Double.parseDouble(moneyFormat.
                format(model.getWarehouseGrossValue(c.getValue())))));

        valueNetCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(Double.parseDouble(moneyFormat.
                format(model.getWarehouseNetValue(c.getValue())))));

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());

        nameCol.setOnEditCommit(event -> {
            if (event.getNewValue().length() <= MAX_NAME_LEN) {
                Warehouse warehouse = event.getTableView().getItems().get(event.getTablePosition().getRow());
                warehouse.setName(event.getNewValue());
                try {
                    model.updateWarehouse(warehouse);
                } catch (RowNotFoundException e) {
                    AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Nie można usunąć magazynu \"" + warehouse.getName() + "\".\nMógł zostać usunięty przez innego użytkownika.");
                    refreshTable();
                    //e.printStackTrace();
                }
            } else {
                AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Wprowadzona nazwa magazynu jest za długa.\n(maksymalna długość: 30 znaków");
                warehousesTable.refresh();
            }
        });

        refreshTable();
    }

    @FXML
    public void addWarehouse() {
        TextInputDialog t = new TextInputDialog();
        t.setGraphic(null);
        t.setTitle("Dodawanie magazynu");
        t.setHeaderText("Podaj nazwę nowego magazynu");
        t.getEditor().textProperty().addListener(new TextFieldCorrectnessIndicator(new LengthValidator(MAX_NAME_LEN)));
        Optional<String> optName = t.showAndWait();

        optName.ifPresent(warehouseName -> {
            if (warehouseName.length() <= MAX_NAME_LEN) {
                Warehouse newWarehouse = new Warehouse();
                newWarehouse.setName(warehouseName);
                model.addWarehouse(newWarehouse);//todo: add error handling
                warehousesTable.getItems().add(newWarehouse);
            } else {
                AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Wprowadzona nazwa magazynu jest za długa.\n(maksymalna długość: 30 znaków");
            }
        });
    }
}
