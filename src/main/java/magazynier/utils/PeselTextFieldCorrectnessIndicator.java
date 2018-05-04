package magazynier.utils;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class PeselTextFieldCorrectnessIndicator implements javafx.beans.value.ChangeListener<String> {

    private final static String textFieldErrorStyle = "-fx-text-box-border: rgb(255,117,0); -fx-focus-color: rgb(255,117,0);";

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

        TextField peselTextField = (TextField) ((StringProperty) observable).getBean();

        if (!PeselValidator.check(peselTextField.getText())) {
            peselTextField.setStyle(textFieldErrorStyle);
        } else {
            peselTextField.setStyle(null);
        }
    }
}
