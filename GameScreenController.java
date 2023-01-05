package com.example.newesmfamil2;

import io.github.palexdev.materialfx.controls.MFXButton;

import java.util.ArrayList;

import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyListView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
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
    private MFXLegacyListView<Client> scoreBoardListView;

//    @FXML
//    private MFXLegacyTableView<String> resultTable;


    private Client client;

    private ArrayList<String> fieldsString = new ArrayList<>();

    private ArrayList<MFXTextField> textFields = new ArrayList<>();

    private ArrayList<VBox> reactionRadioButtons = new ArrayList<>();

    private int rounds;

    private String gameMode;

    private int time; //in second

    private int indexBetweenAllPlayers;

    private String plan;

    private int thisRound = 1;

    private char alphabet;

    private long secondTime;

    private int index;

    private boolean myTurnToDetermineAlphabet;

    private int sumScore = 0;


    @FXML
    void initialize() {


        prepareFieldPane();

        if (Integer.parseInt(plan.charAt(thisRound - 1) + "")==1)
            myTurnToDetermineAlphabet = true;
        else
            myTurnToDetermineAlphabet = false;

        if (myTurnToDetermineAlphabet) {   //it can be 0 or 1       because thisRound starts from 1
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
            for (int i = 0; i < textFields.size(); i++) {
                if(textFields.get(i).getText().equals("")){
                    //add needs
                    break;
                }
                if(i==fieldsString.size()-1)
                    client.sendFinishState();
            }
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
                    finishButton.setVisible(false);
                    finishButton.setDisable(true);
                    for (MFXTextField textField : textFields) {
                        textField.setDisable(true);
                    }
                });

                //sleep here, instead of in server
                //let server start to listen to all clients(222 server)
                try {
                    Thread.sleep(200); //fewer? how many? // can + 1000 for seeing disability of TextFs?
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < textFields.size(); i++) {
                    System.out.println("will sleep for 10 * " + indexBetweenAllPlayers);
                    try {
                        Thread.sleep((long)10 * indexBetweenAllPlayers);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ArrayList<String> othersAnswers = client.sendAnswerAndGetOthersAnswers(textFields.get(i).getText());

                    System.out.println("gameScreen, othersAnswer: " + othersAnswers);

                    int point = sendReactionAndGetPoint(othersAnswers, fieldsString.get(i));

                    sumScore += point;

                    String tempAnswer = textFields.get(i).getText();
                    textFields.get(i).setText(tempAnswer + ", " + point);

                    showTime(15);

                    // add needs
                }

                if (++thisRound <= rounds) {

                    prepareNextRound();
                    nextRound();

                }else{
                    fieldPane.setVisible(false);
                    fieldPane.setDisable(true);

                    alphabetLabel.setVisible(false);
                    alphabetLabel.setDisable(true);

                    scoreBoardListView.setVisible(true);
                    scoreBoardListView.setDisable(false);

                    client.setFinalScore(sumScore);

                    ObservableList<Client> clientsObservableList = FXCollections.observableArrayList();
                    for (int i = 0; i < client.getNumOfAllPlayers(); i++) {
                        String name = client.listenToClientNameForScoreBoard();
                        int score = client.listenToClientScoreForScoreBoard();

                        System.out.println("*** final scores i:" + i + ", score:" + score);
                        Client client = new Client(name, score);

                        clientsObservableList.add(client);
                    }
                    scoreBoardListView.setItems(clientsObservableList);
                    scoreBoardListView.setCellFactory(param -> new ClientCellController());

                    //add needs

//                    resultTable = new MFXLegacyTableView<>();
//                    TableColumn nameColumn = new TableColumn("Name");
//                    TableColumn scoreColumn = new TableColumn("Score");
//
//                    resultTable.getColumns().add
//
//                    if(finalState==1){
//                        stateLabel.setText("You Are The CHAMPION !!!" +
//                                "You Got  " + sumScore + "  !");
//                    }else if(finalState==2){
//                        stateLabel.setText("You Got  \" + sumScore + \"  !" +
//                                "You Are the 2'nd ");
//                    }
                }



            }
        }).start();

        makeTextFieldsEnable();

        if (gameMode.equals("Game Is Finished When The Time Is Over")) {
            System.out.println("game is timeyy");

            showTime(/*time*/30);

            System.out.println("time finished");

            if(myTurnToDetermineAlphabet)
                client.sendFinishState();


        } else if (gameMode.equals("Game Is Finished When A Player Finished")) {
            System.out.println("game is stopyy");
            finishButton.setVisible(true);
            finishButton.setDisable(false);

        }
    }

    private void prepareNextRound() {
        int thisRoundScore = client.listenToRoundScore();
        stateLabel.setVisible(true);
        stateLabel.setDisable(false);


        Platform.runLater(()->{
            alphabetLabel.setText(""); /*Go With ' " + alphabet + " '*/
            stateLabel.setText("You Got  " + thisRoundScore + "  In This Round !" +
                    "\nWaite For The Next Round");
        });
        fieldPane.setVisible(false);
        fieldPane.setDisable(true);


        for (int i = 0; i < textFields.size(); i++) {
            textFields.get(i).setText("");
        }

        System.out.println("-----(before)reactionRadioButtons.size() when removing: " + reactionRadioButtons.size());
        System.out.println("-----(before)reactionPane.size() when removing: " + reactionPane.getChildren().size());

        //remove previous round reaction radioButtons
        while (reactionRadioButtons.size()!=0) {
            Platform.runLater(()->{
                reactionPane.getChildren().remove(0);
            });
            reactionRadioButtons.remove(0);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("-----(after)reactionRadioButtons.size() when removing: " + reactionRadioButtons.size());
        System.out.println("-----(after)reactionPane.size() when removing: " + reactionPane.getChildren().size());


        showTime(10);

        stateLabel.setVisible(false);
        stateLabel.setDisable(true);

    }
    private void nextRound() {

        initialize();

    }


    private void makeTextFieldsEnable() {
        for (int i = 0; i < textFields.size(); i++) {
            textFields.get(i).setDisable(false);
        }
    }

    private int sendReactionAndGetPoint(ArrayList<String> othersAnswers, String category)  {
        fieldPane.setVisible(false);
        fieldPane.setDisable(true);

        reactionPane.setVisible(true);
        reactionPane.setDisable(false);

//        stateLabel.setVisible(true);
//        stateLabel.setDisable(false);

        Platform.runLater(()->{
            alphabetLabel.setText("Do You Consider These Answers To Be As A \"" + category + "\"" + " ?(Must Starts With " + alphabet + " )" +
                "\n(If None Is Chosen, \"Yes\" Will Be Automatically Ticked)");
        });


        ArrayList<ToggleGroup> toggleGroups = new ArrayList<>();
        ArrayList<RadioButton> radioButtonsYes = new ArrayList<>();
        ArrayList<RadioButton> radioButtonsNo = new ArrayList<>();
        for (int i = 0; i < othersAnswers.size(); i++) {
            VBox vBox = new VBox();

            Label answer = new Label(othersAnswers.get(i));
            ToggleGroup toggle = new ToggleGroup();
            MFXRadioButton radioButtonYes = new MFXRadioButton("Yes");
            radioButtonsYes.add(radioButtonYes);
            MFXRadioButton radioButtonNo = new MFXRadioButton("No");
            radioButtonsNo.add(radioButtonNo);

            radioButtonYes.setToggleGroup(toggle);
            radioButtonNo.setToggleGroup(toggle);
            toggleGroups.add(toggle);


            vBox.getChildren().addAll(answer, radioButtonYes, radioButtonNo);

            reactionRadioButtons.add(vBox);

            System.out.println("toggleGroups.size() is " + toggleGroups.size());
            Platform.runLater(()->{
                reactionPane.getChildren().add(vBox);
            });
        }

        showTime(7 * othersAnswers.size()/* + 10*/ /*add needs (just delete this)*/);


        ArrayList<String> reactions = new ArrayList<>();
        for (index = 0; index < toggleGroups.size(); index++) {
            radioButtonsNo.get(index).setDisable(true);
            radioButtonsYes.get(index).setDisable(true);

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

        try {
            Thread.sleep(1000); //"Yes" ticked automatically is visible on screen
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        fieldPane.setVisible(true);
        fieldPane.setDisable(false);

        reactionPane.setVisible(false);
        reactionPane.setDisable(true);

//        stateLabel.setVisible(false);
//        stateLabel.setDisable(true);

        try {
            Thread.sleep(10 * indexBetweenAllPlayers);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return client.sendReactionsAndGetPoint(reactions);

    }


    private void listenForAlphabet() {
        alphabet = Character.toUpperCase(client.listenForAlphabet()) ;

        Platform.runLater(() -> {
            alphabetLabel.setText("Go With ' " +alphabet + " '");
        });
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

        timeLabel.setVisible(false);
        timeLabel.setDisable(true);
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

    private void prepareFieldPane() {
        fieldPane.setVisible(true);
        fieldPane.setDisable(false);

        if(thisRound==1){
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

    }

    public void sendReactions(ArrayList<String> reactions) {
        client.sendReactionsAndGetPoint(reactions);
    }
}
