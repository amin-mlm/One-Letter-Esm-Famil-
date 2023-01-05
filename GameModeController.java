package com.example.newesmfamil2;

import io.github.palexdev.materialfx.controls.MFXButton;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class GameModeController {

    @FXML
    private MFXButton createButton;

    @FXML
    private MFXButton joinButton;

    @FXML
    private AnchorPane rootPane;

    static FXMLLoader fxmlLoader;


    @FXML
    void initialize() {
        createButton.setOnAction(actionEvent -> {
            fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/newesmfamil2/createGame.fxml"));
            try {
                rootPane.getChildren().setAll((Pane)(fxmlLoader.load()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        joinButton.setOnAction(actionEvent -> {
            fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/newesmfamil2/joinGame.fxml"));
            try {
                rootPane.getChildren().setAll((Pane)(fxmlLoader.load()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }

}
