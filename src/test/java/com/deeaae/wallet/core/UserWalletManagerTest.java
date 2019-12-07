package com.deeaae.wallet.core;

import com.deeaae.wallet.core.model.RefundTransactionRequest;
import com.deeaae.wallet.core.model.TransactionRequest;
import com.deeaae.wallet.core.model.TransactionType;
import com.deeaae.wallet.core.model.WalletTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.UUID;

public class UserWalletManagerTest {

    private UserWalletManager userWalletManager;
    private String accountId = "Acnt001";
    private String authorId = "RRB";

    @BeforeEach
    void initTestCase() {
        userWalletManager = new UserWalletManagerImpl();
        userWalletManager.credit(createTransactionRequest(new BigDecimal(10), TransactionType.CREDIT));
    }

    private TransactionRequest createTransactionRequest(BigDecimal amount, TransactionType transactionType) {

        TransactionRequest transactionRequest = TransactionRequest.builder()
                .accountId(accountId)
                .amount(amount)
                .transactionType(transactionType)
                .authorId(authorId)
                .shortDescription("test transaction ")
                .transactionId(UUID.randomUUID().toString())
                .build();
        return transactionRequest;

    }

    @Test
    void testBalance() {
        BigDecimal startBalance = userWalletManager.getBalance(accountId);
        Assertions.assertEquals(userWalletManager.getBalance(accountId).intValue(),10);
    }

    @Test
    void testCredit() {
        BigDecimal startBalance = userWalletManager.getBalance(accountId);
        int sumToBeDeplosited = 100;
        userWalletManager.credit(createTransactionRequest(new BigDecimal(sumToBeDeplosited), TransactionType.CREDIT));
        BigDecimal endBalance = startBalance.add(new BigDecimal(sumToBeDeplosited));
        Assertions.assertEquals(userWalletManager.getBalance(accountId),endBalance);
    }

    @Test
    void testDebit() {
        BigDecimal startBalance = userWalletManager.getBalance(accountId);
        int sumToBeDeducted = 5;
        userWalletManager.credit(createTransactionRequest(new BigDecimal(sumToBeDeducted), TransactionType.DEBIT));
        BigDecimal endBalance = startBalance.subtract(new BigDecimal(sumToBeDeducted));
        Assertions.assertEquals(userWalletManager.getBalance(accountId),endBalance);
    }

    private WalletTransaction createCreditTransaction(int amount) {
        WalletTransaction walletTransaction = userWalletManager.credit(createTransactionRequest(new BigDecimal(amount),TransactionType.CREDIT));
        return walletTransaction;
    }

    private RefundTransactionRequest createRefundRequest(int amount, WalletTransaction parentTransaction) {
        RefundTransactionRequest refundTransactionRequest = RefundTransactionRequest.builder()
                .parentTransactionId(parentTransaction.getTransactionId())
                .transactionId(UUID.randomUUID().toString())
                .amount(new BigDecimal(amount))
                .shortDescription("refund")
                .author(authorId)
                .accountId(accountId)
                .build();
        return refundTransactionRequest;
    }

    @Test
    void testRefund() {
        // Add money
        WalletTransaction walletTransaction = createCreditTransaction(100);

        RefundTransactionRequest refundTransactionRequest = createRefundRequest(60, walletTransaction);
        BigDecimal startBalance = userWalletManager.getBalance(accountId);
        WalletTransaction refundTransaction =  userWalletManager.refund(refundTransactionRequest);
        BigDecimal endBalance = userWalletManager.getBalance(accountId);
        Assertions.assertEquals(endBalance, startBalance.add(new BigDecimal(60)));

        refundTransactionRequest = createRefundRequest(30, walletTransaction);
        startBalance = userWalletManager.getBalance(accountId);
        refundTransaction =  userWalletManager.refund(refundTransactionRequest);
        endBalance = userWalletManager.getBalance(accountId);
        Assertions.assertEquals(endBalance, startBalance.add(new BigDecimal(30)));

        Assertions.assertThrows(Exception.class, ()->userWalletManager.refund(createRefundRequest(30, walletTransaction)));
    }

    @Test
    void testRollback() {
        // Add money
        // roll back same amount

        // Add money
        // roll back same amount again, this should fail.



    }
}
