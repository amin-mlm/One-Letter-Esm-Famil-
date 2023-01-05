package com.example.newesmfamil2;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyListView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.*;
import java.util.ArrayList;


public class JoinGameController {

    @FXML
    public MFXTextField clientNameField;

    @FXML
    public MFXButton refreshButton;

    @FXML
    public AnchorPane rootPane;

    @FXML
    private Label gameStateLabel;

    @FXML
    private MFXLegacyListView<Server> serverListView;

    private ObservableList<Server> serversObservableList;

    private DatabaseHandler databaseHandler = new DatabaseHandler();

    private int gamePort = -1;

    @FXML
    void initialize() {
        ArrayList<Server> servers = databaseHandler.showServers();
        System.out.println("in JoinGameContro: " + servers.size());
        serversObservableList = FXCollections.observableArrayList();
        for(Server server : servers){
            serversObservableList.add(server);
        }

        serverListView.setItems(serversObservableList);
        serverListView.setCellFactory(param -> new ServerCellController());

        refreshButton.setOnAction(actionEvent -> initialize());

    }

    public void joinToServer(int gamePort){
        if(this.gamePort!=-1){ //game is already chosen
            return;
        }
        this.gamePort = gamePort;

        //if client name was empty add needs
        String clientName = clientNameField.getText();

        sayWelcome(gamePort);

        Client client = new Client(clientName);

        new Thread( ()->{
            client.setJoinGameController(this);
            client.joinToServer(gamePort);
            client.waiteForStart();
        }).start();


    }

    private void sayWelcome(int gameId){
        gameStateLabel.setText("welcome to gameId " + gameId +
                "\nwaite for others to join");
        //add needs
    }

    public void gotoGameScreen(Client client){
//        new Thread(()->{
//            Platform.runLater(()->{
//                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/newesmfamil2/gameScreen.fxml"));
//                try {
//                    Parent root = fxmlLoader.load();
//                    rootPane.getChildren().setAll(root);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            });
//
//        }).start();

//        new Thread(()->{
            Platform.runLater(()->{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/newesmfamil2/gameScreen.fxml"));
                GameScreenController gameScreenController = new GameScreenController();
                try {
                    fxmlLoader.setController(gameScreenController);
                    gameScreenController.setClient(client);
                    Parent root = fxmlLoader.load();
                    rootPane.getChildren().setAll(root);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
//        }).start();


    }


    public void notifHostLeftGame() {
        Platform.runLater(()->{
            gameStateLabel.setText(":(");
        });
    }
}
