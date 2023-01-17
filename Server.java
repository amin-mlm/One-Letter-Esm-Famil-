package com.example.newesmfamil2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Server {

    private int port;
    private String hostName;
    private String gameName;
    private String password;

    private CreateGameController createGameController;

    private int rate = 50;

    private int numFields;
    private ArrayList<String> fields;
    private String gameMode;
    private int rounds;
    private int time;

    private int thisRound = 1;
    private String serverPlan;
    private ArrayList<Character> usedAlphabets = new ArrayList<>();

    boolean isAcceptingClientEnough = false;
    boolean didClientsSendAnswers = false;

    private int numPlayers = 0;

    private ArrayList<Socket> sockets = new ArrayList<>();
    private ArrayList<Scanner> scanners = new ArrayList<>();
    private ArrayList<PrintWriter> printWriters = new ArrayList<>();



    private ArrayList<Integer> clientsThisRoundPoints = new ArrayList<>();
    private ArrayList<Integer> clientsSumPoints = new ArrayList<>();
    private ArrayList<String> clientsName = new ArrayList<>();

    ServerSocket serverSocket = null;
    private boolean hostLeftGame = false;

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

    public void startAcceptingClient() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!isAcceptingClientEnough) {

            Socket socket = null;
            Scanner scanner = null;
            PrintWriter printwriter = null;

            try {
                try{
                    socket = serverSocket.accept();
                }catch (SocketException e){ //if the host closes the window, serverSocket.accept() throws exception
                    System.out.println("exception passed");
                    return;
                }
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

            String playerName = exchangeInfo(scanner, printwriter, numPlayers - 1);

            createGameController.addPlayerToBoard(playerName);
        }

        //remove game from database
        new DatabaseHandler().removeServer(port);

        try {
            System.out.println("serverSocket closed after finishing gettingClient");
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String exchangeInfo(Scanner scanner, PrintWriter printWriter, int indexBetweenAllPlayers) {
        String name = scanner.nextLine();
        clientsName.add(name);

        System.out.println("client: new client: " + name);

        printWriter.println(numFields);
        for (String s : fields)
            printWriter.println(s);
        printWriter.println(gameMode);
        printWriter.println(rounds);
        printWriter.println(time);

        return name;
    }

     void closeServerSocket(){
        if(!serverSocket.isClosed())
            try {
                System.out.println("now sreverSocket closed");
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void startGame() {
        System.out.println("in server, playernames");
        for (String s : clientsName) System.out.println(s);

        for (int i = 0; i < numPlayers; i++) {
            clientsSumPoints.add(0);
            clientsThisRoundPoints.add(0);
        }

        //bring host from last to first index of arrayLists
        sockets.add(0, sockets.get(sockets.size() - 1));
        sockets.remove(sockets.size() - 1);

        scanners.add(0, scanners.get(scanners.size() - 1));
        scanners.remove(scanners.size() - 1);

        printWriters.add(0, printWriters.get(printWriters.size() - 1));
        printWriters.remove(printWriters.size() - 1);

        clientsName.add(0, clientsName.get(clientsName.size() - 1));
        clientsName.remove(clientsName.size() - 1);

        //set plan for server to get alphabet from clients
        //for instance,plan: 0123012 means client index 0 (in scanners list) should
        //determine the game alphabet in the first round,
        //client index 1 should determine in the second round and so on.
        int numPlayers = sockets.size();
        serverPlan = "";
        for (int i = 0; i < rounds; i++) {
            serverPlan += i % numPlayers;
        }

        //set and send plan for determining alphabets via clients
        //for instance,plan: 0100010 means this client should determine the
        //game alphabet in the second and sixth round.
        for (int i = 0; i < numPlayers; i++) {
            String clientPlan = "";
            for (int j = 0; j < rounds; j++) {
                if (j % numPlayers == i)
                    clientPlan += "1";
                else
                    clientPlan += "0";
            }
            printWriters.get(i).println(clientPlan); //send plan
            printWriters.get(i).println(numPlayers + ""); //send numPlayers
            printWriters.get(i).println(i + ""); //send index between all players

        }


        new Thread(() -> { //can be without thread? yes I think
            sendGoToGameToClients();
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
                String message;
                try {
                    message = scanners.get(relatedIndex).nextLine();
                }catch (NoSuchElementException e){
                    System.out.println("----noSuch SERVER 2(finish message) relatedIndex = " + relatedIndex);
                    closeSockets();
                    return;
                }
                System.out.println("message in server form client no." + relatedIndex + ", " + message);
                if (message.equals("I Finish This Round")) {
                    new Thread(() -> {
                        System.out.println("relatedIndex of finisher: " + relatedIndex);
                        scanners.get(relatedIndex).nextLine();
                    }).start();
                    //sleep to start above thread
                    try {
                        Thread.sleep(100); //was 20, was 500, was nothing(newer)
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //nothing

                    collectAndCheckAnswers(relatedIndex);

                } else if (message.equals("I Will Send The Answer Now")) {
                    //nothing
                }
            }).start();
            try {
                Thread.sleep(100); //100 fixed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void collectAndCheckAnswers(int finisherIndex) { //index in 3 arrayLists(sockets, scanners, printWriters)
        for (int j = 0; j < printWriters.size(); j++) //was after thread
            printWriters.get(j).println("Send Your Answers");

        new Thread(() -> {
            //get 1 field answer from all clients and
            // send back the points they are given for this
            // field and then go to next field and so on
            for (int i = 0; i < numFields; i++) {

                System.out.println("\nfield: " + (i + 1));
//                ArrayList<String> answers = new ArrayList<>();
                String[] answers = new String[numPlayers];
                ArrayList<String> points = new ArrayList<>();

                for (index = 0; index < scanners.size(); index++) {
                    new Thread(() -> {
                        int relatedIndex = index;
                        String answer;
                        try {
                            System.out.println("AOOOOOOOOOOOOOOOOOOO " + relatedIndex);
                            answer = scanners.get(relatedIndex).nextLine();
                            System.out.println("BOOOOOOOOOOOOOOOOOOO " + relatedIndex);
                        }catch (NoSuchElementException e){
                            System.out.println("----noSuch SERVER 3(answer)");
                            hostLeftGame = true;
                            closeSockets();
                            return;
                        }
//                        answers.add(answer);
                        answers[relatedIndex] = answer;
                    }).start();

                    try {
                        Thread.sleep(100); // 100 fixed
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
//                while (answers.size() != scanners.size()) {
//                    try {
//                        Thread.sleep(200); //check the answers for amount every 200 millis
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    if (hostLeftGame){
//                        return;
//                    }
//                }
                //check if all reactions collected
                while(true) {
                    int j;
                    for (j = 0; j < numPlayers; j++) {
                        if(answers[j]==null) //is not the last reaction of this player collected?
                            break;
                    }
                    if(j==numPlayers)
                        break;

                    try {
                        Thread.sleep(200); //check for reaction collection each 200 millis
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(hostLeftGame){
                        System.out.println("--- server returned(collecting answers)");
                        return;
                    }
                }

                System.out.println("server, checking answers: ");
                for (int j = 0; j <answers.length; j++) {
                    System.out.println(answers[j] + ", ");
                }

                points = getReactionsAndCalculatePoints(answers, fields.get(i));

                if(points==null) //host left game
                    return;

                //send clients points
                for (int j = 0; j < printWriters.size(); j++) {
                    printWriters.get(j).println(points.get(j));
                }

                for (int j = 0; j < points.size(); j++) {
                    clientsThisRoundPoints.set(j, clientsThisRoundPoints.get(j) + Integer.parseInt(points.get(j)));
                    clientsSumPoints.set(j, clientsSumPoints.get(j) + Integer.parseInt(points.get(j)));
                }

            }

            //(10s) see their points +
            // (1s) server wants to send s.t after
            // sleep, so clients should be ready for it
            try {
                Thread.sleep(11000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (++thisRound <= rounds) {
                sendRoundScoreToClients(); //and set the array "clientsThisRoundPoints" to 0
                nextRound();
            } else {
                sendRoundScoreToClients();

                //sort clients name by final score
                outer:
                for (int i = 0; i < clientsSumPoints.size(); i++) {
                    boolean shouldBreak = true;
                    for (int j = 0; j < clientsSumPoints.size() - 1; j++) {
                        if (clientsSumPoints.get(j) < clientsSumPoints.get(j + 1)) {
                            int tempScore = clientsSumPoints.get(j);
                            clientsSumPoints.set(j, clientsSumPoints.get(j + 1));
                            clientsSumPoints.set(j + 1, tempScore);

                            String tempName = clientsName.get(j);
                            clientsName.set(j, clientsName.get(j + 1));
                            clientsName.set(j + 1, tempName);

                            shouldBreak = false;
                        } else if (j == clientsSumPoints.size() - 2 && shouldBreak) {
                            break outer;
                        }

                    }
                }

                sendScoreBoardToClients();
                closeSockets();
            }

        }).start();
    }


    public void closeSockets() {
        //disconnect sockets
        System.out.println("AALL closed");
        for (int i = 0; i < sockets.size(); i++) {
            try {
                sockets.get(i).close();
//                printWriters.get(i).close();
//                scanners.get(i).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void sendScoreBoardToClients() {
        for (int i = 0; i < numPlayers; i++) {
            for (int j = 0; j < numPlayers; j++) {
                printWriters.get(i).println(clientsName.get(j));
                printWriters.get(i).println(clientsSumPoints.get(j));
            }
        }
    }

    private void sendRoundScoreToClients() {
        for (int i = 0; i < printWriters.size(); i++) {
            printWriters.get(i).println(clientsThisRoundPoints.get(i) + "");
        }
        for (int j = 0; j < clientsThisRoundPoints.size(); j++) {
            clientsThisRoundPoints.set(j, 0);
        }
    }

    private void nextRound() {
        determineAlphabet();
        waiteToFinishRoundAndCheckAnswers();
    }


    private ArrayList<String> getReactionsAndCalculatePoints(String[] answers, String category) {
//        ArrayList<ArrayList<String>> allReactions = new ArrayList<>();
        String[][] allReactions = new String[numPlayers][];
        for (index = 0; index < numPlayers; index++) {
//            ArrayList<String> reactionsOfOnePlayer = new ArrayList<>();
            allReactions[index] = new String[numPlayers];

            //send "other answers" of players to them
            for (int i = 0; i < numPlayers; i++) {
                if (index == i) //no need to send one's answer to oneself
                    continue;
                System.out.println("send answer: " + answers[i] + " to client no." + index);
                printWriters.get(index).println(answers[i]);
            }

            new Thread(() -> {
                int relatedIndex = index;
                System.out.println("server 260-307, relatedIndex : " + relatedIndex);

                System.out.println("server, listening ... for reaction of player " + relatedIndex);
                for (int i = 0; i < numPlayers; i++) {
                    if (i == relatedIndex) {
//                        reactionsOfOnePlayer.add("Positive");
                        allReactions[relatedIndex][i] = "Positive"; //oneself reacts "Positive" to oneself answer
                    }
                    else{
                        String reaction;
                        try {
                            reaction = scanners.get(relatedIndex).nextLine();
                        }catch (NoSuchElementException e){
                            System.out.println("----noSuch SERVER 4(reaction)");
                            hostLeftGame = true;
                            closeSockets();
                            return;
                        }
                        System.out.println("reaction of player " + relatedIndex + "to player " + i + " is: " + reaction);
//                        reactionsOfOnePlayer.add(reaction);
                        allReactions[relatedIndex][i] = reaction;
                    }
                }
                System.out.println("---omran ino bbini");
//                allReactions.add(reactionsOfOnePlayer);
            }).start();


            try {
                Thread.sleep(100); //important was 50
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

//        System.out.println("--- server while " + allReactions.size()  + "," +  numPlayers);
        //check if all reactions collected
        while(true) {
            int i;
            for (i = 0; i < numPlayers; i++) {
                if(allReactions[i][numPlayers-1]==null) //is not the last reaction of this player collected?
                     break;
            }
            if(i==numPlayers)
                break;

            try {
                Thread.sleep(200); //check for reaction collection each 200 millis
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(hostLeftGame){
                System.out.println("--- server returned");
                return null;
            }
        }

//        System.out.println("all reactions: " + allReactions);
        System.out.println("all reactions");
        for (int i = 0; i < numPlayers; i++) {
            System.out.print("[");
            for (int j = 0; j <numPlayers; j++) {
                System.out.print(allReactions[i][j]+",");
            }
            System.out.println("]");
        }

        //count pos and neg reactions
        ArrayList<String> filteredAnswers = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            int positiveReactions = 0;
            for (int j = 0; j < numPlayers; j++) {
                if (i != j && allReactions[j][i].equals("Positive")) {
                    positiveReactions++;
                }
            }
            System.out.println("pos. reactions to i " + i + " is: " + positiveReactions);
            if (((double) positiveReactions / (numPlayers - 1)) >= ((double) rate / 100)) {
                filteredAnswers.add(answers[i]);
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
        //index of player in "scanners" arraylist who has to determine the game alphabet
        int playerIndex = Integer.parseInt(serverPlan.charAt(thisRound - 1) + ""); //because thisRound start from 1
        String alphabetString;
        try {
            alphabetString = scanners.get(playerIndex).nextLine();
        }catch (NoSuchElementException e){
            System.out.println("----noSuch SERVER 1(alphabet)");
            closeSockets();
            return;
        }
        char alphabetChar = alphabetString.charAt(0);
        if (!usedAlphabets.contains(alphabetChar)) {
            usedAlphabets.add(alphabetChar);
            printWriters.get(playerIndex).println(0 + ""); //code for no problem

            //sleep for player who wants to determine alphabet
//            try {
//                Thread.sleep(200); // can be omitted? yes 99% actually 200 is high
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            sendAlphabetToClients(alphabetChar);

        } else { //received alphabet was repeated
            printWriters.get(playerIndex).println(-1 + ""); //code for problem
            determineAlphabet();
        }
    }

    private void sendAlphabetToClients(char alphabetChar) {
        for (int i = 0; i < printWriters.size(); i++) {
            printWriters.get(i).println(alphabetChar + "");
        }
    }

    private void sendGoToGameToClients() {
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

    public void setCreateGameController(CreateGameController createGameController) {
        this.createGameController = createGameController;
    }

    public int getNumPlayers() {
        return numPlayers;
    }
}

