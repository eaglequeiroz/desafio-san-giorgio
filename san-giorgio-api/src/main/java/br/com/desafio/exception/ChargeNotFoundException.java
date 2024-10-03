package br.com.desafio.exception;

public class ChargeNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Charge not found";
    public ChargeNotFoundException() {
        super(MESSAGE);
    }
}
