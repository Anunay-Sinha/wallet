package com.deeaae.wallet.core;

import com.deeaae.wallet.core.model.RefundTransactionRequest;
import com.deeaae.wallet.core.model.RollbackTransactionRequest;
import com.deeaae.wallet.core.model.TransactionRequest;
import com.deeaae.wallet.core.model.WalletTransaction;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class UserWalletManagerImpl implements UserWalletManager {

    @Override
    public BigDecimal getBalance(String accountId) {
        return new BigDecimal(10);
    }

    @Override
    public WalletTransaction credit(TransactionRequest creditTransactionRequest) {
        return null;
    }

    @Override
    public WalletTransaction debit(TransactionRequest debitTransactionRequest) {
        return null;
    }

    @Override
    public WalletTransaction rollBack(RollbackTransactionRequest rollbackTransactionRequest) {
        return null;
    }

    @Override
    public WalletTransaction refund(RefundTransactionRequest refundTransactionRequest) {
        return null;
    }
}
