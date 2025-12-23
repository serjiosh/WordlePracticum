/**
 * Исключение, возникающее когда слово не соответствует правилам игры
 * (не состоит из 5 букв, содержит недопустимые символы и т.д.)
 */
public class InvalidWordException extends Exception {
    public InvalidWordException(String message) {
        super(message);
    }
}

