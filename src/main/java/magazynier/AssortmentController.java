package magazynier;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import magazynier.entities.Item;
import magazynier.utils.AlertLauncher;

import javax.persistence.PersistenceException;
import java.io.IOException;

public class AssortmentController {

    public Button delete;
    public TableColumn nameCol;
    public TableColumn eanCol;
    public TableColumn itemModelNumberCol;
    public TableColumn quantityCol;
    public TableColumn priceCol;
    public TableColumn taxCol;
    public TableColumn warehousesCol;
    public TableView itemsTable;
    public Button editButton;
    public Button deleteButton;
    private ItemModel model;

    public AssortmentController() {
        model = new ItemModel();
    }

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        //todo:quantityCol as progressbar
        eanCol.setCellValueFactory(new PropertyValueFactory<String, Item>("ean"));
        nameCol.setCellValueFactory(new PropertyValueFactory<String, Item>("name"));
        itemModelNumberCol.setCellValueFactory(new PropertyValueFactory<String, Item>("itemModelNumber"));
        priceCol.setCellValueFactory(new PropertyValueFactory<String, Item>("currentPrice"));
        taxCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Item, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Item, String> p) {
                return new ReadOnlyObjectWrapper(p.getValue().getVatRate().getName());
            }
        });

        itemsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            boolean rowNotSelected = newValue == null;
            editButton.setDisable(rowNotSelected);
            deleteButton.setDisable(rowNotSelected);
        });

        refreshTable();
    }

    private void refreshTable() {
        itemsTable.getItems().clear();
        itemsTable.getItems().addAll(model.getItemsList());
    }

    public void addItem() {
        Item item = new Item();
        ItemController.ActionResult actionResult = showItemWindow(item, ItemController.Mode.ADD_ITEM);
        if (actionResult == ItemController.ActionResult.CONFIRM) {
            itemsTable.getItems().add(item);
            itemsTable.refresh();
        }
    }

    public void editItem() {
        Item item = (Item) itemsTable.getSelectionModel().getSelectedItem();
        ItemController.ActionResult actionResult = showItemWindow(item, ItemController.Mode.EDIT_ITEM);

        if (actionResult == ItemController.ActionResult.CONFIRM) {
            itemsTable.refresh();
        } else if (actionResult == ItemController.ActionResult.FAIL) {
            refreshTable();
        }
    }

    public void deleteItem() {
        Item selectedItem = (Item) itemsTable.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            try {
                model.deleteItem(selectedItem);
                itemsTable.getItems().remove(selectedItem);
                itemsTable.getSelectionModel().select(null);
            } catch (RowNotFoundException e) {
                AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie można usunąć przedmiotu.",
                        "Nie znaleziono przedmiotu. Mógł zostać wcześniej usunięty przez innego użytkownika.");
                refreshTable();
            } catch (PersistenceException e) {
                AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie można usunąć przedmiotu.",
                        "Ten przedmiot występuje na dokumentach.");
            } catch (Exception e) {
                AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie można usunąć przedmiotu.",
                        "Nieznany błąd.");
                e.printStackTrace();
                refreshTable();
            }
        }
    }

    private ItemController.ActionResult showItemWindow(Item item, ItemController.Mode mode) {
        FXMLLoader itemStageLoader = new FXMLLoader(getClass().getResource("/fxml/item_window.fxml"));
        ItemController ic = new ItemController(item, mode);
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
        return ic.getActionResult();
    }
}
