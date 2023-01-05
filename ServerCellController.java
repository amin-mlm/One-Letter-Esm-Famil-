package com.example.newesmfamil2;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyListCell;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class ServerCellController extends MFXLegacyListCell<Server> {

    @FXML
    private Label gameNameLabel;

    @FXML
    private Label hostLabel;

//    @FXML
//    private Label idLabel;

    @FXML
    private MFXPasswordField passField;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Label roundsLabel;

    @FXML
    private MFXButton startButton;

    private FXMLLoader fxmlLoader;

    @Override
    protected void updateItem(Server item, boolean empty) {
        super.updateItem(item, empty);

        if(empty || item==null){
            setText(null);
            setGraphic(null);
        }else{
            if (fxmlLoader == null ) {
                fxmlLoader = new FXMLLoader(getClass()
                        .getResource("/com/example/newesmfamil2/serverCell.fxml"));
                fxmlLoader.setController(this);
                try {
                    fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

//            idLabel.setText("port: " + item.getPort());
            gameNameLabel.setText(item.getGameName());
            hostLabel.setText(item.getHostName());
            roundsLabel.setText(item.getRounds()+"");

            startButton.setOnAction(actionEvent ->{
                if(passField.getText().equals(item.getPassword())){
//                    new Thread(()->{
//                        Platform.runLater(()->{
                            ((JoinGameController)(GameModeController.fxmlLoader.getController())).joinToServer(item.getPort());
//                        });
//                    }).start();
//                    JoinGameController.waiteToStartGame(item.getId());
                }
                else{
                    // add needs
                }
            });

            setText(null);
            setGraphic(rootPane);
        }
    }
}
