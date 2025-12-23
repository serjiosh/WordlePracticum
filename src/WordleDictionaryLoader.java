import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для загрузки словаря из файла
 */
public class WordleDictionaryLoader {
    
    /**
     * Загружает словарь из файла
     * @param filename путь к файлу словаря
     * @return список слов из словаря
     * @throws IOException если произошла ошибка при чтении файла
     */
    public static List<String> loadDictionary(String filename) throws IOException {
        List<String> words = new ArrayList<>();
        
        try (FileReader fileReader = new FileReader(filename, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(fileReader)) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    words.add(line);
                }
            }
        }
        
        return words;
    }
}

