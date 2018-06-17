package magazynier.utils;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

/**
 * Klasa do czyszczenia formularzy.
 * Formularz - obiekt typu Pane zawierajacy obiekty TextField i ComboBox
 *
 * @author ziemniak
 */

public class FormCleaner {

    /**
     * Czyści zawartość kontrolek
     *
     * @param form - formularz
     */

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

    /**
     * Przywraca domyślny styl kontrolek
     *
     * @param form - formularz
     */
    public static void clearStyles(Pane form) {
        for (Node n : form.getChildren()) {
            if (n instanceof TextField) {
                n.setStyle(null);
            }
        }
    }
}
