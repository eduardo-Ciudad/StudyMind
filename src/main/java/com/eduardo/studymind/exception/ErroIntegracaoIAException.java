package com.eduardo.studymind.exception;

public class ErroIntegracaoIAException extends RuntimeException {
    public ErroIntegracaoIAException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
