package com.example.bank.exception;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String number) {
        super("Fonds insuffisants sur le compte : " + number);
    }
}
