package magazynier.utils;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import magazynier.utils.validators.Validator;

public class TextFieldCorrectnessIndicator implements javafx.beans.value.ChangeListener<String> {
    private final static String textFieldErrorStyle = "-fx-text-box-border: rgb(255,117,0); -fx-focus-color: rgb(255,117,0);";
    private Validator validator;

    public TextFieldCorrectnessIndicator(Validator validator) {
        this.validator = validator;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        TextField textField = (TextField) ((StringProperty) observable).getBean();

        if (!validator.check(textField.getText())) {
            textField.setStyle(textFieldErrorStyle);
        } else {
            textField.setStyle(null);
        }
    }
}
