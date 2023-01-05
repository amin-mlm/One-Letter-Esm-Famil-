package com.example.newesmfamil2;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyListView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
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
    private MFXLegacyListView<Server> serverListView;

    private ObservableList<Server> serversObservableList;

    private DatabaseHandler databaseHandler = new DatabaseHandler();

    @FXML
    void initialize() {
        ArrayList<Server> servers = databaseHandler.showServers();
        System.out.println("in JoinGameContro: " + servers);
        serversObservableList = FXCollections.observableArrayList();
        for(Server server : servers){
            serversObservableList.add(server);
        }

        serverListView.setItems(serversObservableList);
        serverListView.setCellFactory(param -> new ServerCellController());

        refreshButton.setOnAction(actionEvent -> initialize());

    }

    public void joinToServer(int gameId){
        //if client name was empty add needs
        String clientName = clientNameField.getText();

        sayWelcome(gameId);

        Client client = new Client(clientName);

        new Thread( ()->{
            client.joinToServer(gameId);
            client.waiteForStart();
        }).start();


    }

    static void sayWelcome(int gameId){
        System.out.println("welcome to gameid " + gameId);
        //add needs
    }

    public void gotoGameScreen(Client client){

        FXMLLoader fxmlLoader = new FXMLLoader(JoinGameController.class.getResource("/com/example/newesmfamil2/gameScreen.fxml"));
        GameScreenController controller = new GameScreenController();
        try {
            fxmlLoader.setController(controller);
            controller.setClient(client);
            Parent root = fxmlLoader.load();
            rootPane.getChildren().setAll(root);
//            ((GameScreenController)fxmlLoader.getController()).setClient(client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
