package org.academiadecodigo.bootcamp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by codecadet on 22/06/2018.
 */
public class Client {

    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());



    public static void main(String[] args) {

        Socket clientSocket = null;

        boolean connected = true;

        ConsoleHandler consoleHandler = new ConsoleHandler();
        LOGGER.addHandler(consoleHandler);
        consoleHandler.setLevel(Level.ALL);
        LOGGER.setLevel(Level.ALL);

        System.out.println("Destination host?\n");
        Scanner addressArg = new Scanner(System.in);
        String hostName = addressArg.next();

        System.out.println("Destination port?\n");
        int portNumber = addressArg.nextInt();

        try {

            clientSocket = new Socket(hostName, portNumber);

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            ExecutorService singleExecutor = Executors.newSingleThreadExecutor();

            singleExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    String line;

                    try {
                        if ((line = in.readLine()) != null) {
                            if (line.equals("logout")) {
                                System.out.println("logging out");
                                connected = false;
                            } else {
                                System.out.println("Server reply: " + line);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            while (connected) {
                System.out.println("Type message : ");
                Scanner messageArg = new Scanner(System.in);
                String messageToSend = messageArg.next();

                out.println(messageToSend);
            }

        } catch (NullPointerException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            closeSocket(clientSocket);
        }
    }

    private static void closeSocket(Socket clientSocket) {
        try {
            clientSocket.close();
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
