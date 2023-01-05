package com.example.newesmfamil2;

import java.sql.SQLException;
import java.util.ArrayList;


public class ServerFactory {
    static int FIRST_PORT = 8080;
    static String MAIN_HOST = "localhost";

    static ArrayList<Server> servers = new ArrayList<>();
    static DatabaseHandler databaseHandler = new DatabaseHandler();

    public static Server createServer(ArrayList<String> fields, String hostName, String gameName, String password, int rounds, String mode, int time) {
        int gamePort = 0;

        try {
            gamePort = databaseHandler.createServer(password, gameName, hostName, rounds, mode, time);
        } catch (SQLException e) {
            // add needs
            e.printStackTrace();
        }

        Server server = new Server(gamePort, password, fields, hostName, gameName, rounds, mode, time);

        return server;
    }

}

