package magazynier;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import magazynier.entities.Warehouse;

import java.util.ArrayList;

public class WarehousesController {

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

    @FXML
    public void initialize() {

        nameCol.setCellValueFactory(new PropertyValueFactory<Warehouse, String>("name"));
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());

        nameCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
            @Override
            public void handle(TableColumn.CellEditEvent event) {
                Warehouse warehouse = (Warehouse) event.getTableView().getItems().get(event.getTablePosition().getRow());
                warehouse.setName((String) event.getNewValue());
                model.updateWarehouse(warehouse);
                System.out.println(warehouse);
            }
        });

        ArrayList wl = model.getWarehousesList();
        warehousesTable.getItems().addAll(wl);

        //warehousesTable.getItems().add(new Warehouse("NEW WAREHOUSE"));
    }
}
