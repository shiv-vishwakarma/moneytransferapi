package com.banking.moneytransferapi.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "TRANSACTIONS_RECORD")
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRANSACTIONS_ID")
    private Long transactions_id;

    @Column(name = "ACCOUNT_NUMBER")
    private String accountNumber;

    @Column(name = "TRANSACTION_TYPE")
    private String transactionType;

    @Column(name = "AMOUNT")
    private Double amount;

    @Column(name = "TIME")
    private Date time;

    public Transactions(String accountNumber, String transactionType, Double amount, Date time) {
        this.accountNumber = accountNumber;
        this.transactionType = transactionType;
        this.amount = amount;
        this.time = time;
    }
}
