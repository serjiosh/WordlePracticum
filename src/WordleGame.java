import java.io.PrintWriter;
import java.util.*;

/**
 * Класс для управления игровым процессом Wordle
 */
public class WordleGame {
    private final String targetWord;
    private final WordleDictionary dictionary;
    private final PrintWriter log;
    private int remainingAttempts;
    private boolean isWon;
    private final List<String> enteredWords;
    private final List<String> hints;
    
    // Состояние игры для подсказок
    private final Map<Integer, Character> correctPositions; // позиция -> буква
    private final Map<Integer, Set<Character>> wrongPositions; // позиция -> Set букв, которых там быть не должно
    private final Set<Character> presentLetters; // буквы, которые есть в слове
    private final Set<Character> absentLetters; // буквы, которых нет в слове
    
    /**
     * Создаёт новую игру
     * @param dictionary словарь игры
     * @param log логгер для записи системных сообщений
     */
    public WordleGame(WordleDictionary dictionary, PrintWriter log) {
        this.dictionary = dictionary;
        this.log = log;
        this.remainingAttempts = 6;
        this.isWon = false;
        this.enteredWords = new ArrayList<>();
        this.hints = new ArrayList<>();
        
        this.correctPositions = new HashMap<>();
        this.wrongPositions = new HashMap<>();
        this.presentLetters = new HashSet<>();
        this.absentLetters = new HashSet<>();
        
        // Выбираем случайное слово из словаря
        List<String> fiveLetterWords = dictionary.getFiveLetterWords();
        if (fiveLetterWords.isEmpty()) {
            throw new RuntimeException("В словаре нет слов из 5 букв");
        }
        Random random = new Random();
        this.targetWord = fiveLetterWords.get(random.nextInt(fiveLetterWords.size()));
        
        log.println("Загаданное слово: " + targetWord);
    }
    
    /**
     * Получает загаданное слово (для отладки и финального вывода)
     * @return загаданное слово
     */
    public String getTargetWord() {
        return targetWord;
    }
    
    /**
     * Получает количество оставшихся попыток
     * @return количество попыток
     */
    public int getRemainingAttempts() {
        return remainingAttempts;
    }
    
    /**
     * Проверяет, выиграл ли игрок
     * @return true если игрок выиграл
     */
    public boolean isWon() {
        return isWon;
    }
    
    /**
     * Проверяет, закончилась ли игра
     * @return true если игра закончилась
     */
    public boolean isGameOver() {
        return isWon || remainingAttempts <= 0;
    }
    
    /**
     * Валидирует слово перед использованием
     * @param word слово для проверки
     * @throws InvalidWordException если слово не соответствует правилам
     * @throws WordNotFoundInDictionaryException если слова нет в словаре
     */
    public void validateWord(String word) throws InvalidWordException, WordNotFoundInDictionaryException {
        String normalized = WordleDictionary.normalizeWord(word);
        
        if (normalized.isEmpty()) {
            throw new InvalidWordException("Слово не может быть пустым");
        }
        
        if (!WordleDictionary.isValidLength(normalized)) {
            throw new InvalidWordException("Слово должно состоять из 5 букв");
        }
        
        if (!WordleDictionary.containsOnlyRussianLetters(normalized)) {
            throw new InvalidWordException("Слово должно содержать только русские буквы");
        }
        
        if (!dictionary.contains(normalized)) {
            throw new WordNotFoundInDictionaryException("Слова '" + normalized + "' нет в словаре");
        }
    }
    
    /**
     * Делает ход в игре
     * @param guess слово, которое ввёл игрок
     * @return строка с подсказкой (символы +, ^, -)
     * @throws InvalidWordException если слово не соответствует правилам
     * @throws WordNotFoundInDictionaryException если слова нет в словаре
     */
    public String makeMove(String guess) throws InvalidWordException, WordNotFoundInDictionaryException {
        if (isGameOver()) {
            throw new RuntimeException("Игра уже закончилась");
        }
        
        String normalized = WordleDictionary.normalizeWord(guess);
        
        // Проверяем, не отгадал ли игрок слово сразу
        if (normalized.equals(targetWord)) {
            isWon = true;
            remainingAttempts--;
            enteredWords.add(normalized);
            return "+++++";
        }
        
        // Валидируем слово
        validateWord(normalized);
        
        // Уменьшаем количество попыток
        remainingAttempts--;
        enteredWords.add(normalized);
        
        // Получаем подсказку
        String hint = WordleDictionary.compareWords(normalized, targetWord);
        hints.add(hint);
        
        // Обновляем состояние для подсказок
        updateGameState(normalized, hint);
        
        log.println("Ход " + (6 - remainingAttempts) + ": введено слово '" + normalized + "', подсказка: " + hint);
        
        return hint;
    }
    
    /**
     * Обновляет состояние игры на основе подсказки
     */
    private void updateGameState(String word, String hint) {
        char[] wordChars = word.toCharArray();
        
        for (int i = 0; i < hint.length(); i++) {
            char hintChar = hint.charAt(i);
            char letter = wordChars[i];
            
            if (hintChar == '+') {
                // Правильная позиция
                correctPositions.put(i, letter);
                presentLetters.add(letter);
            } else if (hintChar == '^') {
                // Буква есть, но не на этой позиции
                presentLetters.add(letter);
                wrongPositions.computeIfAbsent(i, k -> new HashSet<>()).add(letter);
            } else if (hintChar == '-') {
                // Буквы нет в слове
                // Но нужно проверить, может быть эта буква уже есть на другой позиции
                boolean letterPresent = false;
                for (int j = 0; j < wordChars.length; j++) {
                    if (j != i && wordChars[j] == letter && hint.charAt(j) != '-') {
                        letterPresent = true;
                        break;
                    }
                }
                if (!letterPresent) {
                    absentLetters.add(letter);
                }
            }
        }
    }
    
    /**
     * Получает подсказку - подходящее слово из словаря
     * @return подходящее слово или null если подходящих слов нет
     */
    public String getHint() {
        List<String> suitableWords = dictionary.filterWords(
                correctPositions,
                wrongPositions,
                presentLetters,
                absentLetters
        );
        
        // Убираем уже введённые слова
        suitableWords.removeAll(enteredWords);
        
        // Убираем уже предложенные подсказки
        suitableWords.removeAll(hints);
        
        if (suitableWords.isEmpty()) {
            log.println("Подходящих слов не найдено");
            return null;
        }
        
        // Выбираем случайное слово из подходящих
        Random random = new Random();
        String hint = suitableWords.get(random.nextInt(suitableWords.size()));
        hints.add(hint);
        
        log.println("Подсказка: " + hint);
        return hint;
    }
}

