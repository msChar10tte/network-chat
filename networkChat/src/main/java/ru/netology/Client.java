package ru.netology;

import java.io.*;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

public class Client {
    private String host;
    private int port;
    private String name;
    private final Logger logger = Logger.getInstance();

    public Client() {
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream input = Client.class.getClassLoader().getResourceAsStream("settings.txt")) {
            Properties props = new Properties();
            if (input == null) {
                System.out.println("Файл settings.txt не найден. Используются значения по умолчанию.");
                host = "localhost";
                port = 8080;
                return;
            }
            props.load(input);
            host = props.getProperty("host");
            port = Integer.parseInt(props.getProperty("port"));
        } catch (IOException | NumberFormatException ex) {
            ex.printStackTrace();
            host = "localhost";
            port = 8080;
        }
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите ваше имя: ");
        this.name = scanner.nextLine();

        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(name);
            logger.log("Вы (" + name + ") подключились к серверу. Для выхода введите /exit.");

            Thread readerThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while (!Thread.currentThread().isInterrupted() && (serverMessage = in.readLine()) != null) {
                        logger.log(serverMessage);
                    }
                } catch (IOException e) {
                    logger.log("Соединение с сервером разорвано.");
                }
            });
            readerThread.start();


            String clientMessage;
            while (scanner.hasNextLine()) {
                clientMessage = scanner.nextLine();
                out.println(clientMessage);
                logger.log(this.name + " (Вы): " + clientMessage);
                if ("/exit".equalsIgnoreCase(clientMessage.trim())) {
                    readerThread.interrupt();
                    break;
                }
            }

        } catch (IOException e) {
            logger.log("Не удалось подключиться к серверу: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}
