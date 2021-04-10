package com.company;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class Main extends Application {

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        primaryStage.setTitle("Words parser");
        Parent layout = FXMLLoader.load(getClass().getResource("layout.fxml"));
        Scene scene = new Scene(layout, 640, 400);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
