package magazynier;

import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javassist.NotFoundException;
import magazynier.entities.Warehouse;
import magazynier.utils.AlertLauncher;

import java.util.ArrayList;

public class WarehousesController {

    private static final int MAX_NAME_LEN = 30;
    @FXML
    public TableView warehousesTable;
    @FXML
    public TableColumn nameCol;
    @FXML
    public TableColumn itemsCol;
    private WarehousesModel model;

    public WarehousesController() {
        model = new WarehousesModel();
    }

    void refreshTable() {
        ArrayList wl = model.getWarehousesList();
        warehousesTable.getItems().clear();
        warehousesTable.getItems().addAll(wl);
    }

    @FXML
    public void initialize() {

        warehousesTable.setRowFactory(
                new Callback<TableView<Warehouse>, TableRow<Warehouse>>() {
                    @Override
                    public TableRow<Warehouse> call(TableView<Warehouse> tableView) {
                        final TableRow<Warehouse> row = new TableRow<>();
                        final ContextMenu cm = new ContextMenu();

                        MenuItem del = new MenuItem("Delete");
                        del.setOnAction(event -> {
                            try {
                                model.deleteWarehouse(row.getItem());
                                warehousesTable.getItems().remove(row.getItem());
                            } catch (NotFoundException e) {
                                AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Nie można usunąć magazynu \"" + row.getItem().getName() + "\".\nMógł zostać wcześniej usunięty przez innego użytkownika.");
                                refreshTable();
                                //e.printStackTrace();
                            }
                        });
                        cm.getItems().add(del);

                        row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty())).then(cm).otherwise((ContextMenu) null));
                        return row;
                    }
                });

        nameCol.setCellValueFactory(new PropertyValueFactory<Warehouse, String>("name"));
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());

        nameCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
            @Override
            public void handle(TableColumn.CellEditEvent event) {

                if (((String) event.getNewValue()).length() <= MAX_NAME_LEN) {
                    Warehouse warehouse = (Warehouse) event.getTableView().getItems().get(event.getTablePosition().getRow());
                    warehouse.setName((String) event.getNewValue());
                    try {
                        model.updateWarehouse(warehouse);
                    } catch (NotFoundException e) {
                        AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Nie można usunąć magazynu \"" + warehouse.getName() + "\".\nMógł zostać usunięty przez innego użytkownika.");
                        refreshTable();
                        //e.printStackTrace();
                    }
                } else {
                    AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Wprowadzona nazwa magazynu jest za długa.\n(maksymalna długość: 30 znaków");
                    warehousesTable.refresh();
                }
            }
        });

        refreshTable();
    }
}
