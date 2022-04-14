package org.titanic.db.entity;

import lombok.*;
import org.titanic.enums.Side;
import org.titanic.enums.TransactionStatus;

import javax.persistence.*;
import java.time.Instant;

/**
 * @author Hanno Skowronek
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction", schema = "strategy")
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private Instant timestamp;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private double volume;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Side side;

    @Column
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column
    private long orderId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_userId")
    private AccountEntity account;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "execution_id")
    private StrategyExecutionEntity execution;
}
