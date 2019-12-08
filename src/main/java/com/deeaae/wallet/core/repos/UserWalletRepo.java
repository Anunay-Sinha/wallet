package com.deeaae.wallet.core.repos;


import com.deeaae.wallet.core.model.WalletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface UserWalletRepo extends CrudRepository<WalletTransaction,String> {

    List<WalletTransaction> getWalletTransactionByAccountId(String accountId);

    @Query(value = "SELECT wt FROM WalletTransaction wt WHERE wt.accountId = ?1 ORDER BY wt.timestamp DESC")
    Page<WalletTransaction> getLatestTransactoionByAccountId(String accountId, Pageable pageable);

}
