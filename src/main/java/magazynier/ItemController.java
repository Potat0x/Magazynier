package magazynier;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ItemController {

    public TextField name;
    public TextField ean;
    public TextField itemModelNumber;
    public TextField measurementUnit;
    public TextField price;
    public TextField desiredQuantity;
    public ComboBox vatRate;
    public TextArea description;
    public Button saveButton;
    public Button cancelButton;
    public Label titleLabel;

    public ItemController(String param) {
        this.param = param;
    }

    private String param;


    @FXML
    public void initialize() {
        System.out.println("ItemController initialize");
        titleLabel.setText(param);
    }

    public void saveItem() {
    }

    public void cancelItemAddOrEdit() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }


}
