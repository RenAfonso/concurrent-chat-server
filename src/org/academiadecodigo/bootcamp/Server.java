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
class Server {

    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    private ArrayList<ServerWorker> serverWorkerList;

    Server() {
        serverWorkerList = new ArrayList<>();
    }


    void start() {

        ConsoleHandler consoleHandler = new ConsoleHandler();
        LOGGER.addHandler(consoleHandler);
        consoleHandler.setLevel(Level.ALL);
        LOGGER.setLevel(Level.ALL);

        /*System.out.println("Port?");

        Scanner scanner = new Scanner(System.in);
        int portNumber = scanner.nextInt();*/

        Socket clientSocket = null;

        System.out.println("Waiting for clients");

        try {

            ServerSocket serverSocket = new ServerSocket(5000);

            ExecutorService fixedPool = Executors.newFixedThreadPool(500);

            while (true) {
                clientSocket = serverSocket.accept();

                ServerWorker serverWorker = new ServerWorker(clientSocket);

                serverWorkerList.add(serverWorker);
                fixedPool.submit(serverWorker);
            }

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }

    }

    synchronized void sendAll(String message) {

        String[] stringSplit = message.split(":");

        if(stringSplit[1].equals("pvtm")) {
            for (int i = 0; i < serverWorkerList.size(); i++) {
                if (stringSplit[0].equals(serverWorkerList.get(i).getName())) {
                    continue;
                }
                if (!stringSplit[2].equals(serverWorkerList.get(i).getName())) {
                    continue;
                }
                serverWorkerList.get(i).send(stringSplit[0] + ":" + stringSplit[3]);
            }
            return;
        }

        if(stringSplit[1].equals("setnickname")) {
            for (int i = 0; i < serverWorkerList.size(); i++) {
                if (stringSplit[0].equals(serverWorkerList.get(i).getName())) {
                    continue;
                }
                if (!stringSplit[2].equals(serverWorkerList.get(i).getName())) {
                    continue;
                }
                serverWorkerList.get(i).send(stringSplit[3]);
            }
            return;
        }

        if(stringSplit[0].equals("setnickname")) {
            return;
        }

        for (int i = 0; i < serverWorkerList.size(); i++) {
            if (stringSplit[0].equals(serverWorkerList.get(i).getName())) {
                continue;
            }
            serverWorkerList.get(i).send(message);
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


    public class ServerWorker implements Runnable {

        Socket clientSocket;
        PrintWriter out;
        BufferedReader in;
        String line;
        String name;

        boolean connected = true;

        public ServerWorker(Socket clientSocket) {

            this.clientSocket = clientSocket;

            buildReader();
        }

        @Override
        public void run() {
            while (connected) {
                try {
                    if ((line = in.readLine()) != null) {
                        if (line.startsWith("logout")) {
                            connected = false;
                        }
                        if (line.startsWith("setnickname")) {
                            String[] stringSplit = line.split(":");
                            setName(stringSplit[1]);
                        }

                        sendAll(line);
                    }

                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, e.getMessage());
                }
            }

            closeSocket(clientSocket);
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

        void closeSocket(Socket clientSocket) {
            try {
                clientSocket.close();
            } catch (NullPointerException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}

