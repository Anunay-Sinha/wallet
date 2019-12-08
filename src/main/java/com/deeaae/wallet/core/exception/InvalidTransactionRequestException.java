package com.deeaae.wallet.core.exception;

public class InvalidTransactionRequestException extends CoreWalletException {
    public InvalidTransactionRequestException(){
        super();
    }

    public InvalidTransactionRequestException(String message) {
        super(message);
    }

    public InvalidTransactionRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
