package com.banking.moneytransferapi;

import com.banking.moneytransferapi.entity.BankAccountDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TestH2Repo extends JpaRepository<BankAccountDetails, String> {
    Optional<BankAccountDetails> findByCustomerId(String customerId);
    Optional<BankAccountDetails> findByAccountNumber(String accountNumber);
}
