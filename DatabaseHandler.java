package com.example.newesmfamil2;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseHandler extends Configs {
    Connection connection = null;

    public Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName, dbUser, dbPass);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public int createServer(String password, String gameName, String hostName, int rounds, String mode, int time) throws SQLException {
        String query = "SELECT * FROM " + Const.GAMES_TABLE;
        PreparedStatement read = getConnection().prepareStatement(query);
        ResultSet resultSetReadPort = read.executeQuery();

        String insert = "INSERT INTO " + Const.GAMES_TABLE + "(" + Const.GAME_PORT + "," + Const.GAME_PASSWORD + "," + Const.GAME_GAMENAME + "," +
                Const.GAME_HOSTNAME + "," + Const.GAME_ROUNDS + "," + Const.GAME_MODE + "," + Const.GAME_TIME + ")" + "VALUES" + "(?,?,?,?,?,?,?)";
        PreparedStatement write = getConnection().prepareStatement(insert);

        int lastPort = -1;
        int newPort;

        while (resultSetReadPort.next()) {
            lastPort = resultSetReadPort.getInt(Const.GAME_PORT);
        }

        System.out.println("databaseHandler: last port" + lastPort);


        if (lastPort == -1)
            newPort = ServerFactory.FIRST_PORT;
        else
            newPort = lastPort + 1;

        write.setInt(1, newPort);
        write.setString(2, password);
        write.setString(3, gameName);
        write.setString(4, hostName);
        write.setInt(5, rounds);
        write.setString(6, mode);
        write.setInt(7, time);

        write.executeUpdate();

        write.close();
        read.close();

        System.out.println("dbHandler newId = " + newPort);

        return newPort;
    }

    public void removeServer(int port) {
        String statement = "DELETE FROM " + Const.GAMES_TABLE + " WHERE " + Const.GAME_PORT + "=?";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = getConnection().prepareStatement(statement);
            preparedStatement.setInt(1, port);

            preparedStatement.execute();
            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Server> showServers() {
        ArrayList<Server> servers = new ArrayList<>();

        String query = "SELECT * FROM " + Const.GAMES_TABLE;
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int gameId = resultSet.getInt(1);
                String password = resultSet.getString(2);
                String gameName = resultSet.getString(3);
                String hostName = resultSet.getString(4);
                int rounds = resultSet.getInt(5);
                String mode = resultSet.getString(6);
                int time = resultSet.getInt(7);

                Server server = new Server(gameId, password, null, hostName, gameName, rounds, mode, time);
                servers.add(server);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return servers;
    }

    public boolean isServerExist(int gamePort) {
        String query = "SELECT * FROM " + Const.GAMES_TABLE + " WHERE " + Const.GAME_PORT + "=?";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(query);
            preparedStatement.setInt(1, gamePort);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
