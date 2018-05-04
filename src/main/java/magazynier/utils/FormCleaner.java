package magazynier.utils;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class FormCleaner {
    public static void clearForm(Pane form) {
        for (Node n : form.getChildren()) {
            if (n instanceof TextField) {
                ((TextField) n).clear();
            } else if (n instanceof ComboBox) {
                //noinspection unchecked
                ((ComboBox) n).getSelectionModel().select(null);
            }
        }
    }

    public static void clearStyles(Pane form) {
        for (Node n : form.getChildren()) {
            if (n instanceof TextField) {
                n.setStyle(null);
            }
        }
    }
}
