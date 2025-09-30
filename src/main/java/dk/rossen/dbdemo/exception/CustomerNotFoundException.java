package dk.rossen.dbdemo.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String message) {
        super(message);

    }
    public CustomerNotFoundException() {
        super("Customer not found");
    }
}
