package ws_wrapper.mione.exception;

public class PublicDataException extends RuntimeException {
    private final String errorCode;  // API 에러 코드

    public PublicDataException(String message) {
        super(message);
        this.errorCode = "UNKNOWN";
    }

    public PublicDataException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public PublicDataException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "UNKNOWN";
    }

    public PublicDataException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
