package ru.schegrov;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Created by ramon on 21.06.2016.
 */
public class TestApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.setOut(null);
        stage.setScene(new Scene(new Pane(new Label("My test label")),200, 200));
        stage.setTitle("Test");
        stage.show();
    }
}
