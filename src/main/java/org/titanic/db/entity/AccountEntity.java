package org.titanic.db.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
@Table(name = "account", schema = "strategy")
public class AccountEntity {
    @Id
    private String userId;

    @Column
    private String apiKey;

    @Column
    private String signature;

    @Column
    private String nonce;

    @Column
    private String accountName;
}
