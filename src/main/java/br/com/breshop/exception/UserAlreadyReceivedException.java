package br.com.breshop.exception;

public class UserAlreadyReceivedException extends RuntimeException {
    public UserAlreadyReceivedException(String message) {
        super(message);
    }
}
