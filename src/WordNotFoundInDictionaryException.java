/**
 * Исключение, возникающее когда пользователь ввёл слово, которого нет в словаре
 */
public class WordNotFoundInDictionaryException extends Exception {
    public WordNotFoundInDictionaryException(String message) {
        super(message);
    }
}

