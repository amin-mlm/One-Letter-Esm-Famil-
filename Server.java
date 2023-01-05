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

    private int rate = 50;

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

    int numPlayers = 0;

    private ArrayList<Socket> sockets = new ArrayList<>();
    private ArrayList<Scanner> scanners = new ArrayList<>();
    private ArrayList<PrintWriter> printWriters = new ArrayList<>();
    private ArrayList<String> clientsName = new ArrayList<>();


    private ArrayList<Integer> clientsThisRoundPoints = new ArrayList<>();
    private ArrayList<Integer> clientsSumPoints = new ArrayList<>();

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

            numPlayers++;

            exchangeInfo(scanner, printwriter, numPlayers - 1);

        }
    }

    private void exchangeInfo(Scanner scanner, PrintWriter printWriter, int indexBetweenAllPlayers) {

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

        for (int i = 0; i < numPlayers; i++) {
            clientsSumPoints.add(0);
            clientsThisRoundPoints.add(0);
        }

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
            printWriters.get(i).println(numPlayers + "");
            printWriters.get(i).println(i + "");

        }


        new Thread(() -> { //can be without thread? yes I think
            sendGoToGameTOCLients();
            determineAlphabet();
            waiteToFinishRoundAndCheckAnswers();
        }).start();

    }


    int index;

    private void waiteToFinishRoundAndCheckAnswers() {
        System.out.println("in server, scanner size:" + scanners.size());
        for (index = 0; index < scanners.size(); index++) {
            new Thread(() -> {
                int relatedIndex = index;
                System.out.println("in server, listener is set for index, " + relatedIndex);
                String message = scanners.get(relatedIndex).nextLine();
                System.out.println("message in server form client no." + relatedIndex + ", " + message);
                if (message.equals("I Finish This Round")) {
                    new Thread(() -> {
                        System.out.println("relatedIndex of finisher: " + relatedIndex);
                        scanners.get(relatedIndex).nextLine();
                    }).start();
                    try {
                        Thread.sleep(500); //was 20
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //nothing
                    CollectAndCheckAnswers(relatedIndex);
                } else if (message.equals("I Will Send The Answer Now")) {
                    //nothing
                }
            }).start();
            try {
                Thread.sleep(70); //was 20
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void CollectAndCheckAnswers(int finisherIndex) { //index in 3 arrayLists(sockets, scanners, printWriters)
        new Thread(() -> {
            //get 1 field answer from all clients and
            // send back the points they are given for this
            // field and then go to next field and so on
            for (int i = 0; i < numFields; i++) {

                System.out.println("\nfield: " + (i + 1));
                ArrayList<String> answers = new ArrayList<>();
                ArrayList<String> points = new ArrayList<>();

                for (index = 0; index < scanners.size(); index++) {
                    new Thread(() -> {
                        String answer = scanners.get(index).nextLine();
                        answers.add(answer);
                    }).start();

                    try {
                        Thread.sleep(50); //can be 1? yes 99%++
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while (answers.size() != scanners.size()) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("server, checking answers: " + answers);
                points = getReactionsAndCalculatePoints(answers, fields.get(i));

                //send clients points
                for (int j = 0; j < printWriters.size(); j++) {
                    printWriters.get(j).println(points.get(j));
                }

                for (int j = 0; j < points.size(); j++) {
                    clientsThisRoundPoints.set(j, clientsThisRoundPoints.get(j) + Integer.parseInt(points.get(j)));
                    clientsSumPoints.set(j, clientsSumPoints.get(j) + Integer.parseInt(points.get(j)));
                }


//                if (i == fields.size() - 1)
//                    try {
//                        Thread.sleep(16000); //1 + 15(see their points)
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                else
//                    try {
//                        Thread.sleep(14000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }


            }
            try {
                Thread.sleep(16000); //1 + 15(see their points)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (++thisRound <= rounds) {
                for (int i = 0; i < printWriters.size(); i++) {
                    printWriters.get(i).println(clientsThisRoundPoints.get(i) + "");
                }
                for (int j = 0; j < clientsThisRoundPoints.size(); j++) {
                    clientsThisRoundPoints.set(j, 0);
                }
                nextRound();
            } else {

            }


        }).start();

        for (int j = 0; j < printWriters.size(); j++)
            printWriters.get(j).println("Send Your Answers");
    }

    private void nextRound() {
        determineAlphabet();
        waiteToFinishRoundAndCheckAnswers();
    }


    private ArrayList<String> getReactionsAndCalculatePoints(ArrayList<String> answers, String category) {
        ArrayList<ArrayList<String>> allReactions = new ArrayList<>();
        for (index = 0; index < numPlayers; index++) {
            ArrayList<String> reactionsOfOnePlayer = new ArrayList<>();
            for (int i = 0; i < numPlayers; i++) {
                if (index == i)
                    continue;
                System.out.println("send answer: " + answers.get(i) + " to client no." + index);
                printWriters.get(index).println(answers.get(i));
            }
            new Thread(() -> {
                int relatedIndex = index;
                System.out.println("server 260-307, relatedIndex : " + relatedIndex);

                System.out.println("server, listening ... for reaction of player " + relatedIndex);
                for (int i = 0; i < numPlayers; i++) {
                    if (i == relatedIndex) {
                        reactionsOfOnePlayer.add("Positive");
                        continue;
                    }
                    String reaction = scanners.get(relatedIndex).nextLine();
                    System.out.println("reaction of player " + relatedIndex + "to player " + i + " is: " + reaction);
                    reactionsOfOnePlayer.add(reaction);
                }
                allReactions.add(reactionsOfOnePlayer);
            }).start();

            try {
                Thread.sleep(50); //important
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        while (allReactions.size() != numPlayers) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("all reactions: " + allReactions);
        ArrayList<String> filteredAnswers = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            int positiveReactions = 0;
            for (int j = 0; j < numPlayers; j++) {
                if (i != j && allReactions.get(j).get(i).equals("Positive")) {
                    positiveReactions++;
                }
            }
            System.out.println("pos. reactions to i " + i + " is: " + positiveReactions);
            if (((double) positiveReactions / (numPlayers - 1)) >= ((double) rate / 100)) {
                filteredAnswers.add(answers.get(i));
            } else {
                filteredAnswers.add("");
            }
        }

        System.out.println("SOO filteredAnswers " + filteredAnswers);
        return checkSimilarities(filteredAnswers);

    }

    private ArrayList<String> checkSimilarities(ArrayList<String> answers) {
        ArrayList<String> points = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            if (answers.get(i).equals(""))
                points.add(0 + "");
            else
                for (int j = 0; j < numPlayers; j++) {
                    if (i != j && answers.get(i).equals(answers.get(j))) {
                        points.add(5 + "");
                        break;
                    } else if (j == numPlayers - 1)
                        points.add(10 + "");
                }
        }
        return points;
    }


    private void determineAlphabet() {
        //index of player in scanners arraylist who has to determine the alphabet
        int playerIndex = Integer.parseInt(serverPlan.charAt(thisRound - 1) + ""); //because thisRound start from 1
        String alphabetString = scanners.get(playerIndex).nextLine();
        char alphabetChar = alphabetString.charAt(0);
        if (!usedAlphabets.contains(alphabetChar)) {
            usedAlphabets.add(alphabetChar);
            printWriters.get(playerIndex).println(0 + ""); //code for no problem

            try {
                Thread.sleep(200); // can be omitted? yes 99% actually 200 is high
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

    private void sendGoToGameTOCLients() {
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

