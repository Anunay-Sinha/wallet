package com.deeaae.wallet.core.exception;

public class CoreWalletException extends WalletException {
    public CoreWalletException(){
        super();
    }

    public CoreWalletException(String message) {
        super(message);
    }

    public CoreWalletException(String message, Throwable cause) {
        super(message, cause);
    }
}
