package magazynier.utils;

import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.Map;

public class MaxTextFieldLengthFormValidator {

    public static boolean test(Pane form, final int MAX_TEXT_FIELD_LENGTH, Map<TextField, Integer> customLimits) {
        return form.getChildren().stream().allMatch(node -> {
            if (node instanceof TextField && !customLimits.containsKey(node)) {
                TextField textField = (TextField) node;
                return textField.getText() == null || textField.getText().length() <= MAX_TEXT_FIELD_LENGTH;
            } else {
                return true;
            }
        }) && (customLimits.keySet().stream().allMatch(
                node -> node.getText() == null || node.getText().length() <= customLimits.get(node)
        ));
    }

    public static boolean test(Pane form, final int MAX_TEXT_FIELD_LENGTH) {
        return test(form, MAX_TEXT_FIELD_LENGTH, new HashMap<>());
    }

    private MaxTextFieldLengthFormValidator() {
    }
}
