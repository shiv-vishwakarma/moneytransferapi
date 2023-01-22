package com.banking.moneytransferapi.service;

import com.banking.moneytransferapi.dto.MoneyTransferRequest;
import com.banking.moneytransferapi.entity.BankAccountDetails;
import com.banking.moneytransferapi.entity.Transactions;
import com.banking.moneytransferapi.exception.MoneyTransferApiException;
import com.banking.moneytransferapi.repository.BankAccountDetailsRepo;
import com.banking.moneytransferapi.repository.TransactionsRecordRepo;
import com.banking.moneytransferapi.util.BusinessValidation;
import com.banking.moneytransferapi.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TransferServiceImpl implements TransferService{

    @Autowired
    private BankAccountDetailsRepo bankAccountDetailsRepo;

    @Autowired
    private TransactionsRecordRepo transactionsRecordRepo;

    @Autowired
    private BusinessValidation businessValidation;

    @Override
    public void startTransactionProcess(MoneyTransferRequest moneyTransferRequest) {
        businessValidation.performBusinessValidations(moneyTransferRequest);
        performFundTransfer(moneyTransferRequest);
    }

    @Override
    public Optional<List<Transactions>> fetchTransactionsByAccountNo(String accountno) {
      return transactionsRecordRepo.findAllByAccountNumber(accountno);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED)
    public void performFundTransfer(MoneyTransferRequest moneyTransferRequest) {
        withdrawAmount(moneyTransferRequest.getFromAccountNumber(), moneyTransferRequest.getAmount());
        depositAmount(moneyTransferRequest.getToAccountNumber(), moneyTransferRequest.getAmount());
    }

    private void withdrawAmount(String fromAccountNumber, Double amount) {
        try {
            BankAccountDetails updatedFromAccount = bankAccountDetailsRepo.findByAccountNumber(fromAccountNumber).get();
            updatedFromAccount.setAccountBalance(updatedFromAccount.getAccountBalance() - amount);
            bankAccountDetailsRepo.save(updatedFromAccount);
            transactionsRecordRepo.save(new Transactions(fromAccountNumber,"DEBIT", amount, new Date(System.currentTimeMillis())));
        } catch (Exception ex) {
            throw new MoneyTransferApiException(Constants.ERROR_MSG_DEBIT_MONEY);
        }
    }

    private void depositAmount(String toAccountNumber, Double amount) {
        try {
            BankAccountDetails updatedToAccount = bankAccountDetailsRepo.findByAccountNumber(toAccountNumber).get();
            updatedToAccount.setAccountBalance(updatedToAccount.getAccountBalance() + amount);
            bankAccountDetailsRepo.save(updatedToAccount);
            transactionsRecordRepo.save(new Transactions(toAccountNumber,"CREDIT", amount, new Date(System.currentTimeMillis())));
        } catch (Exception ex) {
            throw new MoneyTransferApiException(Constants.ERROR_MSG_CREDIT_MONEY);
        }
    }

    @Override
    public Optional<BankAccountDetails> fetchAccountDetails(String customerId) {
        return bankAccountDetailsRepo.findByCustomerId(customerId);
    }

    @Override
    public BankAccountDetails createNewBankAccount(BankAccountDetails bankAccountDetails) {
        return bankAccountDetailsRepo.save(bankAccountDetails);
    }

}
