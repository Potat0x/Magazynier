package magazynier;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import magazynier.entities.Item;
import magazynier.entities.VatRate;
import magazynier.utils.AlertLauncher;
import magazynier.utils.validators.EanValidator;
import magazynier.utils.TextFieldCorrectnessIndicator;
import magazynier.utils.TextFieldOverflowIndicator;

public class ItemController {

    enum Mode {
        ADD_ITEM,
        EDIT_ITEM
    }

    public enum Action {
        SAVE,
        CANCEL
    }

    private final int MAX_ITEM_MODEL_NUMBER_LENGTH = 30;
    private final int MAX_ITEM_NAME_LENGTH = 50;
    private final int MAX_MEASUR_UNIT_NAME_LENGTH = 15;

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
    private Item item;
    private Mode mode;
    private Action userAction;
    private EanValidator eanValidator;

    public ItemController(Item item, Mode mode) {
        this.item = item;
        this.mode = mode;
        userAction = Action.CANCEL;
        eanValidator = new EanValidator();
    }

    @FXML
    public void initialize() {

        TextFieldOverflowIndicator.set(itemModelNumber, MAX_ITEM_MODEL_NUMBER_LENGTH);
        TextFieldOverflowIndicator.set(name, MAX_ITEM_NAME_LENGTH);
        TextFieldOverflowIndicator.set(measurementUnit, MAX_MEASUR_UNIT_NAME_LENGTH);

        if (mode == Mode.EDIT_ITEM) {
            updateFormFromItem(item);
        }

        if (mode == Mode.ADD_ITEM) {
            titleLabel.setText("Dodaj");
        } else {
            titleLabel.setText("Edytuj");
        }

        vatRate.getItems().addAll(ItemModel.getVatList());

        if (mode == Mode.EDIT_ITEM) {
            vatRate.getSelectionModel().select(item.getVatRate());
        } else {
            vatRate.getSelectionModel().select(ItemModel.getVatList().get(0));
        }

        ean.textProperty().addListener(new TextFieldCorrectnessIndicator(new EanValidator()));
    }

    public void saveItem() {

        Stage stage = (Stage) cancelButton.getScene().getWindow();

        boolean formLengthValid = validFormMaxLength();
        boolean nipValid = eanValidator.check(ean.getText());

        if (formLengthValid && nipValid) {
            updateItemFromForm(item);
            userAction = Action.SAVE;
            stage.close();
        } else if (formLengthValid) {
            AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Wprowadzony numer EAN jest niepoprawny.");
        } else {
            AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Wprowadzony tekst jest za długi.\n" +
                    "Limity długości:\n\tnazwa przedmiotu: " + MAX_ITEM_MODEL_NUMBER_LENGTH + " znaków." +
                    "\n\tmodel: " + MAX_ITEM_MODEL_NUMBER_LENGTH + " znaków." +
                    "\n\tjednostka miary: " + MAX_MEASUR_UNIT_NAME_LENGTH + " znaków.");
        }

    }

    public void cancelItemAddOrEdit() {
        userAction = Action.CANCEL;
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    double stringToDouble(String str) {
        double val = 0;
        try {
            val = Double.parseDouble(str);
        } catch (Exception e) {
            val = 0;
        }
        return val;
    }

    private void updateItemFromForm(Item item) {
        item.setEan(ean.getText());
        item.setItemModelNumber(itemModelNumber.getText());
        item.setName(name.getText());
        item.setMeasurementUnit(measurementUnit.getText());
        item.setDescription(description.getText());
        item.setVatRate((VatRate) vatRate.getSelectionModel().getSelectedItem());
        item.setCurrentPrice(stringToDouble(price.getText()));
        item.setDesiredQuantity(stringToDouble(desiredQuantity.getText()));
    }

    private void updateFormFromItem(Item item) {
        ean.setText(item.getEan());
        itemModelNumber.setText(item.getItemModelNumber());
        name.setText(item.getName());
        measurementUnit.setText(item.getMeasurementUnit());
        description.setText(item.getDescription());
        vatRate.getSelectionModel().select(item.getVatRate());
        price.setText(String.valueOf(item.getCurrentPrice()));
        desiredQuantity.setText(String.valueOf(item.getDesiredQuantity()));
    }

    public Action getUserAction() {
        return userAction;
    }

    public boolean validFormMaxLength() {
        if ((itemModelNumber.getText() == null || itemModelNumber.getText().length() <= MAX_ITEM_MODEL_NUMBER_LENGTH) &&
                (name.getText() == null || name.getText().length() <= MAX_ITEM_NAME_LENGTH) &&
                (measurementUnit.getText() == null || measurementUnit.getText().length() <= MAX_MEASUR_UNIT_NAME_LENGTH)) {
            return true;
        }
        return false;
    }

}
