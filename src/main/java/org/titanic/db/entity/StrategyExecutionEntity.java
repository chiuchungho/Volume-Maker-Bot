package org.titanic.db.entity;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

/**
 * @author Hanno Skowronek
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "strategyExecution", schema = "strategy")
public class StrategyExecutionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private Instant startTime;

    @Column
    private Instant finishTime;

    @Column
    private double duration;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "strategy_id")
    private StrategyEntity strategy;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "strategy_account", joinColumns = @JoinColumn(name = "execution_id"), inverseJoinColumns = @JoinColumn(name = "account_id"))
    private List<AccountEntity> accounts;
}
