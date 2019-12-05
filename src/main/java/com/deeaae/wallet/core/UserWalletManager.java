package com.deeaae.wallet.core;

import com.deeaae.wallet.core.model.RefundTransactionRequest;
import com.deeaae.wallet.core.model.RollbackTransactionRequest;
import com.deeaae.wallet.core.model.TransactionRequest;
import com.deeaae.wallet.core.model.WalletTransaction;

import java.math.BigDecimal;

public interface UserWalletManager {
    public BigDecimal getBalance(String accountId);
    public WalletTransaction credit(TransactionRequest creditTransactionRequest);
    public WalletTransaction debit(TransactionRequest debitTransactionRequest);
    public WalletTransaction rollBack(RollbackTransactionRequest rollbackTransactionRequest);
    public WalletTransaction refund(RefundTransactionRequest refundTransactionRequest);
}
