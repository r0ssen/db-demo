package dk.rossen.dbdemo.exception;


public class CustomerAlreadyExistsException extends RuntimeException {
    public CustomerAlreadyExistsException(String message) {
        super(message);
    }
    public CustomerAlreadyExistsException() {
        super("Customer already exists");
    }
}
