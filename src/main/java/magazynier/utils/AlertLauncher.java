package magazynier.utils;

import javafx.scene.control.Alert;

/**
 * Klasa ulatwiajaca wywolywanie okienek komunikatow dla uzytkownika.
 *
 * @author ziemniak
 */
public class AlertLauncher {

    /**
     * Wyswietla blokujacy komunikat.
     *
     * @param type    typ
     * @param title   tytul
     * @param header  naglowek
     * @param content tresc
     */

    public static void showAndWait(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
