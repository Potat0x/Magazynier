package magazynier;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javassist.NotFoundException;
import magazynier.entities.Warehouse;

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
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Błąd");
                        alert.setHeaderText(null);
                        alert.setContentText("Nie znaleziono magazynu \"" + warehouse.getName() + "\".\nMógł zostać usunięty przez innego użytkownika.");
                        alert.showAndWait();
                        refreshTable();
                        e.printStackTrace();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Błąd");
                    alert.setHeaderText(null);
                    alert.setContentText("Wprowadzona nazwa magazynu jest za długa (max 30 znaków).");
                    alert.showAndWait();
                    warehousesTable.refresh();
                }
            }
        });

        refreshTable();
    }
}
