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

    private long secondTime;



    @FXML
    private MFXTextField pkField;

    @FXML
    private MFXButton pressBtn;





    @FXML
    void initialize() {
        fieldPane.setOrientation(Orientation.HORIZONTAL);
        fieldPane.setAlignment(Pos.CENTER);
        fieldPane.setHgap(10);

        timeLabel.setVisible(true);
        timeLabel.setDisable(false);

        new Thread(()->{
            showTime();

        }).start();

//        for(String s : fieldsString){
//            MFXTextField textField = new MFXTextField();
//            textField.setFloatingText(s);
//            textField.setPrefHeight(60);
//            textField.setPrefWidth(120);
//
//            textFields.add(textField);
//            fieldPane.getChildren().add(textField);
//        }


        /*if(gameMode.equals("Game Is Finished When The Time Is Over")){
            System.out.println("game is timeyy");
            timeLabel.setVisible(true);
            timeLabel.setDisable(false);
            new Thread( ()->{
                    showTime();

            }).start();
        }*//*else{
            finishButton.setOnAction(actionEvent -> {
                System.out.println(fields.get(1).getText());
            });
        }*/

        System.out.println("continue after thread");

//        pressBtn.setOnAction(actionEvent -> {
//            System.out.println(textFields.get(1).getText());
//        });

    }

    private void showTime(){

            int wholeTimeInSecond = 5;

            long firstTime = (long) (System.nanoTime() / Math.pow(10, 9));
            System.out.println("first time " + firstTime);

            secondTime = firstTime;
            do {
                System.out.println("do show time started");

                Platform.runLater(()->{
                    timeLabel.setText((wholeTimeInSecond + firstTime - secondTime) / 60 + ":" + (wholeTimeInSecond + firstTime - secondTime) % 60);
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                secondTime = (long) (System.nanoTime() / Math.pow(10, 9));
                System.out.println("second time " + secondTime);



                System.out.println();
            } while (secondTime - firstTime <= wholeTimeInSecond);
            System.out.println("show time finish");
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

    }
}
