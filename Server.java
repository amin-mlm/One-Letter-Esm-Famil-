package com.example.newesmfamil2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {

    private int port;
    private String hostName;
    private String gameName;
    private String password;

    private int numFields;
    private ArrayList<String> fields;
    private String gameMode;
    private int rounds;
    private int time;

    boolean isGettingClientEnough = false;

    private ArrayList<Socket> sockets = new ArrayList<>();
    private ArrayList<Scanner> scanners = new ArrayList<>();
    private ArrayList<PrintWriter> printWriters = new ArrayList<>();
    private ArrayList<String> clientsName = new ArrayList<>();

    public Server(int port, String password, ArrayList<String> fields, String hostName, String gameName, int rounds, String gameMode, int time) {
        this.fields = fields;
        if(fields!=null)
            this.numFields = fields.size();
        this.hostName = hostName;
        this.gameName = gameName;
        this.password = password;
        this.rounds = rounds;
        this.gameMode = gameMode;
        this.port = port;
        this.time = time;
    }

    public void startGettingClient() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!isGettingClientEnough) {

            Socket socket = null;
            Scanner scanner = null;
            PrintWriter printwriter = null;

            try {
                socket = serverSocket.accept();
                System.out.println("in Server "+socket);
                scanner = new Scanner(
                        new BufferedReader(
                                new InputStreamReader(
                                        socket.getInputStream())));
                printwriter = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(
                                        socket.getOutputStream())), true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            sockets.add(socket);
            scanners.add(scanner);
            printWriters.add(printwriter);

            exchangeInfo(scanner, printwriter);

        }
    }

    private void exchangeInfo(Scanner scanner, PrintWriter printWriter) {

        String name = scanner.nextLine();
        clientsName.add(name);

        System.out.println("client: new client: " + name);


        printWriter.println(numFields);
        for (String s: fields)
            printWriter.println(s);
        printWriter.println(gameMode);
        printWriter.println(rounds);
        printWriter.println(time);

    }



    public void startGame() {
        System.out.println("in server, playernames");
        for(String s :clientsName) System.out.println(s);

        //bring host from last to first of arrayLists
        Socket lastSocket = sockets.get(sockets.size()-1);
        sockets.remove(sockets.size()-1);
        sockets.add(0, lastSocket);

        Scanner lastScanner = scanners.get(scanners.size()-1);
        scanners.remove(scanners.size()-1);
        scanners.add(0, lastScanner);

        PrintWriter lastPrintWriter = printWriters.get(printWriters.size()-1);
        printWriters.remove(printWriters.size()-1);
        printWriters.add(0, lastPrintWriter);


        //set and send plan for specifying alphabets via clients
        int numPlayers = sockets.size();
        for(int i=0; i<numPlayers; i++){
            String plan = "";
            for(int j=0; j<rounds; j++){
                if(j%numPlayers==i)
                    plan += "1";
                else
                    plan += "0";
            }
            printWriters.get(i).println(plan);
        }


        sendNotif();
//
//        new Thread( ()->{
//            Platform.runLater( ()->{
//                ((CreateGameController)(GameModeController.fxmlLoader.getController())).gotoGameScreen(this);
//            });
//        }).start();

//        new Thread( ()->{
                                        //checkAnswers();
//        }).start();

    }

    private void sendNotif() {
        for (PrintWriter p: printWriters) {
            p.println("go to game");
        }
    }








    public String getHostName() {
        return hostName;
    }

    public String getGameName() {
        return gameName;
    }

    public int getRounds() {
        return rounds;
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<String> getFields() {
        return fields;
    }

    public int getPort() {
        return port;
    }
}

