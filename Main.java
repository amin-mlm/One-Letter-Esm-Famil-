package com.example.newesmfamil2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class Main extends Application {
    static Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/newesmfamil2/gameMode.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 934, 555);
        stage.setTitle("Esm Famil");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
//        DatabaseHandler databaseHandler = new DatabaseHandler();
//        databaseHandler.removeServer(5);
    }
//
//    @Override
//    public void stop() throws Exception {
//        System.out.println("closing..");
//    }
}