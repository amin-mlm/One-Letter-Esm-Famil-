package com.example.newesmfamil2;

import io.github.palexdev.materialfx.controls.*;

import java.io.*;
import java.util.ArrayList;

import io.github.palexdev.materialfx.controls.legacy.MFXLegacyTableView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;


public class CreateGameController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private MFXCheckbox animalCheck;

    @FXML
    private MFXCheckbox carCheck;

    @FXML
    private MFXCheckbox cityCheck;

    @FXML
    private MFXCheckbox clothsCheck;

    @FXML
    private MFXCheckbox countryCheck;

    @FXML
    private MFXCheckbox firstnameCheck;

    @FXML
    private MFXCheckbox flowerCheck;

    @FXML
    private MFXCheckbox foodCheck;

    @FXML
    private MFXCheckbox fruitCheck;

    @FXML
    private MFXCheckbox inanimateCheck;

    @FXML
    private MFXCheckbox lastnameCheck;

    @FXML
    private MFXTextField hostNameField;

    @FXML
    private MFXTextField gameNameField;

    @FXML
    private MFXTextField passwordField;

    @FXML
    private MFXTextField roundsField;

    @FXML
    private MFXRadioButton stopy;

    @FXML
    private MFXRadioButton timey;

    @FXML
    private MFXSlider timeSlider;

    @FXML
    private MFXButton singleGameButton;

    @FXML
    private MFXButton startGettingClientButton;

    @FXML
    private MFXButton startGameButton;

    @FXML
    private MFXLegacyTableView<Client> playerNameTableView = new MFXLegacyTableView<>();

    ObservableList<Client> playerNameList ;

    public Server server;

    static DatabaseHandler databaseHandler = new DatabaseHandler();

    private boolean isGameCreated = false; //will be true when "startGettingClientButton" is pressed;

    @FXML
    void initialize() {

        ToggleGroup modeToggle = new ToggleGroup();
        stopy.setToggleGroup(modeToggle);
        timey.setToggleGroup(modeToggle);

        ArrayList<String> fields = new ArrayList<>();



        timey.setOnAction(actionEvent -> {
            timeSlider.setDisable(false);
        });
        stopy.setOnAction(actionEvent -> {
            timeSlider.setDisable(true);
        });

        //also, eventFilter can be implemented to consume event in some situations
        startGettingClientButton.setOnAction(actionEvent -> {
            isGameCreated = true;

            createTableView();

            if (firstnameCheck.isSelected()) fields.add(firstnameCheck.getText());
            if (lastnameCheck.isSelected()) fields.add(lastnameCheck.getText());
            if (cityCheck.isSelected()) fields.add(cityCheck.getText());
            if (countryCheck.isSelected()) fields.add(countryCheck.getText());
            if (clothsCheck.isSelected()) fields.add(clothsCheck.getText());
            if (animalCheck.isSelected()) fields.add(animalCheck.getText());
            if (foodCheck.isSelected()) fields.add(foodCheck.getText());
            if (flowerCheck.isSelected()) fields.add(flowerCheck.getText());
            if (carCheck.isSelected()) fields.add(carCheck.getText());
            if (fruitCheck.isSelected()) fields.add(fruitCheck.getText());
            if (inanimateCheck.isSelected()) fields.add(inanimateCheck.getText());

            String hostName = hostNameField.getText();
            String gameName = gameNameField.getText();
            String password = passwordField.getText();
            String mode = ((RadioButton)(modeToggle.getSelectedToggle())).getText();
            int rounds = 0;
            try{
                rounds = Integer.parseInt(roundsField.getText());
            }catch(NumberFormatException e){
                // add needs
            }
            int time = 0;
            if(timey.isSelected())
                time = (int)timeSlider.getValue() * 60; // add needs //time is set in seconds

            server = ServerFactory.createServer(fields, hostName, gameName, password, rounds, mode, time);
            server.setCreateGameController(this);
            new Thread( ()-> {
                server.startAcceptingClient();
            }).start();

            System.out.println(fields  + ", gameID = " + server.getPort() + ", hostName: "+ hostName  + ", gameName = " +  gameName  + ", pass: " + password  + ", " + mode  + ", time: " +  time);

        });

        //remove created game from database if the window closes
        Main.mainStage.setOnCloseRequest(windowEvent -> {
            System.out.println("/////Im closingCreateGame.....");
            if(server!=null){
                databaseHandler.removeServer(server.getPort());
                server.closeServerSocket();
                server.closeSockets();
            }
        });



        //enough button
        //also, eventFilter can be implemented to consume event in some situations
        startGameButton.setOnAction(actionEvent -> {
            server.isGettingClientEnough = true;
            Client client = new Client(hostNameField.getText());

            new Thread( ()->{
                client.joinToServer(server.getPort());
                client.waiteForStart();
            }).start();

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            new Thread( ()->{ //can be without thread? yes I think (no) I think
                server.startGame();
            }).start();

        });

    }

    private void createTableView() {
        playerNameTableView.setVisible(true);
        playerNameTableView.setDisable(false);

        playerNameList = FXCollections.observableArrayList();

        TableColumn<Client, String> nameCol = new TableColumn<>("Player Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setMinWidth(200);
        playerNameTableView.getColumns().add(nameCol);

        playerNameTableView.setItems(playerNameList);
    }


    public void addPlayerToBoard(String playerName) {
        System.out.println("added " + playerName);
        playerNameList.add(new Client(playerName));
    }

    public void gotoGameScreen(Client client){
//
//        new Thread(()->{
//            Platform.runLater(()->{
//                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/newesmfamil2/gameScreen.fxml"));
//                try {
//                    Parent root = fxmlLoader.load();
//                    rootPane.getChildren().setAll(root);
//        //            ((GameScreenController)fxmlLoader.getController()).setClient(client);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            });
//        }).start();

//        new Thread(()->{
            Platform.runLater(()->{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/newesmfamil2/gameScreen.fxml"));
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


            });
//        }).start();

    }
}
