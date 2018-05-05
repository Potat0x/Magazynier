package magazynier;

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
import javassist.NotFoundException;
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
    private ItemModel model;

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        System.out.println("AssortmentController initialize");
        eanCol.setCellValueFactory(new PropertyValueFactory<String, Item>("ean"));
        nameCol.setCellValueFactory(new PropertyValueFactory<String, Item>("name"));
        itemModelNumberCol.setCellValueFactory(new PropertyValueFactory<String, Item>("itemModelNumber"));
        priceCol.setCellValueFactory(new PropertyValueFactory<String, Item>("price"));
        taxCol.setCellValueFactory(new PropertyValueFactory<String, Item>("vatRateId"));

        refreshTable();
    }

    private void refreshTable() {
        itemsTable.getItems().clear();
        itemsTable.getItems().addAll(model.getItemsList());
    }

    public AssortmentController() {
        System.out.println("AssortmentController constr");
        model = new ItemModel();
    }

    public void addItem() {
        showItemWindow("Dodawanie towaru");
    }

    public void editItem() {
        showItemWindow("Edytowanie towaru");
    }

    public void deleteItem() {
        Item selectedItem = (Item) itemsTable.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            try {
                model.deleteItem(selectedItem);
                itemsTable.getItems().remove(selectedItem);
                itemsTable.getSelectionModel().select(null);
            } catch (NotFoundException e) {
                AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie można usunąć przedmiotu.",
                        "Nie znaleziono przedmiotu. Mógł zostać wcześniej usunięty przez innego użytkownika.");
                refreshTable();
            } catch (PersistenceException e) {
                AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie można usunąć przedmiotu.",
                        "Ten przedmiot występuje na dokumentach.");
            } catch (Exception e) {
                AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie można usunąć przedmiotu.",
                        "Nieznany błąd.");
                refreshTable();
            }
        }


    }

    private void showItemWindow(String title) {
        FXMLLoader itemStageLoader = new FXMLLoader(getClass().getResource("/fxml/item_window.fxml"));
        ItemController ic = new ItemController(title);
        itemStageLoader.setController(ic);
        Stage itemStage = new Stage();
        itemStage.setTitle("Dodawanie towaru");
        Parent parent = null;
        try {
            parent = itemStageLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        itemStage.setScene(new Scene(parent));
        itemStage.show();
    }
}
