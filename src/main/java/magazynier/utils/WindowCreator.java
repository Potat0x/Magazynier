package magazynier.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Pozwala na stworzenie okna z pliku FXML.
 *
 * @author ziemniak
 */
public class WindowCreator {

    /**
     * Tworzy okno z pliku FXML.
     * Podany plik nie moze miec zdefiniowanego kontrolera. Kontroler jest podawany przez konstruktor.
     *
     * @param filename   nazwa pliku .fxml zawierajacego okno
     * @param controller konroler dla okna
     * @param title      tytul okna
     * @return stworzone okno
     * @throws IOException gdy nie znaleziono podanego pliku
     */
    public static Stage createWindowFromFxml(String filename, Object controller, String title) throws IOException {
        FXMLLoader itemStageLoader = new FXMLLoader(WindowCreator.class.getResource(filename));
        itemStageLoader.setController(controller);
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(itemStageLoader.load()));
        return stage;
    }
}
