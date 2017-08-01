package warpi.service;

public class MalformedCsvException extends RuntimeException {

    public MalformedCsvException(String message) {
        super(message);
    }

    public MalformedCsvException(String message, Exception cause) {
        super(message, cause);
    }
}
