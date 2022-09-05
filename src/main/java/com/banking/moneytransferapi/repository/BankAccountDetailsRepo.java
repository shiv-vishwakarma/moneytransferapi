package com.banking.moneytransferapi.repository;

import com.banking.moneytransferapi.entity.BankAccountDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountDetailsRepo extends JpaRepository<BankAccountDetails, String>{

    Optional<BankAccountDetails> findByCustomerId(String customerId);
    Optional<BankAccountDetails> findByAccountNumber(String accountNumber);
}
