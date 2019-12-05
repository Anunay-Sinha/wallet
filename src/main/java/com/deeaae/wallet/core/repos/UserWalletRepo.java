package com.deeaae.wallet.core.repos;


import com.deeaae.wallet.core.model.WalletTransaction;
import org.springframework.data.repository.CrudRepository;

public interface UserWalletRepo extends CrudRepository<WalletTransaction,String> {

}
