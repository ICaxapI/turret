package ru.exsoft.turret.Gui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.FileInputStream;
import java.io.IOException;

public class Gui extends Application {


    public static void main(String[] args) {
       launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("MovingDetector");
        primaryStage.setResizable(false);
        FXMLLoader loader = new FXMLLoader();
        AnchorPane root = loader.load(new FileInputStream("./general.fxml")); //this.getClass().getResource("general.fxml").openStream()
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(we -> GeneralController.instance.stop());
    }
}
