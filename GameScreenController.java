package com.example.newesmfamil2;

import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

public class GameScreenController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private FlowPane fieldPane;

    @FXML
    private MFXButton finishButton;

    @FXML
    private MFXTextField alphabetField;

    @FXML
    private MFXButton alphabetButton;

    @FXML
    private Label alphabetLabel;

    @FXML
    private Label timeLabel;

    private Client client;

    private ArrayList<String> fieldsString = new ArrayList<>();

    private ArrayList<MFXTextField> textFields = new ArrayList<>();

    private int rounds;

    private String gameMode;

    private int time;

    private String plan;

    private int thisRound = 1;

    private char alphabet;

    private long secondTime;



    @FXML
    void initialize() {

        doPrimaryTasksInPane();

        if(Integer.parseInt(plan.charAt(thisRound - 1) + "")==1){   //it can be 0 or 1       because thisRound starts from 1
            System.out.println("TURN ME");
            alphabetField.setDisable(false);
            alphabetField.setVisible(true);
            alphabetButton.setDisable(false);
            alphabetButton.setVisible(true);
        }else{
            System.out.println("NOT TUEN ME");
            new Thread(()->{
                listenForAlphabet();
                startGame();
            }).start();
        }


        alphabetButton.setOnAction(actionEvent -> {
            new Thread( ()->{
                sendAlphabet();
            }).start();
        });

        finishButton.setOnAction(actionEvent ->{
            client.sendFinishState();
        });
    }

    private void sendAlphabet() {

        String alphabetString = alphabetField.getText();
        char alphabetChar = alphabetString.charAt(0);

        if(alphabetString.length()!=1){
            // add needs
        }
        if( !((alphabetChar>=65 && alphabetChar<=90) || (alphabetChar>=97 && alphabetChar<=122))  ){
            // add needs
        }
        else {
            int result = client.sendAlphabet(alphabetString);
            if(result==0){
                listenForAlphabet();
                alphabetField.setDisable(true);
                alphabetField.setVisible(false);
                alphabetButton.setDisable(true);
                alphabetButton.setVisible(false);
                startGame();
            }
            else if(result==-1){
                System.out.println("gameScreen: repeated alpha try again");
                // add needs for being repeated alphabet
            }
        }


    }

    private void startGame() {
        new Thread(()->{
           String message = client.listenToSendAnswerMessage();
           if(message.equals("Send Your Answers")){
               Platform.runLater(()->{
                   for (int i = 0; i < textFields.size(); i++) {
                       textFields.get(i).setDisable(true);
                   }
               });

               //sleep here, instead of in server

               for (int i = 0; i < fieldsString.size(); i++) {
                   int point = client.sendAnswerAndGetPoint(textFields.get(i).getText());
                   String tempAnswer = textFields.get(i).getText();
                   textFields.get(i).setText(tempAnswer + ", " + point);
                   // add needs
               }
           }
        }).start();

        if(gameMode.equals("Game Is Finished When The Time Is Over")){
            System.out.println("game is timeyy");
            timeLabel.setVisible(true);
            timeLabel.setDisable(false);

            showTime();
            System.out.println("time finished");

            client.sendFinishState();



        }else if(gameMode.equals("Game Is Finished When A Player Finished")){
            System.out.println("game is stopyy");
            finishButton.setVisible(true);
            finishButton.setDisable(false);


        }
    }


    private void listenForAlphabet() {
        alphabet = client.listenForAlphabet();
        Platform.runLater(()->{
            alphabetLabel.setText("Go With '" + Character.toUpperCase(alphabet) + "'");
        });
    }



    private void nextRound(){

    }


    private void showTime(){
        long firstTime = (long) (System.nanoTime() / Math.pow(10, 9));

        secondTime = firstTime;
        do {
            Platform.runLater(()->{
                timeLabel.setText((time + firstTime - secondTime) / 60 + ":" + (time + firstTime - secondTime) % 60);
            });

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            secondTime = (long) (System.nanoTime() / Math.pow(10, 9));
        } while (secondTime - firstTime <= time);

    }

    public void setClient(Client client) {
        this.client = client;
        setGameInfo();
        client.setGameScreenController(this);
    }

    private void setGameInfo(){
        fieldsString = client.getFields();
        rounds = client.getRounds();
        gameMode = client.getGameMode();
        time = client.getTime();
        plan = client.getPlan();

    }




    private void doPrimaryTasksInPane() {
        fieldPane.setOrientation(Orientation.HORIZONTAL);
        fieldPane.setAlignment(Pos.CENTER);
        fieldPane.setHgap(10);
        fieldPane.setVgap(10);


        for(String s : fieldsString){
            MFXTextField textField = new MFXTextField();
            textField.setFloatingText(s);
            textField.setPrefHeight(60);
            textField.setPrefWidth(120);

            textFields.add(textField);
            fieldPane.getChildren().add(textField);
        }

    }
}





