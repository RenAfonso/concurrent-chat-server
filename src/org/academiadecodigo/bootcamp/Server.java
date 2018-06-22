package org.academiadecodigo.bootcamp;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by codecadet on 22/06/2018.
 */
public class Server {

    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    public static void main(String[] args) {

        ConsoleHandler consoleHandler = new ConsoleHandler();
        LOGGER.addHandler(consoleHandler);
        consoleHandler.setLevel(Level.ALL);
        LOGGER.setLevel(Level.ALL);

        System.out.println("Port?\n");

        Scanner scanner = new Scanner(System.in);
        int portNumber = scanner.nextInt();


        System.out.println("Waiting for clients");

        try {

            ServerSocket serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
    }
}
