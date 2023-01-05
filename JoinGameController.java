package com.example.newesmfamil2;

import com.example.newesmfamil2.animaition.Fade;
import com.example.newesmfamil2.animaition.Shaker;
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

    private int gamePort;

    private String gameName;

    @FXML
    void initialize() {
        gamePort =-1;

//        serversObservableList.clear();
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

    public void joinToServer(int gamePort, String gameName, boolean isPasswordCorrect){
        clientNameField.setStyle("-fx-border-color: ");

        if(this.gamePort!=-1){ //game is already chosen
            gameStateLabel.setText("You Have Entered To " + this.gameName + "'s Game" +
                    "\nwait for others to join...");
            new Fade(gameStateLabel).fadeIn();

            return;
        }
        if(clientNameField.getText().equals("") || clientNameField.getText().charAt(0)==' '){
            gameStateLabel.setText("Enter Your Name");
            if(!clientNameField.getText().equals(""))
                gameStateLabel.setText("Enter A Valid Name(Shouldn't Start With Space)");
            new Fade(gameStateLabel).fadeIn();

            clientNameField.setStyle("-fx-border-color: red");
            new Shaker(clientNameField).shake();

            return;
        }
        if(!databaseHandler.isServerExist(gamePort)){
            gameStateLabel.setText(":(\nThe Game Doesn't Exist Anymore");
            new Fade(gameStateLabel).fadeIn();

            clientNameField.setDisable(false);

            initialize();
            return;
        }
        if(!isPasswordCorrect){
            gameStateLabel.setText("Wrong Password!");
            new Fade(gameStateLabel).fadeIn();

            return;
        }

        clientNameField.setDisable(true);
        this.gamePort = gamePort;
        this.gameName = gameName;

        sayWelcome(gameName);

        Client client = new Client(clientNameField.getText());

        new Thread( ()->{
            client.setJoinGameController(this);
            client.joinToServer(gamePort);
            int result = client.waitForStart();
            if(result == -1){ //host left the game
                client.closeSocket();

                Platform.runLater(()->{
                    gameStateLabel.setText(":(\nHost Left The Game, Choose Another Game");
                    clientNameField.setDisable(false);
                    new Fade(gameStateLabel).fadeIn();
                    initialize();
                });
            }
        }).start();


    }

    private void sayWelcome(String gameName){
        gameStateLabel.setText("Welcome To " + gameName + "'s Game" +
                "\nwait for others to join...");
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


//    public void notifHostLeftGame() {
//        Platform.runLater(()->{
//            gameStateLabel.setText(":(");
//        });
//    }
}
