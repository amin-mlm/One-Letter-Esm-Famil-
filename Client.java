package com.example.newesmfamil2;

import javafx.application.Platform;

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
    private String plan;

    private Socket socket;
    private Scanner scanner;
    private PrintWriter printWriter;

    private GameScreenController gameScreenController;



    public Client(String name) {
        this.name = name;
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

    public String getPlan() {
        return plan;
    }

    public void setGameScreenController(GameScreenController gameScreenController) {
        this.gameScreenController = gameScreenController;
    }


    public char listenForAlphabet() {
        return scanner.nextLine().charAt(0);
    }
}

// add needs (if server is not started close window won't be completed )
