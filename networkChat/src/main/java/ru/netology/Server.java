package ru.netology;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Collections;

public class Server {
    private int port;
    private final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    private final Logger logger = Logger.getInstance();

    public Server() {
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream input = Server.class.getClassLoader().getResourceAsStream("settings.txt")) {
            Properties props = new Properties();
            if (input == null) {
                logger.log("Файл settings.txt не найден, используется порт 8080");
                port = 8080;
                return;
            }
            props.load(input);
            port = Integer.parseInt(props.getProperty("port"));
        } catch (IOException | NumberFormatException ex) {
            logger.log("Ошибка чтения настроек, используется порт 8080. Ошибка: " + ex.getMessage());
            port = 8080;
        }
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.log("Сервер запущен на порту " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.log("Новый клиент подключился: " + clientSocket.getRemoteSocketAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            logger.log("Критическая ошибка сервера: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String message, ClientHandler sender) {
        logger.log(message);
        for (ClientHandler client : new ArrayList<>(clients)) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}