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
import magazynier.utils.TextFieldOverflowIndicator;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;

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
                        TableRow<Warehouse> row = new TableRow<>();

                        ContextMenu cmenu = new ContextMenu();
                        MenuItem del = new MenuItem("Usuń");
                        MenuItem add = new MenuItem("Dodaj");
                        del.setOnAction(event -> {

                            if (row.getItem() != null) {
                                try {
                                    model.deleteWarehouse(row.getItem());
                                    warehousesTable.getItems().remove(row.getItem());
                                } catch (NotFoundException e) {
                                    AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Nie można usunąć magazynu \"" + row.getItem().getName() + "\".\nMógł zostać wcześniej usunięty przez innego użytkownika.");
                                    refreshTable();
                                    //e.printStackTrace();
                                } catch (PersistenceException e) {
                                    AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Nie można usunąć magazynu \"" + row.getItem().getName() + "\".\nJest do niego przypisany asortyment.");
                                }
                            }
                        });

                        add.setOnAction(event -> {

                            TextInputDialog t = new TextInputDialog();
                            t.setGraphic(null);
                            t.setTitle("Dodawanie magazynu");
                            t.setHeaderText("Podaj nazwę nowego magazynu");
                            TextFieldOverflowIndicator.set(t.getEditor(), MAX_NAME_LEN);
                            Optional<String> optName = t.showAndWait();

                            optName.ifPresent(warehouseName -> {
                                if (warehouseName.length() <= MAX_NAME_LEN) {
                                    Warehouse newWarehouse = new Warehouse();
                                    newWarehouse.setName(warehouseName);
                                    model.addWarehouse(newWarehouse);//todo: add error handling
                                    tableView.getItems().add(newWarehouse);
                                } else {
                                    AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Wprowadzona nazwa magazynu jest za długa.\n(maksymalna długość: 30 znaków");
                                }
                            });
                        });

                        cmenu.getItems().add(add);
                        cmenu.getItems().add(del);
                        //row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty())).then(cmenu).otherwise((ContextMenu) null));
                        row.setContextMenu(cmenu);
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
