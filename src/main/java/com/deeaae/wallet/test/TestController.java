package com.deeaae.wallet.test;

import com.deeaae.wallet.core.UserWalletManager;
import com.deeaae.wallet.core.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    UserWalletManager userWalletManager;
    @RequestMapping(value = "/credit", method = RequestMethod.GET)
    public WalletTransaction credit() {
        TransactionRequest transactionRequest = TransactionRequest.builder()
                .transactionId(UUID.randomUUID().toString())
                .accountId("Accnt002")
                .transactionType(TransactionType.CREDIT)
                .amount(new BigDecimal(200))
                .authorId("RRA")
                .shortDescription("test")
                .build();
        WalletTransaction walletTransaction = userWalletManager.credit(transactionRequest);
        return walletTransaction;

    }

    @RequestMapping(value = "/debit", method = RequestMethod.GET)
    public WalletTransaction debit() {
        TransactionRequest transactionRequest = TransactionRequest.builder()
                .transactionId(UUID.randomUUID().toString())
                .accountId("Accnt002")
                .transactionType(TransactionType.DEBIT)
                .amount(new BigDecimal(100))
                .authorId("RRA")
                .shortDescription("test")
                .build();
        WalletTransaction walletTransaction = userWalletManager.debit(transactionRequest);
        return walletTransaction;

    }

    @RequestMapping(value = "/refund", method = RequestMethod.GET)
    public List<WalletTransaction> refund(@RequestParam String transactionId) {
        List<WalletTransaction> walletTransactions = new ArrayList<>();
        /*TransactionRequest transactionRequest = TransactionRequest.builder()
                .transactionId(UUID.randomUUID().toString())
                .accountId("Accnt002")
                .transactionType(TransactionType.DEBIT)
                .amount(new BigDecimal(20))
                .authorId("RRA")
                .shortDescription("test")
                .build();
        WalletTransaction walletTransaction = userWalletManager.debit(transactionRequest);
        walletTransactions.add(walletTransaction);*/

        WalletTransaction walletTransaction = new WalletTransaction();
        walletTransaction.setTransactionId(transactionId);
        RefundTransactionRequest refundTransactionRequest = RefundTransactionRequest.builder()
                .transactionId(UUID.randomUUID().toString())
                .parentTransactionId(walletTransaction.getTransactionId())
                .accountId("Accnt002")
                .amount(new BigDecimal(10))
                .author("RRA")
                .shortDescription("refund")
                .build();
        WalletTransaction refundWalletTransaction = userWalletManager.refund(refundTransactionRequest);
        walletTransactions.add(refundWalletTransaction);

        /*refundTransactionRequest = RefundTransactionRequest.builder()
                .transactionId(UUID.randomUUID().toString())
                .parentTransactionId(walletTransaction.getTransactionId())
                .accountId("Accnt002")
                .amount(new BigDecimal(5))
                .author("RRA")
                .shortDescription("refund")
                .build();
        refundWalletTransaction = userWalletManager.refund(refundTransactionRequest);
        walletTransactions.add(refundWalletTransaction);

        try{
            refundTransactionRequest = RefundTransactionRequest.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .parentTransactionId(walletTransaction.getTransactionId())
                    .accountId("Accnt002")
                    .amount(new BigDecimal(15))
                    .author("RRA")
                    .shortDescription("refund")
                    .build();
            refundWalletTransaction = userWalletManager.refund(refundTransactionRequest);
            walletTransactions.add(refundWalletTransaction);


        } catch (Exception ex) {
            ex.printStackTrace();
            walletTransactions.add(null);
        }
        refundTransactionRequest = RefundTransactionRequest.builder()
                .transactionId(UUID.randomUUID().toString())
                .parentTransactionId(walletTransaction.getTransactionId())
                .accountId("Accnt002")
                .amount(new BigDecimal(10))
                .author("RRA")
                .shortDescription("refund")
                .build();
        refundWalletTransaction = userWalletManager.refund(refundTransactionRequest);
        walletTransactions.add(refundWalletTransaction);*/
        return walletTransactions;


    }

    @RequestMapping(value = "/rollback", method = RequestMethod.GET)
    public List<WalletTransaction> rollback() {
        List<WalletTransaction> walletTransactions = new ArrayList<>();
        TransactionRequest transactionRequest = TransactionRequest.builder()
                .transactionId(UUID.randomUUID().toString())
                .accountId("Accnt002")
                .transactionType(TransactionType.DEBIT)
                .amount(new BigDecimal(20))
                .authorId("RRA")
                .shortDescription("test")
                .build();
        WalletTransaction walletTransaction = userWalletManager.debit(transactionRequest);
        walletTransactions.add(walletTransaction);

        RollbackTransactionRequest rollbackTransactionRequest = RollbackTransactionRequest.builder()
                .transactionIdToBeRolledBack(walletTransaction.getTransactionId())
                .accountId("Accnt002")
                .author("RRA")
                .shortDescription("rollback")
                .build();
        WalletTransaction rollbackTransaction = userWalletManager.rollBack(rollbackTransactionRequest);
        walletTransactions.add(rollbackTransaction);



        try{
             rollbackTransactionRequest = RollbackTransactionRequest.builder()
                    .transactionIdToBeRolledBack(walletTransaction.getTransactionId())
                    .accountId("Accnt002")
                    .author("RRA")
                    .shortDescription("rollback")
                    .build();
            rollbackTransaction = userWalletManager.rollBack(rollbackTransactionRequest);
            walletTransactions.add(rollbackTransaction);


        } catch (Exception ex) {
            ex.printStackTrace();
            walletTransactions.add(null);
        }


        return walletTransactions;


    }
}
