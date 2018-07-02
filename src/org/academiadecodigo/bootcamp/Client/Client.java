package org.academiadecodigo.bootcamp.Client;

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

    private boolean connected = true;
    private Scanner messageArg;

    void start() {

        Socket clientSocket = null;

        ConsoleHandler consoleHandler = new ConsoleHandler();
        LOGGER.addHandler(consoleHandler);
        consoleHandler.setLevel(Level.ALL);
        LOGGER.setLevel(Level.ALL);

        System.out.println("Destination host?");
        Scanner addressArg = new Scanner(System.in);
        String hostName = addressArg.next();

        System.out.println("Destination port?");
        int portNumber = addressArg.nextInt();

        try {

            clientSocket = new Socket(hostName, portNumber);

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));



            ExecutorService singleExecutor = Executors.newSingleThreadExecutor();

            singleExecutor.submit(new Runnable() {
                boolean connected = true;

                @Override
                public void run() {
                    String line;

                    while (connected) {
                        try {
                            if ((line = in.readLine()) != null) {
                                if (line.startsWith("logout")) {
                                    System.out.println("logging out");
                                    connected = false;
                                }
                                System.out.println(line);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            messageArg = new Scanner(System.in);

            System.out.println("nickname?");

            String nicknameToSend = messageArg.nextLine();

            out.println("setnickname:" + nicknameToSend);

            System.out.println("Your nickname is now " + nicknameToSend);

            while (connected) {

                String messageToSend = messageArg.nextLine();

                out.println(messageToSend);

                if (messageToSend.equals("logout")) {
                    connected = false;
                    continue;
                }
            }

        } catch (NullPointerException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
            System.exit(1);

        } catch (NoSuchElementException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
            System.exit(1);

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, e.getMessage());

        } finally {
            closeSocket(clientSocket);
            closeScanner(messageArg);
        }
    }

    private void closeSocket(Socket clientSocket) {
        try {
            clientSocket.close();

        } catch (NullPointerException e) {
            System.out.println(e.getMessage());

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void closeScanner(Scanner scanner) {
            scanner.close();
    }
}
