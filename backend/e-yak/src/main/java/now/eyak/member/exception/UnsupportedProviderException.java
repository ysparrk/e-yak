package now.eyak.member.exception;

public class UnsupportedProviderException extends RuntimeException {
    public UnsupportedProviderException() {
    }

    public UnsupportedProviderException(String message) {
        super(message);
    }
}
