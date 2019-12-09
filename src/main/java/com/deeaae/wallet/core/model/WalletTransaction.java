package com.deeaae.wallet.core.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class WalletTransaction {
    @Id
    String transactionId;
    String accountId;
    BigDecimal amount;
    @Enumerated(EnumType.STRING)
    TransactionType transactionType;
    BigDecimal netBalance;
    LocalDateTime timestamp;
    String author;
    String shortDescription;
    @Version
    private long version;

}
