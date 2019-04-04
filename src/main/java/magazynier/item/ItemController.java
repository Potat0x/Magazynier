package magazynier.item;

import antlr.StringUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import magazynier.ActionMode;
import magazynier.ActionResult;
import magazynier.MeasurementUnit;
import magazynier.RowNotFoundException;
import magazynier.utils.AlertLauncher;
import magazynier.utils.StringToDoubleConverter;
import magazynier.utils.TextFieldCorrectnessIndicator;
import magazynier.utils.validators.EanValidator;
import magazynier.utils.validators.LengthValidator;

//noinspection unchecked
@SuppressWarnings("WeakerAccess")
public class ItemController {

    private final int MAX_ITEM_MODEL_NUMBER_LENGTH = 30;
    private final int MAX_ITEM_NAME_LENGTH = 50;
    private final int MAX_MEASUR_UNIT_NAME_LENGTH = 15;

    public TextField name;
    public TextField ean;
    public TextField itemModelNumber;
    public ComboBox<MeasurementUnit> measurementUnit;
    public TextField price;
    public TextField desiredQuantity;
    public ComboBox<VatRate> vatRate;
    public TextArea description;
    public Button saveButton;
    public Button cancelButton;
    public Label titleLabel;
    private Item item;
    private ActionMode mode;
    private ActionResult actionResult;
    private EanValidator eanValidator;
    private ItemModel model;

    public ItemController(Item item, ActionMode mode) {
        this.item = item;
        this.mode = mode;
        actionResult = ActionResult.CANCEL;
        eanValidator = new EanValidator();
        model = new ItemModel();
    }

    @FXML
    public void initialize() {

        itemModelNumber.textProperty().addListener(new TextFieldCorrectnessIndicator(new LengthValidator(MAX_ITEM_MODEL_NUMBER_LENGTH)));
        name.textProperty().addListener(new TextFieldCorrectnessIndicator(new LengthValidator(MAX_ITEM_NAME_LENGTH)));
        measurementUnit.getItems().addAll(ItemModel.getMeasurementUnitsList());


        if (mode == ActionMode.EDIT) {
            updateFormFromItem(item);
        }

        if (mode == ActionMode.ADD) {
            titleLabel.setText("Dodaj");
        } else {
            titleLabel.setText("Edytuj");
        }

        vatRate.getItems().addAll(ItemModel.getVatList());

        if (mode == ActionMode.EDIT) {
            vatRate.getSelectionModel().select(item.getVatRate());
        } else {
            vatRate.getSelectionModel().select(ItemModel.getVatList().get(0));
            measurementUnit.getSelectionModel().select(ItemModel.getMeasurementUnitsList().get(0));
        }

        ean.textProperty().addListener(new TextFieldCorrectnessIndicator(new EanValidator()));
    }

    @FXML
    public void saveItem() {

        Stage stage = (Stage) cancelButton.getScene().getWindow();

        boolean formLengthValid = validFormMaxLength();
        boolean eanValid = eanValidator.check(ean.getText());
        boolean numericFieldsValid = checkIfNumericFieldsValid();
        System.out.println("NUM VALID ="+numericFieldsValid);

        if (formLengthValid && eanValid && numericFieldsValid) {
            updateItemFromForm(item);
            actionResult = ActionResult.CONFIRM;
            if (mode == ActionMode.ADD) {
                try {
                    model.addItem(item);
                } catch (Exception e) {
                    actionResult = ActionResult.FAIL;
                    AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie udało się dodać przedmiotu.", "Nieznany błąd.");
                    e.printStackTrace();
                }
            } else {
                try {
                    model.updateItem(item);
                } catch (RowNotFoundException e) {
                    actionResult = ActionResult.FAIL;
                    AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie można zaktualizować przedmiotu.", "Nie znalaziono przedmiotu. Mógł zostać usunięty z bazy.");
                } catch (Exception e) {
                    actionResult = ActionResult.FAIL;
                    AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie można zaktualizować przedmiotu.", "Nieznany błąd.");
                    e.printStackTrace();
                }
            }

            stage.close();
        } else if (!eanValid) {
            AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Wprowadzony numer EAN jest niepoprawny.");
        } else if (!numericFieldsValid){
            AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Wprowadzone liczby są nieprawidłowe.");
        }else if(!formLengthValid){
            AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", null, "Wprowadzony tekst jest za długi.\n" +
                    "Limity długości:\n\tnazwa przedmiotu: " + MAX_ITEM_MODEL_NUMBER_LENGTH + " znaków." +
                    "\n\tmodel: " + MAX_ITEM_MODEL_NUMBER_LENGTH + " znaków." +
                    "\n\tjednostka miary: " + MAX_MEASUR_UNIT_NAME_LENGTH + " znaków.");
        }

    }

    @FXML
    public void cancelItemAddOrEdit() {
        actionResult = ActionResult.CANCEL;
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private boolean checkIfNumericFieldsValid() {
        try {
            StringToDoubleConverter.convert(price.getText());
            StringToDoubleConverter.convert(desiredQuantity.getText());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void updateItemFromForm(Item item) {
        item.setEan(ean.getText());
        item.setItemModelNumber(itemModelNumber.getText());
        item.setName(name.getText());
        item.setMeasurementUnit(measurementUnit.getSelectionModel().getSelectedItem());
        item.setDescription(description.getText());
        item.setVatRate(vatRate.getSelectionModel().getSelectedItem());
        item.setCurrentPrice(StringToDoubleConverter.convert(price.getText()));
        item.setDesiredQuantity(StringToDoubleConverter.convert(desiredQuantity.getText()));
    }

    private void updateFormFromItem(Item item) {
        ean.setText(item.getEan());
        itemModelNumber.setText(item.getItemModelNumber());
        name.setText(item.getName());
        measurementUnit.getSelectionModel().select(item.getMeasurementUnit());
        description.setText(item.getDescription());
        vatRate.getSelectionModel().select(item.getVatRate());
        price.setText(String.valueOf(item.getCurrentPrice()));
        desiredQuantity.setText(String.valueOf(item.getDesiredQuantity()));
    }

    public ActionResult getActionResult() {
        return actionResult;
    }

    private boolean validFormMaxLength() {
        return (itemModelNumber.getText() == null || itemModelNumber.getText().length() <= MAX_ITEM_MODEL_NUMBER_LENGTH) &&
                (name.getText() == null || name.getText().length() <= MAX_ITEM_NAME_LENGTH);
    }

}
