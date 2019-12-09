package com.deeaae.wallet.core;

import com.deeaae.wallet.core.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.annotation.Testable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.UUID;

@SpringBootTest
public class UserWalletManagerTest {

    @Autowired
    private UserWalletManager userWalletManager;
    private String accountId = "Acnt001";
    private String authorId = "RRB";

    @BeforeEach
    void initTestCase() {
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

    private RollbackTransactionRequest createRollbackRequest(WalletTransaction parentTransaction) {
        RollbackTransactionRequest rollbackTransactionRequest = RollbackTransactionRequest.builder()
                .transactionIdToBeRolledBack(parentTransaction.getTransactionId())
                .shortDescription("refund")
                .author(authorId)
                .accountId(accountId)
                .build();
        return rollbackTransactionRequest;
    }

    private WalletTransaction createCreditTransaction(int amount) {
        WalletTransaction walletTransaction = userWalletManager.credit(createTransactionRequest(new BigDecimal(amount),TransactionType.CREDIT));
        return walletTransaction;
    }

    @Test
    void testBalance() {
        String testAccountId = UUID.randomUUID().toString();
        BigDecimal startBalance = userWalletManager.getBalance(testAccountId);
        Assertions.assertEquals(userWalletManager.getBalance(testAccountId).intValue(),0);
        TransactionRequest transactionRequest = createTransactionRequest(new BigDecimal(10),TransactionType.CREDIT);
        transactionRequest.setAccountId(testAccountId);
        userWalletManager.credit(transactionRequest);

        startBalance = userWalletManager.getBalance(testAccountId);
        Assertions.assertEquals(userWalletManager.getBalance(testAccountId).intValue(),10);

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
        userWalletManager.debit(createTransactionRequest(new BigDecimal(sumToBeDeducted), TransactionType.DEBIT));
        BigDecimal endBalance = startBalance.subtract(new BigDecimal(sumToBeDeducted));
        Assertions.assertEquals(userWalletManager.getBalance(accountId),endBalance);


        Assertions.assertThrows(Exception.class,
                ()->userWalletManager.debit(createTransactionRequest(new BigDecimal(50000),
                        TransactionType.DEBIT)));

    }



    @Test
    void testRefund() {
        // Add money
        WalletTransaction walletTransaction = createCreditTransaction(200);
        walletTransaction = userWalletManager.debit(createTransactionRequest(new BigDecimal(100), TransactionType.DEBIT));

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

        final WalletTransaction walletTransaction1 = walletTransaction;
        Assertions.assertThrows(Exception.class, ()->userWalletManager.refund(createRefundRequest(30, walletTransaction1)));
    }

    @Test
    void testRollback() {
        WalletTransaction walletTransaction = createCreditTransaction(200);
        BigDecimal startBalance = userWalletManager.getBalance(accountId);
        walletTransaction = userWalletManager.debit(createTransactionRequest(new BigDecimal(100), TransactionType.DEBIT));
        RollbackTransactionRequest rollbackTransactionRequest = createRollbackRequest(walletTransaction);
        userWalletManager.rollBack(rollbackTransactionRequest);
        BigDecimal endBalance = userWalletManager.getBalance(accountId);
        Assertions.assertEquals(endBalance, startBalance);
        WalletTransaction walletTransaction1 = walletTransaction;
        Assertions.assertThrows(Exception.class, ()->userWalletManager.rollBack(createRollbackRequest(walletTransaction1)));


        // Add money
        // roll back same amount again, this should fail.



    }
}
