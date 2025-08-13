package ru.netology;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoggerTest {

    @Test
    void log_writesMessageToFile() throws IOException {

        String testMessage = "Это тестовое сообщение для JUnit";
        File logFile = new File("file.log");
        if (logFile.exists()) {
            logFile.delete();
        }
        Logger logger = Logger.getInstance();

        logger.log(testMessage);

        assertTrue(logFile.exists());
        String content = Files.readString(Path.of(logFile.getPath()));
        assertTrue(content.contains(testMessage));

        logFile.delete();
    }
}