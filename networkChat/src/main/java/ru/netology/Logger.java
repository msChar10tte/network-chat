package ru.netology;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final String LOG_FILE_PATH = "file.log";
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private static Logger INSTANCE;

    private Logger() {}

    public static synchronized Logger getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Logger();
        }
        return INSTANCE;
    }

    public synchronized void log(String message) {
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE_PATH, true))) {
            String logEntry = String.format("[%s] %s", dtf.format(LocalDateTime.now()), message);
            System.out.println(logEntry);
            out.println(logEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}