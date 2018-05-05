package magazynier;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AssortmentController {

    @FXML
    public void initialize() {
        System.out.println("AssortmentController initialize");
    }

    public AssortmentController() {
        System.out.println("AssortmentController constr");
    }

    public void addItem() {
        showItemWindow("Dodawanie towaru");
    }

    public void editItem() {
        showItemWindow("Edytowanie towaru");
    }

    public void deleteItem() {
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
