package com.deeaae.wallet.core.exception;

public class WalletException extends RuntimeException {

    public WalletException(){
        super();
    }

    public WalletException(String message) {
        super(message);
    }

    public WalletException(String message, Throwable cause) {
        super(message, cause);
    }

}
