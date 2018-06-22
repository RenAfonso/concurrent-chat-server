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

    private final ArrayList<ServerWorker> serverWorkerList = null;

    public Server() {


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
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                ServerWorker serverWorker = new ServerWorker(clientSocket, out, in, this);
                fixedPool.submit(serverWorker);
                serverWorkerList.add(serverWorker);


            }

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }

    }

    public void sendAll(String message) {

        for (int i = 0; i < serverWorkerList.size(); i++) {
            serverWorkerList.get(i).send(message);
        }
    }


    public class ServerWorker implements Runnable {

        Socket clientSocket;
        PrintWriter out;
        BufferedReader in;
        Server server;
        String line;

        public ServerWorker(Socket clientSocket, PrintWriter out, BufferedReader in, Server server) {

            this.clientSocket = clientSocket;
            this.out = out;
            this.in = in;
            this.server = server;
        }

        @Override
        public void run() {

            try {
                if ((line = in.readLine()) != null) {
                    server.sendAll(line);
                }

            } catch (IOException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
            }
        }

        void send(String string) {
            out.println(string);
        }
    }
}

