package magazynier.utils;

import javafx.scene.control.Alert;

public class AlertLauncher {

    public static void showAndWait(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
