import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

/**
 * Главный класс игры Wordle
 */
public class Wordle {
    private static final String DICTIONARY_FILE = "russian_nouns.txt";
    private static final String LOG_FILE = "wordle.log";
    
    public static void main(String[] args) {
        PrintWriter log = null;
        
        try {
            // Создаём лог-файл
            log = createLogFile();
            
            // Загружаем словарь
            log.println("Загрузка словаря из файла: " + DICTIONARY_FILE);
            List<String> rawWords = WordleDictionaryLoader.loadDictionary(DICTIONARY_FILE);
            
            if (rawWords.isEmpty()) {
                log.println("ОШИБКА: Словарь пуст");
                System.out.println("Ошибка: словарь пуст. Проверьте файл словаря.");
                return;
            }
            
            log.println("Загружено слов: " + rawWords.size());
            
            // Создаём словарь
            WordleDictionary dictionary = new WordleDictionary(rawWords);
            List<String> fiveLetterWords = dictionary.getFiveLetterWords();
            
            if (fiveLetterWords.isEmpty()) {
                log.println("ОШИБКА: В словаре нет слов из 5 букв");
                System.out.println("Ошибка: в словаре нет слов из 5 букв.");
                return;
            }
            
            log.println("Слов из 5 букв: " + fiveLetterWords.size());
            
            // Создаём игру
            WordleGame game = new WordleGame(dictionary, log);
            
            // Запускаем игровой цикл
            playGame(game, log);
            
        } catch (FileNotFoundException e) {
            String errorMsg = "ОШИБКА: Файл словаря не найден: " + DICTIONARY_FILE;
            if (log != null) {
                log.println(errorMsg);
                log.println(e.getMessage());
                e.printStackTrace(log);
            } else {
                System.err.println(errorMsg);
                e.printStackTrace();
            }
        } catch (IOException e) {
            String errorMsg = "ОШИБКА: Ошибка при работе с файлом";
            if (log != null) {
                log.println(errorMsg);
                log.println(e.getMessage());
                e.printStackTrace(log);
            } else {
                System.err.println(errorMsg);
                e.printStackTrace();
            }
        } catch (RuntimeException e) {
            String errorMsg = "ОШИБКА: " + e.getMessage();
            if (log != null) {
                log.println(errorMsg);
                e.printStackTrace(log);
            } else {
                System.err.println(errorMsg);
                e.printStackTrace();
            }
        } catch (Exception e) {
            String errorMsg = "ОШИБКА: Неожиданная ошибка";
            if (log != null) {
                log.println(errorMsg);
                log.println(e.getMessage());
                e.printStackTrace(log);
            } else {
                System.err.println(errorMsg);
                e.printStackTrace();
            }
        } finally {
            if (log != null) {
                log.close();
            }
        }
    }
    
    /**
     * Создаёт лог-файл
     * @return PrintWriter для записи в лог
     * @throws IOException если не удалось создать файл
     */
    private static PrintWriter createLogFile() throws IOException {
        File logFile = new File(LOG_FILE);
        FileWriter fileWriter = new FileWriter(logFile, StandardCharsets.UTF_8);
        return new PrintWriter(fileWriter, true); // autoFlush = true
    }
    
    /**
     * Игровой цикл
     * @param game объект игры
     * @param log логгер
     */
    private static void playGame(WordleGame game, PrintWriter log) {
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        
        System.out.println("Добро пожаловать в игру Wordle!");
        System.out.println("У вас есть 6 попыток, чтобы угадать слово из 5 букв.");
        System.out.println("Введите слово или нажмите Enter для подсказки.");
        System.out.println();
        
        while (!game.isGameOver()) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            
            // Если пустой ввод - даём подсказку
            if (input.isEmpty()) {
                String hint = game.getHint();
                if (hint != null) {
                    System.out.println("Подсказка: " + hint);
                } else {
                    System.out.println("Подходящих слов не найдено.");
                }
                continue;
            }
            
            try {
                // Делаем ход
                String result = game.makeMove(input);
                
                // Выводим результат
                System.out.println("> " + result);
                
                // Проверяем, выиграл ли игрок
                if (game.isWon()) {
                    System.out.println();
                    System.out.println("Поздравляем! Вы угадали слово!");
                    System.out.println("Загаданное слово: " + game.getTargetWord());
                    log.println("Игра завершена: победа игрока");
                    break;
                }
                
                // Показываем оставшиеся попытки
                int remaining = game.getRemainingAttempts();
                if (remaining > 0) {
                    System.out.println("Осталось попыток: " + remaining);
                }
                
            } catch (InvalidWordException e) {
                System.out.println("Ошибка: " + e.getMessage());
                log.println("Некорректный ввод: " + e.getMessage());
            } catch (WordNotFoundInDictionaryException e) {
                System.out.println("Ошибка: " + e.getMessage());
                log.println("Слово не найдено в словаре: " + e.getMessage());
            }
        }
        
        // Если игра закончилась проигрышем
        if (!game.isWon() && game.isGameOver()) {
            System.out.println();
            System.out.println("Игра окончена. Вы не угадали слово.");
            System.out.println("Загаданное слово: " + game.getTargetWord());
            log.println("Игра завершена: проигрыш игрока");
        }
        
        scanner.close();
    }
}

