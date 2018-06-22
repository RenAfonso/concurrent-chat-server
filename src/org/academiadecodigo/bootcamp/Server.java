package org.academiadecodigo.bootcamp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by codecadet on 22/06/2018.
 */
public class Server {

    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    private ArrayList<ServerWorker> serverWorkerList;

    public Server() {
        serverWorkerList = new ArrayList<>();
    }


    public void start() {


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

            ExecutorService fixedPool = Executors.newFixedThreadPool(50);

            while (true) {
                Socket clientSocket = serverSocket.accept();


                ServerWorker serverWorker = new ServerWorker(clientSocket);

                serverWorkerList.add(serverWorker);
                fixedPool.submit(serverWorker);
            }

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }

    }

    synchronized void sendAll(String message) {

        for (int i = 0; i < serverWorkerList.size(); i++) {
            serverWorkerList.get(i).send(message);
        }
    }


    public class ServerWorker implements Runnable {

        Socket clientSocket;
        PrintWriter out;
        BufferedReader in;
        String line;

        public ServerWorker(Socket clientSocket) {

            this.clientSocket = clientSocket;

            buildReader();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    if ((line = in.readLine()) != null) {
                        sendAll(line);

                    }

                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, e.getMessage());
                }
            }
        }

        void send(String string) {
            out.println(string);
        }

        void buildReader() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

