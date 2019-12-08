package com.deeaae.wallet.core;

import com.deeaae.wallet.core.exception.CoreWalletException;
import com.deeaae.wallet.core.exception.InvalidTransactionRequestException;
import com.deeaae.wallet.core.model.*;
import com.deeaae.wallet.core.repos.RefundNRollbackTransactionRepo;
import com.deeaae.wallet.core.repos.UserWalletRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
public class UserWalletManagerImpl implements UserWalletManager {

    private UserWalletRepo userWalletRepo;

    private Random random = new Random(100);

    private RefundNRollbackTransactionRepo refundNRollbackTransactionRepo;

    @Autowired
    public UserWalletManagerImpl(UserWalletRepo userWalletRepo, RefundNRollbackTransactionRepo refundNRollbackTransactionRepo) {
        this.userWalletRepo = userWalletRepo;
        this.refundNRollbackTransactionRepo = refundNRollbackTransactionRepo;
    }



    @Override
    @Transactional
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
    @Transactional
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

            int sleep = 200* random.nextInt(20);
            log.info("Sleep : {}", sleep);
            Thread.sleep( sleep);

            return userWalletRepo.save(walletTransaction);
        }catch (Exception ex) {
            log.error("error while processing credit for {}. {}", creditTransactionRequest.getAccountId(),
                    creditTransactionRequest);
            throw new CoreWalletException("error while processing credit", ex);
        }
    }

    @Override
    @Transactional
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
    @Transactional
    public WalletTransaction rollBack(RollbackTransactionRequest rollbackTransactionRequest) {
        ArrayList<String> errorList = new ArrayList<>();
        if(!validateRollbackTransactionRequest(rollbackTransactionRequest, errorList)) {
            StringBuilder errorString = new StringBuilder();
            errorList.stream().forEach(x-> errorString.append(x));
            throw new InvalidTransactionRequestException(errorString.toString());

        }
        String parentTransactionId = rollbackTransactionRequest.getTransactionIdToBeRolledBack();
        String accountId = rollbackTransactionRequest.getAccountId();
        WalletTransaction parentTransaction = userWalletRepo.getTransactionByTransactionIdAndAccountId(parentTransactionId, accountId);
        List<RuntimeException> exceptions = new ArrayList<>();

        if(!validateIfRefundOrRollbackAllowed(parentTransaction, accountId, parentTransaction.getAmount(),exceptions)) {
            if(!exceptions.isEmpty()) {
                throw exceptions.get(0);
            }
        }

        WalletTransaction rollbackTransaction =  updateWalletForRollback(rollbackTransactionRequest, parentTransaction.getAmount());
        updateRefundNRollbackForRollback(rollbackTransactionRequest,rollbackTransaction.getTransactionId(),rollbackTransaction.getAmount());
        return rollbackTransaction;

    }

    private boolean validateRollbackTransactionRequest(RollbackTransactionRequest rollbackTransactionRequest, List<String> errorList) {
        boolean isValid = true;
        if(rollbackTransactionRequest== null) {
            String err = "rollback request is null";
            errorList.add(err);
            return false;
        }

        if(rollbackTransactionRequest.getAccountId() == null || rollbackTransactionRequest.getAccountId().isEmpty()) {
            String err = "account id is not provided";
            errorList.add(err);
            isValid = false;
        }

        if(rollbackTransactionRequest.getTransactionIdToBeRolledBack() == null || rollbackTransactionRequest.getTransactionIdToBeRolledBack().isEmpty()) {
            String err = "parent transaction id is not provided";
            errorList.add(err);
            isValid = false;
        }


        return isValid;

    }



    private boolean validateRefundTransactionRequest(RefundTransactionRequest refundTransactionRequest, List<String> errorList) {
        boolean isValid = true;
        if(refundTransactionRequest== null) {
            String err = "refund request is null";
            errorList.add(err);
            return false;
        }

        if(refundTransactionRequest.getAccountId() == null || refundTransactionRequest.getAccountId().isEmpty()) {
            String err = "account id is not provided";
            errorList.add(err);
            isValid = false;
        }

        if(refundTransactionRequest.getTransactionId() == null || refundTransactionRequest.getTransactionId().isEmpty()) {
            String err = " transaction id is not provided";
            errorList.add(err);
            isValid = false;
        }

        if(refundTransactionRequest.getParentTransactionId() == null || refundTransactionRequest.getParentTransactionId().isEmpty()) {
            String err = "parent transaction id is not provided";
            errorList.add(err);
            isValid = false;
        }

        if(refundTransactionRequest.getAmount() == null || refundTransactionRequest.getAmount().doubleValue()<=0.0) {
            String err = "amount need to be provided and has to be a positive number";
            errorList.add(err);
            isValid = false;
        }
        return isValid;

    }

    private WalletTransaction updateWalletForRefund(RefundTransactionRequest refundTransactionRequest) {
        TransactionRequest creditTransactionRequest = TransactionRequest.builder()
                .transactionId(refundTransactionRequest.getTransactionId())
                .accountId(refundTransactionRequest.getAccountId())
                .amount(refundTransactionRequest.getAmount())
                .transactionType(TransactionType.CREDIT)
                .authorId(refundTransactionRequest.getAuthor())
                .shortDescription(refundTransactionRequest.getShortDescription())
                .build();
        return credit(creditTransactionRequest);

    }

    private WalletTransaction updateWalletForRollback(RollbackTransactionRequest rollbackTransactionRequest, BigDecimal amount) {
        TransactionRequest creditTransactionRequest = TransactionRequest.builder()
                .transactionId(UUID.randomUUID().toString())
                .accountId(rollbackTransactionRequest.getAccountId())
                .amount(amount)
                .transactionType(TransactionType.CREDIT)
                .authorId(rollbackTransactionRequest.getAuthor())
                .shortDescription(rollbackTransactionRequest.getShortDescription())
                .build();
        return credit(creditTransactionRequest);

    }

    private RefundNRollbackTransaction updateRefundNRollback(RefundNRollbackTransaction refundNRollbackTransaction) {
        return refundNRollbackTransactionRepo.save(refundNRollbackTransaction);
    }

    private RefundNRollbackTransaction updateRefundNRollbackForRefund(RefundTransactionRequest refundTransactionRequest) {
        RefundNRollbackTransaction refundNRollbackTransaction = RefundNRollbackTransaction.builder()
                .parentTransactionId(refundTransactionRequest.getParentTransactionId())
                .refundType(RefundType.REFUND)
                .transactionId(refundTransactionRequest.getTransactionId())
                .accountId(refundTransactionRequest.getAccountId())
                .amount(refundTransactionRequest.getAmount())
                .author(refundTransactionRequest.getAuthor())
                .shortDescription(refundTransactionRequest.getShortDescription())
                .timestamp(LocalDateTime.now())
                .build();
        return updateRefundNRollback(refundNRollbackTransaction);

    }

    private RefundNRollbackTransaction updateRefundNRollbackForRollback(RollbackTransactionRequest rollbackTransactionRequest, String transactionId, BigDecimal amount) {
        RefundNRollbackTransaction refundNRollbackTransaction = RefundNRollbackTransaction.builder()
                .parentTransactionId(rollbackTransactionRequest.getTransactionIdToBeRolledBack())
                .refundType(RefundType.ROLLBACK)
                .transactionId(transactionId)
                .accountId(rollbackTransactionRequest.getAccountId())
                .amount(amount)
                .author(rollbackTransactionRequest.getAuthor())
                .shortDescription(rollbackTransactionRequest.getShortDescription())
                .timestamp(LocalDateTime.now())
                .build();
        return updateRefundNRollback(refundNRollbackTransaction);

    }

    private boolean validateIfRefundOrRollbackAllowed(WalletTransaction parentTransaction, String accountId, BigDecimal amount, List<RuntimeException> exceptions) {

        List<RefundNRollbackTransaction> refundNRollbackTransactions =  refundNRollbackTransactionRepo.getRefundTransactionsFor(parentTransaction.getTransactionId());

        if(parentTransaction == null || parentTransaction.getTransactionType()==TransactionType.CREDIT) {
            exceptions.add(new InvalidTransactionRequestException("Parent transaction id " + parentTransaction.getTransactionId() +
                    ", is not present for the account " + accountId + ". or transaction type is not debit"));
            return false;

        }
        BigDecimal totalNetAmountOnTransaction = parentTransaction.getAmount();
        if(refundNRollbackTransactions!=null) {
            for (RefundNRollbackTransaction rrtr:refundNRollbackTransactions) {
                totalNetAmountOnTransaction = totalNetAmountOnTransaction.subtract(rrtr.getAmount());
            }
        }

        BigDecimal totalAmountPostThisRefund = totalNetAmountOnTransaction.subtract(amount);
        if(totalAmountPostThisRefund.doubleValue()<0.0) {
            exceptions.add(new InvalidTransactionRequestException("Refund bandwidth of "+ totalNetAmountOnTransaction + " breached"));
            return false;
        }
        return true;

    }

    @Override
    @Transactional
    public WalletTransaction refund(RefundTransactionRequest refundTransactionRequest) {
        ArrayList<String> errorList = new ArrayList<>();
        if(!validateRefundTransactionRequest(refundTransactionRequest, errorList)) {
            StringBuilder errorString = new StringBuilder();
            errorList.stream().forEach(x-> errorString.append(x));
            throw new InvalidTransactionRequestException(errorString.toString());

        }
        String parentTransactionId = refundTransactionRequest.getParentTransactionId();
        String accountId = refundTransactionRequest.getAccountId();
        WalletTransaction parentTransaction = userWalletRepo.getTransactionByTransactionIdAndAccountId(parentTransactionId, accountId);
        List<RuntimeException> exceptions =new ArrayList<>();
        if(!validateIfRefundOrRollbackAllowed(parentTransaction, accountId, refundTransactionRequest.getAmount(),exceptions)) {
            if(!exceptions.isEmpty()) {
                throw exceptions.get(0);
            }
        }

        updateRefundNRollbackForRefund(refundTransactionRequest);
        return updateWalletForRefund(refundTransactionRequest);
    }
}
