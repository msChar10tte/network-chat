package ru.netology;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Server server;
    private PrintWriter out;
    private BufferedReader in;
    private String clientName;

    public ClientHandler(Socket socket, Server server) {
        this.clientSocket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            this.clientName = in.readLine();
            if (this.clientName == null) return;

            server.broadcastMessage("Сервер: " + clientName + " присоединился к чату.", this);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if ("/exit".equalsIgnoreCase(inputLine.trim())) {
                    break;
                }
                server.broadcastMessage(clientName + ": " + inputLine, this);
            }
        } catch (IOException e) {
            System.out.println("Соединение с клиентом " + clientName + " потеряно.");
        } finally {
            server.removeClient(this);
            server.broadcastMessage("Сервер: " + clientName + " покинул чат.", this);
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}