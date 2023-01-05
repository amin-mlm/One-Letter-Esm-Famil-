package com.example.newesmfamil2;

import io.github.palexdev.materialfx.controls.MFXButton;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

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
//        BackgroundImage myBI1= new BackgroundImage(new Image("C:\\Users\\USER\\Desktop\\java desktop\\esmFamilAll\\newEsmFamil2\\src\\main\\resources\\com\\example\\newesmfamil2\\assets\\no1.jpg",170,83,false,true),
//                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
//                BackgroundSize.DEFAULT);
//        createButton.setBackground(new Background(myBI1));
//
//
//        BackgroundImage myBI0= new BackgroundImage(new Image("C:\\Users\\USER\\Desktop\\java desktop\\esmFamilAll\\newEsmFamil2\\src\\main\\resources\\com\\example\\newesmfamil2\\assets\\no0.jpg",170,83,false,true),
//                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
//                BackgroundSize.DEFAULT);
//        joinButton.setBackground(new Background(myBI0));

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
