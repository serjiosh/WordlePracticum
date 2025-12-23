import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для класса WordleDictionary
 */
public class WordleDictionaryTest {
    private WordleDictionary dictionary;
    
    @BeforeEach
    void setUp() {
        List<String> words = Arrays.asList("слово", "тест", "игра", "код", "герой", "гонец", "СЛОВО", "Ёлка", "ёлка");
        dictionary = new WordleDictionary(words);
    }
    
    @Test
    void testNormalizeWord() {
        assertEquals("слово", WordleDictionary.normalizeWord("СЛОВО"));
        assertEquals("елка", WordleDictionary.normalizeWord("Ёлка"));
        assertEquals("елка", WordleDictionary.normalizeWord("ёлка"));
        assertEquals("", WordleDictionary.normalizeWord(null));
        assertEquals("тест", WordleDictionary.normalizeWord("  тест  "));
    }
    
    @Test
    void testIsValidLength() {
        assertTrue(WordleDictionary.isValidLength("слово"));
        assertTrue(WordleDictionary.isValidLength("тест"));
        assertFalse(WordleDictionary.isValidLength("код"));
        assertFalse(WordleDictionary.isValidLength("длинноеслово"));
        assertFalse(WordleDictionary.isValidLength(null));
    }
    
    @Test
    void testContainsOnlyRussianLetters() {
        assertTrue(WordleDictionary.containsOnlyRussianLetters("слово"));
        assertTrue(WordleDictionary.containsOnlyRussianLetters("тест"));
        assertFalse(WordleDictionary.containsOnlyRussianLetters("word"));
        assertFalse(WordleDictionary.containsOnlyRussianLetters("слово123"));
        assertFalse(WordleDictionary.containsOnlyRussianLetters(null));
        assertFalse(WordleDictionary.containsOnlyRussianLetters(""));
    }
    
    @Test
    void testContains() {
        assertTrue(dictionary.contains("слово"));
        assertTrue(dictionary.contains("СЛОВО"));
        assertTrue(dictionary.contains("Ёлка"));
        assertTrue(dictionary.contains("елка"));
        assertFalse(dictionary.contains("нет"));
    }
    
    @Test
    void testGetFiveLetterWords() {
        List<String> fiveLetterWords = dictionary.getFiveLetterWords();
        assertEquals(4, fiveLetterWords.size());
        assertTrue(fiveLetterWords.contains("слово"));
        assertTrue(fiveLetterWords.contains("герой"));
        assertTrue(fiveLetterWords.contains("гонец"));
        assertTrue(fiveLetterWords.contains("елка"));
    }
    
    @Test
    void testGetRandomWord() {
        String word = dictionary.getRandomWord();
        assertNotNull(word);
        assertTrue(dictionary.contains(word));
    }
    
    @Test
    void testCompareWordsExactMatch() {
        String result = WordleDictionary.compareWords("слово", "слово");
        assertEquals("+++++", result);
    }
    
    @Test
    void testCompareWordsNoMatch() {
        String result = WordleDictionary.compareWords("тест", "слово");
        assertEquals("----", result);
    }
    
    @Test
    void testCompareWordsPartialMatch() {
        String result = WordleDictionary.compareWords("гонец", "герой");
        // г - правильная позиция (+)
        // о - есть в слове, но не на месте (^)
        // н - нет в слове (-)
        // е - правильная позиция (+)
        // ц - нет в слове (-)
        assertEquals("+^-+-", result);
    }
    
    @Test
    void testCompareWordsAllWrongPosition() {
        String result = WordleDictionary.compareWords("тест", "стет");
        // Все буквы есть, но на неправильных позициях
        assertEquals("^^^^", result);
    }
    
    @Test
    void testFilterWords() {
        Map<Integer, Character> correctPositions = new HashMap<>();
        correctPositions.put(0, 'г');
        
        Map<Integer, Set<Character>> wrongPositions = new HashMap<>();
        Set<Character> wrongAt1 = new HashSet<>();
        wrongAt1.add('о');
        wrongPositions.put(1, wrongAt1);
        
        Set<Character> presentLetters = new HashSet<>();
        presentLetters.add('е');
        
        Set<Character> absentLetters = new HashSet<>();
        absentLetters.add('н');
        absentLetters.add('ц');
        
        List<String> filtered = dictionary.filterWords(correctPositions, wrongPositions, presentLetters, absentLetters);
        
        // Должно найти слова, начинающиеся с 'г', содержащие 'е', но не 'н' и 'ц'
        assertTrue(filtered.contains("герой"));
        assertFalse(filtered.contains("гонец"));
    }
    
    @Test
    void testGetRandomWordFromEmptyDictionary() {
        WordleDictionary emptyDict = new WordleDictionary(Collections.emptyList());
        assertThrows(RuntimeException.class, () -> emptyDict.getRandomWord());
    }
}

