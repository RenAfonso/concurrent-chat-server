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

    private String name;

    public Client(String name) {
        this.name = name;
    }

    public void start() {

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

                @Override
                public void run() {
                    String line;

                    System.out.println("You just logged in as: " + getName());

                    out.println("setnickname:" + getName());

                    while (connected) {
                        try {
                            if ((line = in.readLine()) != null) {
                                    System.out.println(line);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });


            while (true) {
                Scanner messageArg = new Scanner(System.in);
                String messageToSend = getName() + ":" + messageArg.nextLine();

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

    public String getName() {
        return name;
    }
}
