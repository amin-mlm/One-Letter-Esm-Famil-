package com.example.newesmfamil2;

import io.github.palexdev.materialfx.controls.*;

import java.io.*;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

import javax.swing.plaf.TableHeaderUI;

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
    private MFXButton singleGameButton;

    @FXML
    private MFXTextField roundsField;

    @FXML
    private MFXRadioButton stopy;

    @FXML
    private MFXRadioButton timey;

    @FXML
    private MFXSlider timeSlider;

    @FXML
    private MFXButton startGettingClientButton;

    @FXML
    private MFXButton startGameButton;


    public Server server;

    static DatabaseHandler databaseHandler = new DatabaseHandler();


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
                time = (int)timeSlider.getValue();

            server = ServerFactory.createServer(fields, hostName, gameName, password, rounds, mode, time);

            new Thread( ()-> {
                server.startGettingClient();
            }).start();



            //list view of players

            System.out.println(fields  + ", gameID = " + server.getPort() + ", hostName: "+ hostName  + ", gameName = " +  gameName  + ", pass: " + password  + ", " + mode  + ", time: " +  time);

        });



        //enough button
        //also, eventFilter can be implemented to consume event in some situations
        startGameButton.setOnAction(actionEvent -> {

            server.isGettingClientEnough = true;
            Client client = new Client(gameNameField.getText());

            new Thread( ()->{
                client.joinToServer(server.getPort());
                Platform.runLater(()->{
                    client.waiteForStart();
                });
            }).start();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            new Thread( ()->{ //can be without thread? yes I think
                server.startGame();
            }).start();

        });

    }


    public void gotoGameScreen(Client client){

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


    }


}
