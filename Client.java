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

    private Socket socket;
    private Scanner scanner;
    private PrintWriter printWriter;

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
        System.out.println("client listening...");
        String message = scanner.nextLine();
        if(message.equals("go to game")){
            System.out.println("go to game heard YESSSSSSSSSSSSSS");

            new Thread( ()->{
                Platform.runLater( ()->{
//                    if (GameModeController.fxmlLoader.getLocation().toString().equals("/com/example/newesmfamil2/createGame.fxml"))
                    if((GameModeController.fxmlLoader.getController()) instanceof CreateGameController)
                        ((CreateGameController)(GameModeController.fxmlLoader.getController())).gotoGameScreen();
                    else
                        ((JoinGameController)(GameModeController.fxmlLoader.getController())).gotoGameScreen();
                });
            }).start();

            // write s.t to play for game
        }
    }
}
