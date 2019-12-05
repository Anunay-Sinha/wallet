package com.deeaae.wallet.core.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionRequest {
    String transactionId;
    String accountId;
    TransactionType transactionType;
    BigDecimal amount;
    String shortDescription;
    String authorId;
}
