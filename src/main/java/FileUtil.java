import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtil {

    private static final String FILE_NAME = "chatIds.txt";

    public static boolean writeChatId(long chatId) {
        try {
            Path path = Paths.get(FILE_NAME);

            List<Long> adminChatIds = getChatIds();
            if (adminChatIds.contains(chatId)) return true;

            Files.write(path, (String.valueOf(chatId) + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static List<Long> getChatIds() {
        try {
            Path path = Paths.get(FILE_NAME);
            List<Long> result = new ArrayList<>();

            for (String line : Files.readAllLines(path)) {
                result.add(Long.valueOf(line));
            }
            return result;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

}