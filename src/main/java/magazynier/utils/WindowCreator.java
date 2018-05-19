package magazynier.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WindowCreator {

    public static Stage createWindowFromFxml(String filename, Object controller, String title) throws IOException {
        FXMLLoader itemStageLoader = new FXMLLoader(WindowCreator.class.getResource(filename));
        itemStageLoader.setController(controller);
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(itemStageLoader.load()));
        return stage;
    }
}
