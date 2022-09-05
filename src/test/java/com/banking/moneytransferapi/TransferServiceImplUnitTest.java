package com.banking.moneytransferapi;

import com.banking.moneytransferapi.dto.MoneyTransferRequest;
import com.banking.moneytransferapi.entity.BankAccountDetails;
import com.banking.moneytransferapi.exception.MoneyTransferApiException;
import com.banking.moneytransferapi.repository.BankAccountDetailsRepo;
import com.banking.moneytransferapi.service.TransferServiceImpl;
import com.banking.moneytransferapi.util.BusinessValidation;
import com.banking.moneytransferapi.util.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TransferServiceImplUnitTest {

    @InjectMocks
    private static TransferServiceImpl transferService;

    @Mock
    private BankAccountDetailsRepo bankAccountDetailsRepo;

    @Mock
    private BusinessValidation businessValidation;

    private static BankAccountDetails bankAccountDetails = new BankAccountDetails();
    private static MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest() ;

    @BeforeAll
    static void setUpBeforeClass() {
        moneyTransferRequest.setAmount(500.0);
        moneyTransferRequest.setFromAccountNumber("1234");
        moneyTransferRequest.setToAccountNumber("4321");

        bankAccountDetails.setAccountNumber("1234");
        bankAccountDetails.setAccountBalance(5000.0);
        bankAccountDetails.setCustomerId("CUST1001");
    }

    @Test
    void testStartTransactionProcess() throws Exception {
        when(bankAccountDetailsRepo.findByAccountNumber(anyString())).thenReturn(Optional.of(bankAccountDetails));
        when(bankAccountDetailsRepo.save(Mockito.any(BankAccountDetails.class))).thenReturn(bankAccountDetails);
        transferService.startTransactionProcess(moneyTransferRequest);
        verify(businessValidation, times(1)).performBusinessValidations(moneyTransferRequest);
        verify(bankAccountDetailsRepo,times(2)).findByAccountNumber(anyString());
        verify(bankAccountDetailsRepo,times(2)).save(Mockito.any(BankAccountDetails.class));
    }

    @Test
    void testFetchAccountDetails(){
        when(bankAccountDetailsRepo.findByCustomerId(anyString())).thenReturn(Optional.of(bankAccountDetails));
        Assertions.assertEquals(Optional.ofNullable(bankAccountDetails), transferService.fetchAccountDetails(anyString()));
    }

    @Test
    void testCreateNewBankAccount() {
        when(bankAccountDetailsRepo.save(Mockito.any(BankAccountDetails.class))).thenReturn(bankAccountDetails);
        Assertions.assertEquals(bankAccountDetails, transferService.createNewBankAccount(bankAccountDetails));
    }

    @Test
    void testStartTransactionProcess_exception_while_withdraw() {
       when(bankAccountDetailsRepo.findByAccountNumber(anyString())).thenThrow(MoneyTransferApiException.class);
       Throwable exception = assertThrows(MoneyTransferApiException.class, () -> transferService.startTransactionProcess(moneyTransferRequest));
       Assertions.assertEquals(Constants.ERROR_MSG_DEBIT_MONEY, exception.getMessage());
    }

    @Test
    void testStartTransactionProcess_exception_while_deposit() {
        when(bankAccountDetailsRepo.save(Mockito.any(BankAccountDetails.class))).thenReturn(bankAccountDetails);
        when(bankAccountDetailsRepo.findByAccountNumber(anyString())).thenReturn(Optional.ofNullable(bankAccountDetails)).thenThrow(MoneyTransferApiException.class);
        Throwable exception = assertThrows(MoneyTransferApiException.class, () -> transferService.startTransactionProcess(moneyTransferRequest));
        Assertions.assertEquals(Constants.ERROR_MSG_CREDIT_MONEY, exception.getMessage());
    }
}