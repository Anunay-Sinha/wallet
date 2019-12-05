package com.deeaae.wallet.core.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WalletTransaction {
    @Id
    String transactionId;
    String accountId;
    BigDecimal amount;
    TransactionType transactionType;
    BigDecimal netBalance;
    LocalDateTime timestamp;
    String author;
    String shoetDescription;

}
