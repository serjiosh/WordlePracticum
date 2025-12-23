import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс для работы со словарём игры Wordle
 */
public class WordleDictionary {
    private final List<String> words;
    
    /**
     * Создаёт словарь из списка слов
     * @param rawWords список слов из файла
     */
    public WordleDictionary(List<String> rawWords) {
        this.words = normalizeWords(rawWords);
    }
    
    /**
     * Нормализует слова: приводит к нижнему регистру и заменяет ё на е
     * @param rawWords исходный список слов
     * @return нормализованный список слов
     */
    private List<String> normalizeWords(List<String> rawWords) {
        return rawWords.stream()
                .map(word -> word.toLowerCase().replace('ё', 'е'))
                .collect(Collectors.toList());
    }
    
    /**
     * Нормализует одно слово
     * @param word исходное слово
     * @return нормализованное слово
     */
    public static String normalizeWord(String word) {
        if (word == null) {
            return "";
        }
        return word.toLowerCase().replace('ё', 'е').trim();
    }
    
    /**
     * Проверяет, что слово состоит из 5 букв
     * @param word слово для проверки
     * @return true если слово состоит из 5 букв
     */
    public static boolean isValidLength(String word) {
        return word != null && word.length() == 5;
    }
    
    /**
     * Проверяет, что слово содержит только русские буквы
     * @param word слово для проверки
     * @return true если слово содержит только русские буквы
     */
    public static boolean containsOnlyRussianLetters(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }
        for (char c : word.toCharArray()) {
            if (!isRussianLetter(c)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Проверяет, является ли символ русской буквой
     * @param c символ для проверки
     * @return true если символ - русская буква
     */
    private static boolean isRussianLetter(char c) {
        return (c >= 'а' && c <= 'я') || (c >= 'А' && c <= 'Я');
    }
    
    /**
     * Проверяет, есть ли слово в словаре
     * @param word слово для проверки
     * @return true если слово есть в словаре
     */
    public boolean contains(String word) {
        String normalized = normalizeWord(word);
        return words.contains(normalized);
    }
    
    /**
     * Получает случайное слово из словаря
     * @return случайное слово
     */
    public String getRandomWord() {
        if (words.isEmpty()) {
            throw new RuntimeException("Словарь пуст");
        }
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }
    
    /**
     * Получает все слова из словаря, состоящие из 5 букв
     * @return список слов из 5 букв
     */
    public List<String> getFiveLetterWords() {
        return words.stream()
                .filter(word -> word.length() == 5)
                .collect(Collectors.toList());
    }
    
    /**
     * Получает все слова словаря
     * @return список всех слов
     */
    public List<String> getAllWords() {
        return new ArrayList<>(words);
    }
    
    /**
     * Сравнивает два слова и возвращает строку с подсказками
     * @param guess слово, которое ввёл игрок
     * @param target загаданное слово
     * @return строка из 5 символов: + (правильная позиция), ^ (есть в слове, но не на месте), - (нет в слове)
     */
    public static String compareWords(String guess, String target) {
        if (guess == null || target == null || guess.length() != target.length()) {
            throw new IllegalArgumentException("Слова должны быть одинаковой длины");
        }
        
        char[] guessChars = guess.toCharArray();
        char[] targetChars = target.toCharArray();
        char[] result = new char[guess.length()];
        
        // Сначала отмечаем правильные позиции (+)
        boolean[] targetUsed = new boolean[target.length()];
        boolean[] guessUsed = new boolean[guess.length()];
        
        // Проверяем точные совпадения
        for (int i = 0; i < guessChars.length; i++) {
            if (guessChars[i] == targetChars[i]) {
                result[i] = '+';
                targetUsed[i] = true;
                guessUsed[i] = true;
            }
        }
        
        // Проверяем буквы, которые есть в слове, но не на правильной позиции (^)
        for (int i = 0; i < guessChars.length; i++) {
            if (result[i] == '\0') { // Ещё не обработано
                for (int j = 0; j < targetChars.length; j++) {
                    if (!targetUsed[j] && guessChars[i] == targetChars[j]) {
                        result[i] = '^';
                        targetUsed[j] = true;
                        guessUsed[i] = true;
                        break;
                    }
                }
            }
        }
        
        // Остальные буквы отмечаем как отсутствующие (-)
        for (int i = 0; i < result.length; i++) {
            if (result[i] == '\0') {
                result[i] = '-';
            }
        }
        
        return new String(result);
    }
    
    /**
     * Фильтрует слова по заданным условиям
     * @param correctPositions буквы на правильных позициях (Map: позиция -> буква)
     * @param wrongPositions буквы на неправильных позициях (Map: позиция -> Set букв)
     * @param presentLetters буквы, которые есть в слове
     * @param absentLetters буквы, которых нет в слове
     * @return список подходящих слов
     */
    public List<String> filterWords(Map<Integer, Character> correctPositions,
                                   Map<Integer, Set<Character>> wrongPositions,
                                   Set<Character> presentLetters,
                                   Set<Character> absentLetters) {
        List<String> fiveLetterWords = getFiveLetterWords();
        List<String> filtered = new ArrayList<>();
        
        for (String word : fiveLetterWords) {
            if (matchesConditions(word, correctPositions, wrongPositions, presentLetters, absentLetters)) {
                filtered.add(word);
            }
        }
        
        return filtered;
    }
    
    /**
     * Проверяет, соответствует ли слово заданным условиям
     */
    private boolean matchesConditions(String word,
                                     Map<Integer, Character> correctPositions,
                                     Map<Integer, Set<Character>> wrongPositions,
                                     Set<Character> presentLetters,
                                     Set<Character> absentLetters) {
        char[] chars = word.toCharArray();
        
        // Проверяем правильные позиции
        for (Map.Entry<Integer, Character> entry : correctPositions.entrySet()) {
            int pos = entry.getKey();
            char letter = entry.getValue();
            if (pos >= chars.length || chars[pos] != letter) {
                return false;
            }
        }
        
        // Проверяем неправильные позиции (буква есть, но не на этой позиции)
        for (Map.Entry<Integer, Set<Character>> entry : wrongPositions.entrySet()) {
            int pos = entry.getKey();
            Set<Character> letters = entry.getValue();
            if (pos < chars.length && letters.contains(chars[pos])) {
                return false;
            }
        }
        
        // Проверяем, что все необходимые буквы присутствуют
        for (char letter : presentLetters) {
            boolean found = false;
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == letter && !correctPositions.containsKey(i)) {
                    // Проверяем, что эта позиция не запрещена
                    Set<Character> wrongAtPos = wrongPositions.get(i);
                    if (wrongAtPos == null || !wrongAtPos.contains(letter)) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                return false;
            }
        }
        
        // Проверяем отсутствующие буквы
        for (char letter : absentLetters) {
            for (char c : chars) {
                if (c == letter) {
                    return false;
                }
            }
        }
        
        return true;
    }
}

