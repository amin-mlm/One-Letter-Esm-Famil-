package com.example.newesmfamil2;

import io.github.palexdev.materialfx.controls.MFXButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyListView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
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

    @FXML
    private MFXButton newGameButton;

    @FXML
    private MFXButton exitButton;

    static Client client;

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

        newGameButton.setOnAction(actionEvent -> {
            closeCurrentGame();
            newGame();
        });

        exitButton.setOnAction(actionEvent -> {
            closeCurrentGame();
            exitButton.getScene().getWindow().hide();
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

                    String answer = removeSpaceFromAnswer(textFields.get(i).getText());
                    ArrayList<String> othersAnswers = client.sendAnswerAndGetOthersAnswers(answer);

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
                    prepareNextRound();

                    alphabetLabel.setVisible(false);
                    alphabetLabel.setDisable(true);

                    scoreBoardListView.setVisible(true);
                    scoreBoardListView.setDisable(false);

                    client.setFinalScore(sumScore);

                    int lastRank=1;
                    ObservableList<Client> clientsObservableList = FXCollections.observableArrayList();
                    for (int i = 0; i < client.getNumOfAllPlayers(); i++) {
                        String name = client.listenToClientNameForScoreBoard();
                        int finalScore = client.listenToClientScoreForScoreBoard();

                        System.out.println("*** final scores i:" + i + ", score:" + finalScore);
                        int rank;
                        if(i==0)
                            rank = 1;
                        else{
                            if(finalScore==clientsObservableList.get(i-1).getFinalScore()){
                                rank = lastRank;
                            }else
                                rank = ++lastRank;
                        }

                        Client client = new Client(name, finalScore, rank);

                        clientsObservableList.add(client);
                    }
                    scoreBoardListView.setItems(clientsObservableList);
                    scoreBoardListView.setCellFactory(param -> new ClientCellController());



                    //add needs

                    // next Game



                    newGameButton.setVisible(true);
                    newGameButton.setDisable(false);

                    exitButton.setVisible(true);
                    exitButton.setDisable(false);


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

    private void newGame() {
        scoreBoardListView.setVisible(false);
        scoreBoardListView.setDisable(true);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("com/example/newesmfamil2/gameMode.fxml"));
        try {
            rootPane.getChildren().setAll((Pane)(fxmlLoader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeCurrentGame() {
        try {
            client.closeGame();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String removeSpaceFromAnswer(String primaryAnswer) {
        if(primaryAnswer.equals("")) return primaryAnswer;

        var scanner = new Scanner(primaryAnswer);
        String answer = "";
        while (scanner.hasNext()) {
            answer += scanner.next() + " ";
        }
        return answer.substring(0, answer.length()-1); //remove last space
    }

    private void prepareNextRound() {
        int thisRoundScore = client.listenToRoundScore();
        stateLabel.setVisible(true);
        stateLabel.setDisable(false);


        Platform.runLater(()->{
            alphabetLabel.setText("");
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

        Platform.runLater(()->{
            alphabetLabel.setText("Do You Consider These Answers To Be As A \"" + category + "\"" + " ?(Must Starts With " + alphabet + " )" +
                "\n(If None Is Chosen, \"Yes\" Will Be Automatically Ticked)");
        });


        ArrayList<ToggleGroup> toggleGroups = new ArrayList<>();
        ArrayList<RadioButton> radioButtonsYes = new ArrayList<>();
        ArrayList<RadioButton> radioButtonsNo = new ArrayList<>();

        int[] similarAnswers = new int[othersAnswers.size()]; //e.g. index i in othersAnswers is similar to similarAnswers[i] so we won't create radioButton for othersAnswers.get(i)
        Arrays.fill(similarAnswers, -1);

        for (int i = 0; i < othersAnswers.size(); i++) {
            int state = isAnswerSimilarToOthers(othersAnswers.get(i), i, othersAnswers);
            if(othersAnswers.get(i).equals("") || !(othersAnswers.get(i).charAt(0)+"").equalsIgnoreCase(alphabet+"")){
                System.out.println("++++++++++++++++ emptyIf i " + i);
                continue;
            }
            else if(state!=-1){
                similarAnswers[i] = state;
                System.out.println("++++++++++++++++ similarIf i " + i);
                continue;
            }

            System.out.println("++++++++++++++++ vbox i " + i);

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
        index = 0;
        for (int i=0; i < othersAnswers.size(); i++) {
            if(othersAnswers.get(i).equals("") || !(othersAnswers.get(i).charAt(0)+"").equalsIgnoreCase(alphabet+"")) {
                reactions.add("Negative");
//                System.out.println("    for answer " + othersAnswers.get(i) + ", empty negative");
                continue;
            }else if(similarAnswers[i]!=-1){
                reactions.add(reactions.get(similarAnswers[i]));
//                System.out.println("    for answer " + othersAnswers.get(i) + ", similar ot i " + similarAnswers[i]);
                continue;
            }


//            System.out.println("    for answer " + othersAnswers.get(i) + ", vbox");


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
            index++;
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

        try {
            Thread.sleep(10 * indexBetweenAllPlayers);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return client.sendReactionsAndGetPoint(reactions);

    }

    private int isAnswerSimilarToOthers(String answer, int index, ArrayList<String> othersAnswers) {
        for (int i = 0; i < othersAnswers.size(); i++) {
            if(i==index)
                break;
            if(othersAnswers.get(i).equals(answer))
                return i;
        }
        return -1;
    }


    private void listenForAlphabet() {
        Platform.runLater(()->{
            alphabetLabel.setText("Let Game Alphabet Be Determined ..."); //add needs
        });

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
