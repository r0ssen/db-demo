package dk.rossen.dbdemo.exception;


public class AccountAlreadyExistsException extends RuntimeException {
    public AccountAlreadyExistsException(String message) {
        super(message);
    }
    public AccountAlreadyExistsException() {
        super("Account already exists");
    }
}
