package com.banking.moneytransferapi.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Bank_Account_Details")
public class BankAccountDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BANK_ACCOUNT_DETAILS_ID")
    private Long bankAccountDetailsId;

    @Column(name = "ACCOUNT_NUMBER",unique = true)
    private String accountNumber;

    @Column(name = "ACCOUNT_HOLDER_NAME")
    private String accountHolderName;

    @Column(name = "ACCOUNT_TYPE")
    private String accountType;

    @Column(name = "IFSC_CODE")
    private String ifscCode;

    @Email
    @Column(name = "EMAIL_ID")
    private String emailId;

    @Column(name = "MOBILE_NUMBER")
    private String mobileNumber;

    @Column(name = "CUSTOMER_ID", unique = true)
    private String customerId;

    @Column(name = "ACCOUNT_BALANCE")
    private Double accountBalance;
}
