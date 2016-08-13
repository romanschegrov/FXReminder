package ru.schegrov;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.log4j.Logger;
import ru.schegrov.controller.AppController;
import ru.schegrov.model.AppModel;
import ru.schegrov.util.HibernateHelper;

import java.util.Locale;
import java.util.ResourceBundle;

public class App extends Application {

    private static final Logger logger = Logger.getLogger(App.class);

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws Exception {

        logger.info("start");

        ResourceBundle resourceBundle = ResourceBundle.getBundle("config.locale", new Locale("en"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/app.fxml"));
        loader.setResources(resourceBundle);
        loader.setControllerFactory(t -> new AppController());
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/pic/title.png")));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle(resourceBundle.getString("app.title"));
        stage.setOnCloseRequest(event -> HibernateHelper.closeSessionFactory());
        stage.show();

        logger.info("end");
    }
}
