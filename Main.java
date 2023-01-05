package com.example.newesmfamil2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    static Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/newesmfamil2/gameMode.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 934, 555);
        stage.setScene(scene);
        stage.setTitle("Esm Famil");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
//        DatabaseHandler databaseHandler = new DatabaseHandler();
//        databaseHandler.removeServer(5);
    }}