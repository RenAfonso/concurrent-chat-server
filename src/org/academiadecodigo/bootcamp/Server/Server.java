package org.academiadecodigo.bootcamp.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    private final List<ServerWorker> serverWorkerList;

    Server() {
        serverWorkerList = Collections.synchronizedList(new ArrayList<>());
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

                ServerWorker serverWorker = new ServerWorker(clientSocket, this);

                serverWorkerList.add(serverWorker);
                fixedPool.submit(serverWorker);
            }

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
    }

    void sendAll(String message) {

        synchronized (serverWorkerList) {

            String[] stringSplit = message.split(":");

            if (stringSplit[1].equals("pvtm")) {
                handlePvtm(stringSplit);
                return;
            }

            if (stringSplit[1].equals("setnickname")) {
                handleSetNickName(stringSplit);
                return;
            }

            if (stringSplit[0].equals("setnickname")) {
                return;
            }

            if (stringSplit[1].equals("@online")) {
                handleWho(stringSplit);
                return;
            }

            if (stringSplit[1].equals("logout")) {
                handleLogout(stringSplit[0]);
            }

            for (int i = 0; i < serverWorkerList.size(); i++) {
                serverWorkerList.get(i).send(message);
            }
        }
    }

    private void handlePvtm(String[] stringSplit) {
        String sender = stringSplit[0];
        String receiver = stringSplit[2];
        String messageToSend = stringSplit[3];

        for (int i = 0; i < serverWorkerList.size(); i++) {
            if (!receiver.equals(serverWorkerList.get(i).getName())) {
                continue;
            }
            serverWorkerList.get(i).send("\u001B[33m" + sender + ":" + messageToSend + "\u001B[0m");
        }
    }

    private void handleSetNickName(String[] stringSplit) {
        String oldNickName = stringSplit[0];
        String newNickName = stringSplit[2];

        for (int i = 0; i < serverWorkerList.size(); i++) {
            if (oldNickName.equals(serverWorkerList.get(i).getName())) {
                serverWorkerList.get(i).send("Your nickname is now " + newNickName);
                serverWorkerList.get(i).setName(newNickName);
                continue;
            }
            serverWorkerList.get(i).send(oldNickName + " is now known as " + newNickName);
        }
    }

    private void handleWho(String[] stringSplit) {
        String sender = stringSplit[0];
        String userArray = "";

        for (int i = 0; i < serverWorkerList.size(); i++) {
            userArray += serverWorkerList.get(i).getName() + "\n";
        }

        for (int i = 0; i < serverWorkerList.size(); i++) {
            if (sender.equals(serverWorkerList.get(i).getName())) {
                serverWorkerList.get(i).send(userArray);
            }
        }
    }

    private void handleLogout(String user) {

        for (int i = 0; i < serverWorkerList.size(); i++) {
            if (user.equals(serverWorkerList.get(i).getName())) {
                serverWorkerList.get(i).send("logout");
                serverWorkerList.remove(serverWorkerList.get(i));
            }
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

