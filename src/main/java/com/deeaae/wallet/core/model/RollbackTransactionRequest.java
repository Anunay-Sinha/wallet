package com.deeaae.wallet.core.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class RollbackTransactionRequest {
    String transactionIdToBeRolledBack;
    String accountId;
    String author;
    String shortDescription;
}
