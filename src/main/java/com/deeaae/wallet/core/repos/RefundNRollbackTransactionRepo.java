package com.deeaae.wallet.core.repos;

import com.deeaae.wallet.core.model.RefundNRollbackTransaction;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.LockModeType;
import java.util.List;

public interface RefundNRollbackTransactionRepo extends CrudRepository<RefundNRollbackTransaction, String> {

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("SELECT r FROM RefundNRollbackTransaction r WHERE r.parentTransactionId =  ?1 ORDER BY r.timestamp")
    public List<RefundNRollbackTransaction> getRefundTransactionsFor(String parentTransactionId);
}
