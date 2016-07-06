package ru.schegrov;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.schegrov.controller.AppController;
import ru.schegrov.model.AppModel;
import java.util.Locale;
import java.util.ResourceBundle;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws Exception {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("config.locale"/*, new Locale("en")*/);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/app.fxml"));
        loader.setResources(resourceBundle);
        loader.setControllerFactory(t -> new AppController(new AppModel()));
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/pic/title.png")));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle(resourceBundle.getString("app.title"));
        stage.show();
    }
}
