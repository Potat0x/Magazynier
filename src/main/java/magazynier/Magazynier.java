package magazynier;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Magazynier extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Magazynier");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        //MainController mc = fxmlLoader.getController();
//        primaryStage.setMaximized(true);
//        primaryStage.setFullScreen(true);
    }

    @Override
    public void stop() {
        HibernateSessionFactory.close();
    }
}
