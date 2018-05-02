package magazynier;

import javafx.scene.control.TextField;

public class TextFieldOverflowIndicator {

    private final static String textFieldErrorStyle = "-fx-text-box-border: rgb(255,117,0); -fx-focus-color: rgb(255,117,0);";

    static void set(TextField textField, int maxLength) {

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxLength) {
                textField.setStyle(textFieldErrorStyle);
            } else {
                textField.setStyle(null);
            }
        });
    }
}
