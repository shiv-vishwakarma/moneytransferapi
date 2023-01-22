package com.banking.moneytransferapi.controller;

import com.banking.moneytransferapi.dto.MoneyTransferRequest;
import com.banking.moneytransferapi.dto.MoneyTransferResponse;
import com.banking.moneytransferapi.entity.BankAccountDetails;
import com.banking.moneytransferapi.entity.Transactions;
import com.banking.moneytransferapi.exception.BusinessValidationException;
import com.banking.moneytransferapi.service.TransferService;
import com.banking.moneytransferapi.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class MoneyTransferController {

    @Autowired
    private TransferService transferService;

    @PostMapping("/fundTransfer")
    public ResponseEntity<MoneyTransferResponse> performFundTransfer(@Valid @RequestBody MoneyTransferRequest moneyTransferRequest){
        transferService.startTransactionProcess(moneyTransferRequest);

        MoneyTransferResponse moneyTransferResponse = new MoneyTransferResponse();
        moneyTransferResponse.setTransactionStatus(Constants.SUCCESS);
        moneyTransferResponse.setTransactionMessage("Amount was successfully transferred to beneficiary account");
        moneyTransferResponse.setTransactionRefId(UUID.randomUUID().toString());
        return new ResponseEntity<>(moneyTransferResponse, HttpStatus.OK);
    }

    @GetMapping("/fetchAccDetailByCustId/{custId}")
    public ResponseEntity<BankAccountDetails> fetchAccountDetails (@PathVariable("custId") String customerId){
        Optional<BankAccountDetails> accountDetails = transferService.fetchAccountDetails(customerId);
        if (accountDetails.isPresent()) {
            return new ResponseEntity<BankAccountDetails>(accountDetails.get(), HttpStatus.OK);
        }
        throw new BusinessValidationException("Account with Customer ID : "+customerId+" does not exist");
    }

    @PostMapping("/openAccount")
    public ResponseEntity<BankAccountDetails> openBankAccount(@Valid @RequestBody BankAccountDetails bankAccountDetails){
        return new ResponseEntity<BankAccountDetails>(transferService.createNewBankAccount(bankAccountDetails),  HttpStatus.OK);
    }

    @GetMapping("/fetchTransactionsByAccountNo/{accountno}")
    public ResponseEntity<List<Transactions>> fetchTransactionDetails (@PathVariable("accountno") String accountno){
        Optional<List<Transactions>> transactions = transferService.fetchTransactionsByAccountNo(accountno);
        if (transactions.isPresent()) {
            return new ResponseEntity<List<Transactions>>(transactions.get(), HttpStatus.OK);
        }
        throw new BusinessValidationException("No Transactions for account Number : "+accountno);
    }
}
