package com.banking.moneytransferapi.repository;

import com.banking.moneytransferapi.entity.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionsRecordRepo extends JpaRepository<Transactions, String> {

    Optional<List<Transactions>> findAllByAccountNumber(String accountno);
}
