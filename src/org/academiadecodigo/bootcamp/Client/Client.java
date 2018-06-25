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

    private String name;
    private boolean connected = true;
    private Scanner messageArg;

    Client(String name) {
        this.name = name;
    }

    void start() {

        Socket clientSocket = null;

        ConsoleHandler consoleHandler = new ConsoleHandler();
        LOGGER.addHandler(consoleHandler);
        consoleHandler.setLevel(Level.ALL);
        LOGGER.setLevel(Level.ALL);

        /*System.out.println("Destination host?");
        Scanner addressArg = new Scanner(System.in);
        String hostName = addressArg.next();*/

        /*System.out.println("Destination port?");
        int portNumber = addressArg.nextInt();*/

        try {

            clientSocket = new Socket("localhost", 5000);

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));



            ExecutorService singleExecutor = Executors.newSingleThreadExecutor();

            singleExecutor.submit(new Runnable() {
                boolean connected = true;
                String nickname;

                @Override
                public void run() {
                    String line;

                    System.out.println("You just logged in as: " + getName());

                    out.println("setnickname:" + getName());

                    nickname = getName();

                    while (connected) {
                        try {
                            if ((line = in.readLine()) != null) {
                                if (line.startsWith("Your nickname is now ")) {
                                    handleNameChange(line);
                                }
                                if (line.startsWith("logout")) {
                                    System.out.println("logging out");
                                    connected = false;
                                    break;
                                }
                                System.out.println(line);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });


            while (connected) {
                messageArg = new Scanner(System.in);
                String messageToSend = getName() + ":" + messageArg.nextLine();

                out.println(messageToSend);

                if (messageToSend.equals(getName() + ":logout")) {
                    connected = false;
                    break;
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

    private void handleNameChange(String line) {
        String[] nickArray = line.split(" ");
        setName(nickArray[4]);
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

    private String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }
}
