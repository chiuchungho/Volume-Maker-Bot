package org.titanic.db.entity;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.Set;

/**
 * @author Hanno Skowronek
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "strategy", schema = "strategy")
public class StrategyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private Instant created;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private double volume;

    @Column(nullable = false)
    private double priceMin;

    @Column(nullable = false)
    private double priceMax;

    @Column(nullable = false)
    private int durationHourMin;

    @Column(nullable = false)
    private int durationHourMax;

    @Column
    private Instant nextExecution;

    @Column
    private Instant lastExecution;

    @Column(nullable = false)
    private String telegramUsername;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private int numberOfTradesMin;

    @Column(nullable = false)
    private int numberOfTradesMax;
}