/*



package com.example.newesmfamil2;

import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

public class GameScreenController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private FlowPane fieldPane;

    @FXML
    private MFXButton finishButton;

    @FXML
    private MFXTextField alphabetField;

    @FXML
    private MFXButton alphabetButton;

    @FXML
    private Label timeLabel;

    private Client client;

    private ArrayList<String> fieldsString = new ArrayList<>();

    private ArrayList<MFXTextField> textFields = new ArrayList<>();

    private int rounds;

    private String gameMode;

    private int time;

    private String plan;

    private int thisRound = 1;

    private char alphabet;

    private long secondTime;



    @FXML
    void initialize() {
        fieldPane.setOrientation(Orientation.HORIZONTAL);
        fieldPane.setAlignment(Pos.CENTER);
        fieldPane.setHgap(10);
        fieldPane.setVgap(10);


        for(String s : fieldsString){
            MFXTextField textField = new MFXTextField();
            textField.setFloatingText(s);
            textField.setPrefHeight(60);
            textField.setPrefWidth(120);

            textFields.add(textField);
            fieldPane.getChildren().add(textField);
        }

        if(plan.charAt(thisRound - 1)==1){   //it can be 0 or 1       because thisRound starts from 1
            alphabetField.setDisable(false);
            alphabetField.setVisible(true);
            alphabetButton.setDisable(false);
            alphabetButton.setVisible(true);
        }else{
            listenForAlphabet();

        }

        if(gameMode.equals("Game Is Finished When The Time Is Over")){
            System.out.println("game is timeyy");
            timeLabel.setVisible(true);
            timeLabel.setDisable(false);
            new Thread( ()->{
                showTime();
            }).start();
        }else{
            finishButton.setVisible(true);
            finishButton.setDisable(false);

        }
        alphabetButton.setOnAction(actionEvent -> {
            sendAlphabet();
        });
    }

    private void sendAlphabet() {

        String alphabetString = alphabetField.getText();
        char alphabetChar = alphabetString.charAt(0);

        if(alphabetString.length()!=1){
            // add needs
        }
        if( !((alphabetChar>=65 && alphabetChar<=90) || (alphabetChar>=97 && alphabetChar<=122))  ){
            // add needs
        }
        else {
            int result = client.sendAlphabet(alphabetString);
            if(result==0){
                new Thread(()->{
                    //add needs for being correct
                    listenForAlphabet();

                }).start();
            }
            if(result==-1){
                System.out.println("gameScreen: repeated alpha try again");
                // add needs for being repeated alphabet
            }
        }


    }

    private void listenForAlphabet() {
        alphabet = client.listenForAlphabet();
    }

    private void nextRound(){

    }


    private void showTime(){
        long firstTime = (long) (System.nanoTime() / Math.pow(10, 9));

        secondTime = firstTime;
        do {
            Platform.runLater(()->{
                timeLabel.setText((time + firstTime - secondTime) / 60 + ":" + (time + firstTime - secondTime) % 60);
            });

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            secondTime = (long) (System.nanoTime() / Math.pow(10, 9));
        } while (secondTime - firstTime <= time);

        ///send answers

    }

    public void setClient(Client client) {
        this.client = client;
        setGameInfo();
        client.setGameScreenController(this);
    }

    private void setGameInfo(){
        fieldsString = client.getFields();
        rounds = client.getRounds();
        gameMode = client.getGameMode();
        time = client.getTime();
        plan = client.getPlan();

    }
}

 */