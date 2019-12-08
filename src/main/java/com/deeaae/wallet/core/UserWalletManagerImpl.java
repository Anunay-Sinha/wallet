package com.deeaae.wallet.core;

import com.deeaae.wallet.core.exception.CoreWalletException;
import com.deeaae.wallet.core.exception.InvalidTransactionRequestException;
import com.deeaae.wallet.core.model.*;
import com.deeaae.wallet.core.repos.UserWalletRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserWalletManagerImpl implements UserWalletManager {

    private UserWalletRepo userWalletRepo;

    @Autowired
    public UserWalletManagerImpl(UserWalletRepo userWalletRepo) {
        this.userWalletRepo = userWalletRepo;
    }



    @Override
    public BigDecimal getBalance(String accountId) {
        log.info("fetching latest record for accont {}", accountId);
        Pageable pageable = PageRequest.of(0,1);
        Page<WalletTransaction> walletTransactions = userWalletRepo.getLatestTransactoionByAccountId(accountId,
                pageable);
        if(!walletTransactions.getContent().isEmpty()) {
            BigDecimal balance = walletTransactions.getContent().get(0).getNetBalance();
            if (balance!= null) {
                log.info("balance for accountId {} is {}", accountId, balance);
                return balance;
            }
        }
        log.info("balance not found for accountid {}", accountId);
        return new BigDecimal(0);
    }

    private boolean validateTransactionRequest(TransactionRequest transactionRequest, List errorList) {
        Boolean isValid = true;
        BigDecimal amount = transactionRequest.getAmount();
        if(transactionRequest == null) {
            String err = "Transaction request is null";
            errorList.add(err);
            isValid = false;
            return false;
        }

        if(amount==null || amount.doubleValue() < 0) {
            String err = "Amount is either null or negative";
            errorList.add(err);
            isValid = false;
        }

        if(transactionRequest.getAccountId()==null || transactionRequest.getAccountId().isEmpty()) {
            String err = "Account Id cannot be null or empty";
            errorList.add(err);
            isValid = false;
        }

        return isValid;
    }

    private boolean validateCreditTransactionRequest(TransactionRequest transactionRequest, List errorList) {
        Boolean isValid = true;
        isValid = validateTransactionRequest(transactionRequest, errorList);
        if(transactionRequest.getTransactionType()!= TransactionType.CREDIT) {
            String err = "Transaction type has to be credit.";
            errorList.add(err);
            isValid = false;

        }
        return isValid;

    }

    private boolean validateDebitTransactionRequest(TransactionRequest transactionRequest, List errorList) {
        Boolean isValid = true;
        isValid = validateTransactionRequest(transactionRequest, errorList);
        if(transactionRequest.getTransactionType()!= TransactionType.DEBIT) {
            String err = "Transaction type has to be debit.";
            errorList.add(err);
            isValid = false;

        }
        return isValid;

    }

    private WalletTransaction createWalletTransaction(TransactionRequest transactionRequest, BigDecimal netBalance) {
        WalletTransaction walletTransaction = WalletTransaction.builder()
                .transactionId(transactionRequest.getTransactionId())
                .accountId(transactionRequest.getAccountId())
                .amount(transactionRequest.getAmount())
                .transactionType(transactionRequest.getTransactionType())
                .author(transactionRequest.getAuthorId())
                .timestamp(LocalDateTime.now())
                .shortDescription(transactionRequest.getShortDescription())
                .accountId(transactionRequest.getAccountId())
                .netBalance(netBalance)
                .build();
        return walletTransaction;

    }


    @Override
    public WalletTransaction credit(TransactionRequest creditTransactionRequest) {

        List<String> errorList = new ArrayList<>();
        if (!validateCreditTransactionRequest(creditTransactionRequest, errorList)) {
            StringBuilder errorString = new StringBuilder();
            errorList.stream().forEach(x-> errorString.append(x));
            throw new InvalidTransactionRequestException(errorString.toString());
        }

        try {
            BigDecimal startBalance = getBalance(creditTransactionRequest.getAccountId());
            BigDecimal netBalance = startBalance.add(creditTransactionRequest.getAmount());
            WalletTransaction walletTransaction = createWalletTransaction(creditTransactionRequest, netBalance);

            return userWalletRepo.save(walletTransaction);
        }catch (Exception ex) {
            log.error("error while processing credit for {}. {}", creditTransactionRequest.getAccountId(),
                    creditTransactionRequest);
            throw new CoreWalletException("error while processing credit", ex);
        }
    }

    @Override
    public WalletTransaction debit(TransactionRequest debitTransactionRequest) {
        List<String> errorList = new ArrayList<>();
        if (!validateDebitTransactionRequest(debitTransactionRequest, errorList)) {
            StringBuilder errorString = new StringBuilder();
            errorList.stream().forEach(x-> errorString.append(x));
            throw new InvalidTransactionRequestException(errorString.toString());
        }
        BigDecimal startBalance = getBalance(debitTransactionRequest.getAccountId());
        BigDecimal netBalance = startBalance.subtract(debitTransactionRequest.getAmount());
        if(netBalance.doubleValue() < 0.0) {
            log.error("Not enough balance for in {} wallet for this transaction, Balance {}. WalletTransaction {}",
                    debitTransactionRequest.getAccountId() ,startBalance, debitTransactionRequest);
            throw new CoreWalletException("Not enough balance for this transaction");
        }

        try {

            WalletTransaction walletTransaction = createWalletTransaction(debitTransactionRequest, netBalance);
            return userWalletRepo.save(walletTransaction);
        }catch (Exception ex) {
            log.error("error while processing debit for {}. {}", debitTransactionRequest.getAccountId(),
                    debitTransactionRequest);
            throw new CoreWalletException("error while processing debit", ex);
        }
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
