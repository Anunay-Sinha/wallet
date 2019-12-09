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
public class RefundNRollbackTransaction {
    @Id
    String transactionId;
    String parentTransactionId;
    String accountId;
    BigDecimal amount;
    @Enumerated(EnumType.STRING)
    RefundType refundType;
    LocalDateTime timestamp;
    String author;
    String shortDescription;
    @Version
    private long version;

}
