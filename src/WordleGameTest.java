import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для класса WordleGame
 */
public class WordleGameTest {
    private WordleDictionary dictionary;
    private PrintWriter log;
    private WordleGame game;
    
    @BeforeEach
    void setUp() {
        List<String> words = Arrays.asList("слово", "тест", "игра", "герой", "гонец", "абаза", "аббат");
        dictionary = new WordleDictionary(words);
        log = new PrintWriter(new StringWriter());
        game = new WordleGame(dictionary, log);
    }
    
    @Test
    void testInitialState() {
        assertEquals(6, game.getRemainingAttempts());
        assertFalse(game.isWon());
        assertFalse(game.isGameOver());
        assertNotNull(game.getTargetWord());
        assertEquals(5, game.getTargetWord().length());
    }
    
    @Test
    void testValidateWordCorrect() throws Exception {
        // Не должно выбрасывать исключение
        assertDoesNotThrow(() -> game.validateWord("слово"));
    }
    
    @Test
    void testValidateWordEmpty() {
        assertThrows(InvalidWordException.class, () -> game.validateWord(""));
        assertThrows(InvalidWordException.class, () -> game.validateWord("   "));
    }
    
    @Test
    void testValidateWordWrongLength() {
        assertThrows(InvalidWordException.class, () -> game.validateWord("код"));
        assertThrows(InvalidWordException.class, () -> game.validateWord("длинноеслово"));
    }
    
    @Test
    void testValidateWordNotRussian() {
        assertThrows(InvalidWordException.class, () -> game.validateWord("word"));
        assertThrows(InvalidWordException.class, () -> game.validateWord("слово123"));
    }
    
    @Test
    void testValidateWordNotInDictionary() {
        assertThrows(WordNotFoundInDictionaryException.class, () -> game.validateWord("нет"));
    }
    
    @Test
    void testMakeMoveCorrectGuess() throws Exception {
        String targetWord = game.getTargetWord();
        String result = game.makeMove(targetWord);
        
        assertEquals("+++++", result);
        assertTrue(game.isWon());
        assertEquals(5, game.getRemainingAttempts());
    }
    
    @Test
    void testMakeMoveWrongGuess() throws Exception {
        String targetWord = game.getTargetWord();
        String wrongWord = "слово".equals(targetWord) ? "тест" : "слово";
        
        String result = game.makeMove(wrongWord);
        
        assertNotNull(result);
        assertEquals(5, game.getRemainingAttempts());
        assertFalse(game.isWon());
    }
    
    @Test
    void testMakeMoveDecreasesAttempts() throws Exception {
        int initialAttempts = game.getRemainingAttempts();
        game.makeMove("слово");
        
        assertEquals(initialAttempts - 1, game.getRemainingAttempts());
    }
    
    @Test
    void testGameOverAfterSixAttempts() throws Exception {
        String targetWord = game.getTargetWord();
        String wrongWord = "слово".equals(targetWord) ? "тест" : "слово";
        
        for (int i = 0; i < 6; i++) {
            if (i == 5 && wrongWord.equals(targetWord)) {
                // Если случайно угадали на последней попытке
                wrongWord = "игра";
            }
            game.makeMove(wrongWord);
        }
        
        assertTrue(game.isGameOver());
    }
    
    @Test
    void testGetHint() {
        String hint = game.getHint();
        assertNotNull(hint);
        assertEquals(5, hint.length());
        assertTrue(dictionary.contains(hint));
    }
    
    @Test
    void testGetHintAfterMoves() throws Exception {
        String targetWord = game.getTargetWord();
        String wrongWord = "слово".equals(targetWord) ? "тест" : "слово";
        
        game.makeMove(wrongWord);
        String hint = game.getHint();
        
        assertNotNull(hint);
        assertNotEquals(wrongWord, hint); // Подсказка не должна быть уже введённым словом
    }
    
    @Test
    void testMakeMoveAfterGameOver() throws Exception {
        String targetWord = game.getTargetWord();
        game.makeMove(targetWord);
        
        assertThrows(RuntimeException.class, () -> game.makeMove("тест"));
    }
}

