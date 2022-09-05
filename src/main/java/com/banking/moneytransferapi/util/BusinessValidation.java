package com.banking.moneytransferapi.util;

import com.banking.moneytransferapi.dto.MoneyTransferRequest;
import com.banking.moneytransferapi.entity.BankAccountDetails;
import com.banking.moneytransferapi.exception.BusinessValidationException;
import com.banking.moneytransferapi.repository.BankAccountDetailsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BusinessValidation {

    @Autowired
    private BankAccountDetailsRepo bankAccountDetailsRepo;

    public void performBusinessValidations(MoneyTransferRequest moneyTransferRequest) {

        //Check if both account numbers are not same
        if(moneyTransferRequest.getFromAccountNumber().equalsIgnoreCase(moneyTransferRequest.getToAccountNumber())){
            throw new BusinessValidationException(Constants.ERROR_MSG_SAME_ACCOUNTS);
        }

        //Check for Sufficient account balance
        Optional<BankAccountDetails> bankAccountDetails = bankAccountDetailsRepo.findByAccountNumber(moneyTransferRequest.getFromAccountNumber());
        if (bankAccountDetails.get().getAccountBalance() < moneyTransferRequest.getAmount()){
            throw new BusinessValidationException(Constants.ERROR_MSG_INSUFFICIENT_BAL);
        }

        //Check if FROM ACCOUNT is present in database
        bankAccountDetailsRepo.findByAccountNumber(moneyTransferRequest.getFromAccountNumber()).orElseThrow(() -> new BusinessValidationException(Constants.ERROR_MSG_FROM_ACCOUNT));

        //Check if FROM ACCOUNT is present in database
        bankAccountDetailsRepo.findByAccountNumber(moneyTransferRequest.getToAccountNumber()).orElseThrow(() -> new BusinessValidationException(Constants.ERROR_MSG_TO_ACCOUNT));
    }
}
