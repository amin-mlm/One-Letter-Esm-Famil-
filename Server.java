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

    private int thisRound = 1;
    private String serverPlan;
    private ArrayList<Character> usedAlphabets = new ArrayList<>();

    boolean isGettingClientEnough = false;
    boolean didClientsSendAnswers = false;


    private ArrayList<Socket> sockets = new ArrayList<>();
    private ArrayList<Scanner> scanners = new ArrayList<>();
    private ArrayList<PrintWriter> printWriters = new ArrayList<>();
    private ArrayList<String> clientsName = new ArrayList<>();

    public Server(int port, String password, ArrayList<String> fields, String hostName, String gameName, int rounds, String gameMode, int time) {
        this.fields = fields;
        if (fields != null) //we need to create some deficient
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
                System.out.println("in Server " + socket);
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
        for (String s : fields)
            printWriter.println(s);
        printWriter.println(gameMode);
        printWriter.println(rounds);
        printWriter.println(time);

    }


    public void startGame() {
        System.out.println("in server, playernames");
        for (String s : clientsName) System.out.println(s);

        //bring host from last to first of arrayLists
        Socket lastSocket = sockets.get(sockets.size() - 1);
        sockets.remove(sockets.size() - 1);
        sockets.add(0, lastSocket);

        Scanner lastScanner = scanners.get(scanners.size() - 1);
        scanners.remove(scanners.size() - 1);
        scanners.add(0, lastScanner);

        PrintWriter lastPrintWriter = printWriters.get(printWriters.size() - 1);
        printWriters.remove(printWriters.size() - 1);
        printWriters.add(0, lastPrintWriter);


        //set plan for server to get alphabet from clients
        //for instance,plan: 0123012 means client index 0 (in scanners list) should
        //determine the game alphabet in the first game,
        //client index 1 should determine in the second game and so on.
        int numPlayers = sockets.size();
        serverPlan = "";
        for (int i = 0; i < rounds; i++) {
            serverPlan += i % numPlayers;
        }

        //set and send the plan for determining alphabets via clients
        //for instance,plan: 0100010 means this client should determine the
        //game alphabet in the second and sixth game.
        for (int i = 0; i < numPlayers; i++) {
            String clientPlan = "";
            for (int j = 0; j < rounds; j++) {
                if (j % numPlayers == i)
                    clientPlan += "1";
                else
                    clientPlan += "0";
            }
            printWriters.get(i).println(clientPlan);
        }


        sendNotif();

        //ye go to game screen inja buddaaaaaaaaaaaaaaaaaa gozashtam too io.txt file

        new Thread(() -> { //can be without thread? yes I think
            determineAlphabet();
            waiteToFinishRoundAndCheckAnswers();
        }).start();

    }


    int index;

    private void waiteToFinishRoundAndCheckAnswers() {
        System.out.println("in server, scanner size:"+scanners.size());
        for (index = 0; index < scanners.size(); index++) {
            new Thread(() -> {
                int relatedIndex = index;
                String message = scanners.get(index).nextLine();
                System.out.println("message in server form client no." + relatedIndex + ", " + message);
                if (message.equals("I Finish This Round")) {
                    new Thread(()->{
                        scanners.get(relatedIndex).nextLine();
                    }).start();
                    //nothing
                    CollectAndCheckAnswers(index);
                } else if (message.equals("I Will Send The Answer Now")) {
                    //nothing
                }
            }).start();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void CollectAndCheckAnswers(int finisherIndex) { //index in 3 arrayLists(sockets, scanners, printWriters)
        if (!didClientsSendAnswers) {
            didClientsSendAnswers = true;

            new Thread(() -> {
                //get 1 field answer from all clients and
                // send back the points they gave for this field
                // and then go for next field and so on
                for (int i = 0; i < numFields; i++) {
                    ArrayList<String> answers = new ArrayList<>();
                    ArrayList<Integer> points = new ArrayList<>();

                    for (index = 0; index < scanners.size(); index++) {
                        new Thread(() -> {
                            String answer = scanners.get(index).nextLine();
                            answers.add(answer);
                        }).start();

                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    points = calculatePoints(answers, fields.get(i));

                    for (int j = 0; j < printWriters.size(); j++) {
                        printWriters.get(j).println(points.get(j) + "");
                    }


                }
            }).start();

            for (int j = 0; j < printWriters.size(); j++)
                printWriters.get(j).println("Send Your Answers");

        }

    }

    private void determineAlphabet() {
        int playerIndex = Integer.parseInt(serverPlan.charAt(thisRound - 1) + ""); //because thisRound start from 1
        String alphabetString = scanners.get(playerIndex).nextLine();
        char alphabetChar = alphabetString.charAt(0);
        if (!usedAlphabets.contains(alphabetChar)) {
            usedAlphabets.add(alphabetChar);
            printWriters.get(playerIndex).println(0 + ""); //code for no problem

            try {
                Thread.sleep(200); // can be omitted? yes 99%
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            sendAlphabetToClients(alphabetChar);

        } else { //sent alphabet was repeated
            printWriters.get(playerIndex).println(-1 + ""); //code for problem
            determineAlphabet();
        }
    }

    private void sendAlphabetToClients(char alphabetChar) {
        for (int i = 0; i < printWriters.size(); i++) {
            printWriters.get(i).println(alphabetChar + "");
        }
    }

    private ArrayList<Integer> calculatePoints(ArrayList<String> answers, String category) {
        ArrayList<Integer> points = new ArrayList<>();
        for (int i = 0; i < answers.size(); i++) {
            points.add(5);
        }

        return points;
    }


    private void sendNotif() {
        for (PrintWriter p : printWriters) {
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

