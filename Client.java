package com.example.newesmfamil2;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    String name;

    private ArrayList<String> fields = new ArrayList<>();
    private String gameMode;
    private int rounds;
    private int time;
    private int indexBetweenAllPlayers;
    private String plan;

    private int numOfAllPlayers;

    private Socket socket;
    private Scanner scanner;
    private PrintWriter printWriter;

    private int finalScore;

    private GameScreenController gameScreenController;



    public Client(String name) {
        this.name = name;
    }

    public Client(String name, int finalScore) {
        this.name = name;
        this.finalScore = finalScore;
    }

    public void joinToServer(int gameId) {
        try {
            socket = new Socket(ServerFactory.MAIN_HOST, gameId);
            System.out.println("in client "+socket);
            scanner = new Scanner(
                    new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream())));
            printWriter = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream())), true);


            exchangeInfo();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exchangeInfo() {
        printWriter.println(name);

        int numField = scanner.nextInt();
        scanner.nextLine();
        for(int i=0; i<numField; i++){
            fields.add(scanner.nextLine());
        }
        gameMode = scanner.nextLine();
        rounds = scanner.nextInt();
        time = scanner.nextInt();
        scanner.nextLine();

    }

    public void waiteForStart() {
        System.out.println("client listening for plan...");
        plan = scanner.nextLine();
        numOfAllPlayers = Integer.parseInt(scanner.nextLine());
        indexBetweenAllPlayers = Integer.parseInt(scanner.nextLine());
        System.out.println(plan);
        System.out.println("client listening for message...");
        String message = scanner.nextLine();
        if(message.equals("go to game")){
            System.out.println("go to game heard YESSSSSSSSSSSSSS");

//            new Thread( ()->{
//                Platform.runLater( ()->{
//                    if (GameModeController.fxmlLoader.getLocation().toString().equals("/com/example/newesmfamil2/createGame.fxml"))
                    if((GameModeController.fxmlLoader.getController()) instanceof CreateGameController)
                        ((CreateGameController)GameModeController.fxmlLoader.getController()).gotoGameScreen(this);
                    else
                        ((JoinGameController)GameModeController.fxmlLoader.getController()).gotoGameScreen(this);
//                });
//            }).start();

            // write s.t to play for game
        }
    }

    public int sendAlphabet(String alphabetChar) {
        printWriter.println(alphabetChar);
        return Integer.parseInt(scanner.nextLine());
    }






    public char listenForAlphabet() {
        return scanner.nextLine().charAt(0);
    }

    public String listenToSendAnswerMessage() {
        String message = scanner.nextLine();
        System.out.println("message in client, " + message);
        if(message.equals("Send Your Answers")){
            System.out.println("client, I Will Send The Answer Now");
            printWriter.println("I Will Send The Answer Now");
        }
        return message;
    }

    public void sendFinishState() {
        printWriter.println("I Finish This Round");
    }

    public ArrayList<String> sendAnswerAndGetOthersAnswers(String answer) {
        printWriter.println(answer);
        ArrayList<String> othersAnswers = new ArrayList<>();
        for (int i = 0; i < numOfAllPlayers - 1; i++) {
            String otherAnswer = scanner.nextLine();
            System.out.println("gathered answer " + otherAnswer + " where i is " + i + " and numPL is " + numOfAllPlayers);
            othersAnswers.add(otherAnswer);
        }
        System.out.println(othersAnswers + " returned from client to gameScreen");
        return othersAnswers;
    }


    public int sendReactionsAndGetPoint(ArrayList<String> reactions) {
        for (int i = 0; i < reactions.size(); i++) {
            printWriter.println(reactions.get(i));
        }
        return Integer.parseInt(scanner.nextLine());
    }


    public String getName() {
        return name;
    }

    public ArrayList<String> getFields() {
        return fields;
    }

    public String getGameMode() {
        return gameMode;
    }

    public int getRounds() {
        return rounds;
    }

    public int getTime() {
        return time;
    }

    public int getIndexBetweenAllPlayers() {
        return indexBetweenAllPlayers;
    }

    public String getPlan() {
        return plan;
    }

    public int getNumOfAllPlayers() {
        return numOfAllPlayers;
    }

    public int getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(int finalScore) {
        this.finalScore = finalScore;
    }

    public void setGameScreenController(GameScreenController gameScreenController) {
        this.gameScreenController = gameScreenController;
    }

    public int listenToRoundScore() {
        return Integer.parseInt(scanner.nextLine());
    }

    public String listenToClientNameForScoreBoard() {
        return scanner.nextLine();
    }

    public int listenToClientScoreForScoreBoard() {
        return Integer.parseInt(scanner.nextLine());
    }
}

// add needs (if server is not started close window won't be completed )
