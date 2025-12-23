import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для класса WordleDictionaryLoader
 */
public class WordleDictionaryLoaderTest {
    private static final String TEST_DICTIONARY_FILE = "test_dictionary.txt";
    
    @BeforeAll
    static void setUp() throws IOException {
        // Создаём тестовый файл словаря
        try (PrintWriter writer = new PrintWriter(new FileWriter(TEST_DICTIONARY_FILE, StandardCharsets.UTF_8))) {
            writer.println("слово");
            writer.println("тест");
            writer.println("игра");
            writer.println("  "); // пустая строка с пробелами
            writer.println("код");
        }
    }
    
    @Test
    void testLoadDictionary() throws IOException {
        List<String> words = WordleDictionaryLoader.loadDictionary(TEST_DICTIONARY_FILE);
        
        assertNotNull(words);
        assertEquals(4, words.size());
        assertTrue(words.contains("слово"));
        assertTrue(words.contains("тест"));
        assertTrue(words.contains("игра"));
        assertTrue(words.contains("код"));
    }
    
    @Test
    void testLoadDictionaryWithEmptyFile() throws IOException {
        String emptyFile = "empty_test.txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(emptyFile, StandardCharsets.UTF_8))) {
            // Пустой файл
        }
        
        List<String> words = WordleDictionaryLoader.loadDictionary(emptyFile);
        assertTrue(words.isEmpty());
        
        new File(emptyFile).delete();
    }
    
    @Test
    void testLoadDictionaryFileNotFound() {
        assertThrows(FileNotFoundException.class, () -> {
            WordleDictionaryLoader.loadDictionary("nonexistent_file.txt");
        });
    }
}

