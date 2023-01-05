package com.example.newesmfamil2;

import io.github.palexdev.materialfx.controls.MFXButton;

import java.util.ArrayList;

import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyTableView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class GameScreenController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private FlowPane fieldPane;

    @FXML
    private FlowPane reactionPane;

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

    @FXML
    private Label stateLabel;

    @FXML
    private MFXLegacyTableView<String> resultTable;


    private Client client;

    private ArrayList<String> fieldsString = new ArrayList<>();

    private ArrayList<MFXTextField> textFields = new ArrayList<>();

    private int rounds;

    private String gameMode;

    private int time;

    private int indexBetweenAllPlayers;

    private String plan;

    private int thisRound = 1;

    private char alphabet;

    private long secondTime;

    private int index;


    @FXML
    void initialize() {

        if(thisRound==1){
            addTextFieldsToPane();
        }

        if (Integer.parseInt(plan.charAt(thisRound - 1) + "") == 1) {   //it can be 0 or 1       because thisRound starts from 1
            System.out.println("TURN ME");
            alphabetField.setDisable(false);
            alphabetField.setVisible(true);
            alphabetButton.setDisable(false);
            alphabetButton.setVisible(true);
        } else {
            System.out.println("NOT TURN ME");
            new Thread(() -> {
                listenForAlphabet();
                startGame();
            }).start();
        }


        alphabetButton.setOnAction(actionEvent -> {
            new Thread(() -> {
                if(sendAlphabet()==0){
                    listenForAlphabet();
                    startGame();
                }
            }).start();
        });

        finishButton.setOnAction(actionEvent -> {
            client.sendFinishState();
        });
    }

    private int sendAlphabet() {

        String alphabetString = alphabetField.getText();
        char alphabetChar = alphabetString.charAt(0);

        if (alphabetString.length() != 1) {
            // add needs
            return -1; //code for invalid char error
        }
        if (!((alphabetChar >= 65 && alphabetChar <= 90) || (alphabetChar >= 97 && alphabetChar <= 122))) {
            // add needs
            return -1; //code for invalid char error
        } else {
            int result = client.sendAlphabet(alphabetString);
            if (result == 0) {
                alphabetField.setDisable(true);
                alphabetField.setVisible(false);
                alphabetButton.setDisable(true);
                alphabetButton.setVisible(false);
                return 0; //code for all ok
            } else if (result == -1) {
                System.out.println("gameScreen: repeated alpha try again");
                // add needs for being repeated alphabet
                return -1; //code for invalid char error
            }
        }


        return 0;
    }

    private void startGame() {
        new Thread(() -> {
            String message = client.listenToSendAnswerMessage();
            if (message.equals("Send Your Answers")) {
                Platform.runLater(() -> {
                    for (int i = 0; i < textFields.size(); i++) {
                        textFields.get(i).setDisable(true);
                    }
                });

                //sleep here, instead of in server
                //let server start to listen to all clients(212 server)
                try {
                    Thread.sleep(200); //fewer? how many?
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < fieldsString.size(); i++) {
                    System.out.println("will sleep for 10 * " + indexBetweenAllPlayers);
                    try {
                        Thread.sleep(10 * indexBetweenAllPlayers);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ArrayList<String> othersAnswers = client.sendAnswerAndGetOthersAnswers(textFields.get(i).getText());

                    System.out.println("gameScreen, othersAnswer: " + othersAnswers);
                    int point = sendReactionAndGetPoint(othersAnswers, fieldsString.get(i));

                    String tempAnswer = textFields.get(i).getText();
                    textFields.get(i).setText(tempAnswer + ", " + point);

                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    // add needs


                }

                if(++thisRound<=rounds){

                    int thisRoundScore = client.listenToRoundScore();
                    Platform.runLater(()->{
                        stateLabel.setText("You Got  " + thisRoundScore + "  In This Round !" +
                                "\nWaite For The Next Round");
                    });


                    nextRound();

                }/*else{
//                    int sumScore = client.listenToSumScore();
//                    int finalState = client.listenToFinalState();
                    ArrayList<String> names = new ArrayList<>();
                    ArrayList<Integer> scores = new ArrayList<>();
                    for (int i = 0; i < client.getNumOfAllPlayers(); i++) {
                        names.add(client.listenToClientName());
                        scores.add(client.listenToClientScore());
                    }
                    resultTable = new MFXLegacyTableView<>();
                    TableColumn nameColumn = new TableColumn("Name");
                    TableColumn scoreColumn = new TableColumn("Score");

                    resultTable.getColumns().ad


                    if(finalState==1){
                        stateLabel.setText("You Are The CHAMPION !!!" +
                                "You Got  " + sumScore + "  !");
                    }else if(finalState==2){
                        stateLabel.setText("You Got  \" + sumScore + \"  !" +
                                "You Are the 2'nd ");
                    }
                }*/



            }
        }).start();

        makeTextFieldsEnable();

        if (gameMode.equals("Game Is Finished When The Time Is Over")) {
            System.out.println("game is timeyy");

            showTime(time * 60);

            System.out.println("time finished");

            client.sendFinishState();


        } else if (gameMode.equals("Game Is Finished When A Player Finished")) {
            System.out.println("game is stopyy");
            finishButton.setVisible(true);
            finishButton.setDisable(false);


        }
    }

    private void makeTextFieldsEnable() {
        for (int i = 0; i < textFields.size(); i++) {
            textFields.get(i).setDisable(false);
        }
    }

    private int sendReactionAndGetPoint(ArrayList<String> othersAnswers, String category) {
        finishButton.setVisible(false);
        finishButton.setDisable(true);

        fieldPane.setVisible(false);
        fieldPane.setDisable(true);

        reactionPane.setVisible(true);
        reactionPane.setDisable(false);

        Platform.runLater(()->{
            alphabetLabel.setText("Do You Consider These Answers To Be As A \"" + category + "\"" + " ?" +
                "\n(If None Is Chosen, \"Yes\" Will Be Automatically Ticked)");
        });


        ArrayList<ToggleGroup> toggleGroups = new ArrayList<>();
        ArrayList<RadioButton> radioButtonsYes = new ArrayList<>();
        for (int i = 0; i < othersAnswers.size(); i++) {
            VBox vBox = new VBox();

            Label answer = new Label(othersAnswers.get(i));
            ToggleGroup toggle = new ToggleGroup();
            MFXRadioButton radioButtonYes = new MFXRadioButton("Yes");
            radioButtonsYes.add(radioButtonYes);
            MFXRadioButton radioButtonNo = new MFXRadioButton("No");

            radioButtonYes.setToggleGroup(toggle);
            radioButtonNo.setToggleGroup(toggle);
            toggleGroups.add(toggle);

            vBox.getChildren().addAll(answer, radioButtonYes, radioButtonNo);

            System.out.println("toggleGroups.size() is " + toggleGroups.size());
            Platform.runLater(()->{
                reactionPane.getChildren().add(vBox);
            });
        }

         Thread thread = new Thread(()->{ //can without thread?
            showTime(7 * othersAnswers.size() + 10 /*add needs (just delete this)*/);
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArrayList<String> reactions = new ArrayList<>();
        for (index = 0; index < toggleGroups.size(); index++) {
            if(toggleGroups.get(index).getSelectedToggle()==null){
                Platform.runLater(()->{
                    toggleGroups.get(index).selectToggle(radioButtonsYes.get(index));
                });
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(toggleGroups.get(index).getSelectedToggle().equals(radioButtonsYes.get(index))){
                reactions.add("Positive");
            }else{
                reactions.add("Negative");
            }
        }

        finishButton.setVisible(true);
        finishButton.setDisable(false);

        fieldPane.setVisible(true);
        fieldPane.setDisable(false);

        reactionPane.setVisible(false);
        reactionPane.setDisable(true);



        return client.sendReactionsAndGetPoint(reactions);

    }


    private void listenForAlphabet() {
        alphabet = client.listenForAlphabet();
        Platform.runLater(() -> {
            alphabetLabel.setText("Go With '" + Character.toUpperCase(alphabet) + "'");
        });
    }


    private void nextRound() {
        fieldPane.setVisible(false);
        showTime(10);
        stateLabel.setVisible(false);
        fieldPane.setVisible(true);
        for (int i = 0; i < textFields.size(); i++) {
            textFields.get(i).setText("");
        }

        initialize();


    }


    private void showTime(int time) {
        timeLabel.setVisible(true);
        timeLabel.setDisable(false);

        long firstTime = (long) (System.nanoTime() / Math.pow(10, 9));

        secondTime = firstTime;
        do {
            Platform.runLater(() -> {
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

    private void setGameInfo() {
        fieldsString = client.getFields();
        rounds = client.getRounds();
        gameMode = client.getGameMode();
        time = client.getTime();
        indexBetweenAllPlayers = client.getIndexBetweenAllPlayers();
        plan = client.getPlan();
    }

    private void addTextFieldsToPane() {

        fieldPane.setOrientation(Orientation.HORIZONTAL);
        fieldPane.setAlignment(Pos.CENTER);
        fieldPane.setHgap(10);
        fieldPane.setVgap(10);

        for (String s : fieldsString) {
            MFXTextField textField = new MFXTextField();
            textField.setDisable(true);
            textField.setFloatingText(s);
            textField.setPrefHeight(60);
            textField.setPrefWidth(120);

            textFields.add(textField);
            fieldPane.getChildren().add(textField);
        }

    }

    public void sendReactions(ArrayList<String> reactions) {
        client.sendReactionsAndGetPoint(reactions);
    }
}
