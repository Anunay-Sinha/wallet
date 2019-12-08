package com.deeaae.wallet.test;

import com.deeaae.wallet.core.UserWalletManager;
import com.deeaae.wallet.core.model.TransactionRequest;
import com.deeaae.wallet.core.model.TransactionType;
import com.deeaae.wallet.core.model.WalletTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
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
                .amount(new BigDecimal(20))
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
                .amount(new BigDecimal(10))
                .authorId("RRA")
                .shortDescription("test")
                .build();
        WalletTransaction walletTransaction = userWalletManager.debit(transactionRequest);
        return walletTransaction;

    }
}
