package org.academiadecodigo.bootcamp.Server;

import org.academiadecodigo.bootcamp.Client.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by codecadet on 25/06/2018.
 */
public class ServerWorker implements Runnable {

    private final Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String line;
    private String name;
    private Server server;

    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());

    private boolean connected = true;

    ServerWorker(Socket clientSocket, Server server) {

        this.clientSocket = clientSocket;
        this.server = server;

        buildReader();
    }

    @Override
    public void run() {

        ConsoleHandler consoleHandler = new ConsoleHandler();
        LOGGER.addHandler(consoleHandler);
        consoleHandler.setLevel(Level.ALL);
        LOGGER.setLevel(Level.ALL);

        while (connected) {
            try {
                if ((line = in.readLine()) != null) {
                    if (line.startsWith("setnickname")) {
                        String[] stringSplit = line.split(":");
                        setName(stringSplit[1]);
                    }

                    server.sendAll(line);
                }

            } catch (IOException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
            }
        }

        //closeSocket();
    }

    void send(String string) {
        out.println(string);
    }

    private void buildReader() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeSocket() {
        try {
            clientSocket.close();
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    void setName(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }
}
