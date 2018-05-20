package magazynier;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import magazynier.item.Item;
import magazynier.item.ItemController;
import magazynier.item.ItemModel;
import magazynier.utils.AlertLauncher;
import magazynier.utils.MoneyValueFormat;

import javax.persistence.PersistenceException;
import java.io.IOException;

import static magazynier.utils.WindowCreator.createWindowFromFxml;

public class AssortmentController {

    public Button delete;
    public TableColumn<Item, String> nameCol;
    public TableColumn<Item, String> eanCol;
    public TableColumn<Item, String> itemModelNumberCol;
    public TableColumn<Item, Double> quantityCol;
    public TableColumn<Item, Double> desiredQuantityCol;
    public TableColumn<Item, String> priceCol;
    public TableColumn<Item, String> taxCol;
    public TableColumn<Item, String> warehousesCol;
    public TableColumn<Item, Double> grossValCol;
    public TableColumn<Item, Double> netValCol;
    public TableView<Item> itemsTable;
    public Button editButton;
    public Button deleteButton;
    public Label netValLabel;
    public Label grossValLabel;
    private ItemModel model;

    public AssortmentController() {
        model = new ItemModel();
    }

    @FXML
    public void initialize() {

        eanCol.setCellValueFactory(new PropertyValueFactory<>("ean"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        itemModelNumberCol.setCellValueFactory(new PropertyValueFactory<>("itemModelNumber"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("currentPrice"));
        taxCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getVatRate().getName()));

        MoneyValueFormat moneyFormat = new MoneyValueFormat();
        quantityCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(Double.parseDouble(moneyFormat.
                format(model.getAvailableQuantity(c.getValue())))));//todo: 3x sout * item_count

        grossValCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(Double.parseDouble(moneyFormat.
                format(model.getItemTotalGrossValue(c.getValue())))));

        netValCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(Double.parseDouble(moneyFormat.
                format(model.getItemTotalNetValue(c.getValue())))));

        itemsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            boolean rowNotSelected = newValue == null;
            editButton.setDisable(rowNotSelected);
            deleteButton.setDisable(rowNotSelected);
        });

        desiredQuantityCol.setCellFactory(ProgressBarTableCell.forTableColumn());
        desiredQuantityCol.setCellValueFactory(i -> new ReadOnlyObjectWrapper<>(model.getAvailableQuantity(i.getValue())/i.getValue().getDesiredQuantity()));

        refreshTable();
    }

    private void refreshTable() {
        itemsTable.getItems().clear();
        itemsTable.getItems().addAll(model.getItemsList());
    }

    public void addItem() {
        Item item = new Item();
        ActionResult actionResult = showItemWindow(item, ActionMode.ADD);
        if (actionResult == ActionResult.CONFIRM) {
            itemsTable.getItems().add(item);
            itemsTable.refresh();
        }
    }

    public void editItem() {
        Item item = itemsTable.getSelectionModel().getSelectedItem();
        ActionResult actionResult = showItemWindow(item, ActionMode.EDIT);

        if (actionResult == ActionResult.CONFIRM) {
            itemsTable.refresh();
        } else if (actionResult == ActionResult.FAIL) {
            refreshTable();
        }
    }

    public void deleteItem() {
        Item selectedItem = itemsTable.getSelectionModel().getSelectedItem();

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

    private ActionResult showItemWindow(Item item, ActionMode mode) {

        ItemController ic = new ItemController(item, mode);
        try {
            Stage itemStage = createWindowFromFxml("/fxml/item_window.fxml", ic, "Towar");
            itemStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ic.getActionResult();
    }

    @FXML
    public void refreshItemsList() {
        refreshTable();
    }
}
