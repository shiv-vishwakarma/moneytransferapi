package com.banking.moneytransferapi.util;

import com.banking.moneytransferapi.dto.MoneyTransferRequest;
import com.banking.moneytransferapi.exception.BusinessValidationException;
import com.banking.moneytransferapi.repository.BankAccountDetailsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BusinessValidation {

    @Autowired
    private BankAccountDetailsRepo bankAccountDetailsRepo;

    public void performBusinessValidations(MoneyTransferRequest moneyTransferRequest) {

        //Check if both account numbers are not same
        if(moneyTransferRequest.getFromAccountNumber().equalsIgnoreCase(moneyTransferRequest.getToAccountNumber())){
            throw new BusinessValidationException(Constants.ERROR_MSG_SAME_ACCOUNTS);
        }

        //Check if FROM ACCOUNT is present in database , if Present, then check for sufficient account balance
        bankAccountDetailsRepo.findByAccountNumber(moneyTransferRequest.getFromAccountNumber()).ifPresentOrElse(bankAcc -> {
            if (bankAcc.getAccountBalance() < moneyTransferRequest.getAmount()){
                throw new BusinessValidationException(Constants.ERROR_MSG_INSUFFICIENT_BAL);
            }
        }, () -> {throw new BusinessValidationException(Constants.ERROR_MSG_FROM_ACCOUNT);});


        //Check if FROM ACCOUNT is present in database
        bankAccountDetailsRepo.findByAccountNumber(moneyTransferRequest.getToAccountNumber()).orElseThrow(() -> new BusinessValidationException(Constants.ERROR_MSG_TO_ACCOUNT));
    }
}
