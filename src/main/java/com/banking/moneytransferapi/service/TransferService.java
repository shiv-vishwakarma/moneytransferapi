package com.banking.moneytransferapi.service;

import com.banking.moneytransferapi.dto.MoneyTransferRequest;
import com.banking.moneytransferapi.entity.BankAccountDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface TransferService {

    public Optional<BankAccountDetails> fetchAccountDetails(String customerId);

    BankAccountDetails createNewBankAccount(BankAccountDetails bankAccountDetails);

    void startTransactionProcess(MoneyTransferRequest moneyTransferRequest);
}
