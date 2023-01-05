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
    private Label timeLabel;

    private Client client;

    private ArrayList<String> fieldsString = new ArrayList<>();

    private ArrayList<MFXTextField> textFields = new ArrayList<>();

    private int rounds;

    private String gameMode;

    private int time;

    private String plan;

    private int thisRound = 0;

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
    }

    private void setGameInfo(){
        fieldsString = client.getFields();
        rounds = client.getRounds();
        gameMode = client.getGameMode();
        time = client.getTime();
        plan = client.getPlan();

    }
}
