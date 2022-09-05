package com.banking.moneytransferapi;

import com.banking.moneytransferapi.dto.MoneyTransferRequest;
import com.banking.moneytransferapi.dto.MoneyTransferResponse;
import com.banking.moneytransferapi.entity.BankAccountDetails;
import com.banking.moneytransferapi.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MoneytransferapiApplicationTests {

    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";

    private static RestTemplate restTemplate;

    @Autowired
    private TestH2Repo testH2Repo;

    @BeforeAll
    public static void init() {
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    public void setup() {
        baseUrl = baseUrl.concat(":").concat(port + "").concat("/api");
    }

    @Test
    @Sql(statements = "DELETE FROM BANK_ACCOUNT_DETAILS WHERE CUSTOMER_ID = 'CUST10001'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testOpenBankAccount() {
        BankAccountDetails accountDetails = new BankAccountDetails(1L, "1234", "Shiv1", "Savings", "ICIC2345", "Hello@gmail.com", "8976479745", "CUST10001", 5000D);
        ResponseEntity<BankAccountDetails> response = restTemplate.exchange(baseUrl.concat("/openAccount"), HttpMethod.POST, new HttpEntity(accountDetails,new HttpHeaders()), BankAccountDetails.class);
        Assertions.assertAll(
                () -> Assertions.assertEquals(HttpStatus.OK, response.getStatusCode()),
                () ->  Assertions.assertEquals("CUST10001", response.getBody().getCustomerId()),
                () -> Assertions.assertEquals(1, testH2Repo.findAll().size())
        );
    }

    @Test
    @Sql(statements = "INSERT INTO BANK_ACCOUNT_DETAILS (BANK_ACCOUNT_DETAILS_ID,ACCOUNT_NUMBER,ACCOUNT_HOLDER_NAME,ACCOUNT_TYPE,IFSC_CODE,EMAIL_ID,MOBILE_NUMBER,CUSTOMER_ID,ACCOUNT_BALANCE) VALUES (1,'1234','Shiv1','Savings','ICIC2345','Hello@gmail.com','8976479745','CUST10001',5000.0)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM BANK_ACCOUNT_DETAILS WHERE CUSTOMER_ID = 'CUST10001'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testFetchAccountDetails() {
        ResponseEntity<BankAccountDetails> response = restTemplate.getForEntity(baseUrl.concat("/fetchAccDetailByCustId/{custId}"), BankAccountDetails.class, "CUST10001");
        Assertions.assertAll(
                () -> Assertions.assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> Assertions.assertEquals(1, testH2Repo.findAll().size()),
                () -> Assertions.assertEquals("CUST10001", response.getBody().getCustomerId())
        );
    }

    @Test()
    void testFetchAccountDetails_for_no_account() throws JsonProcessingException {
        try {
            ResponseEntity<BankAccountDetails> response = restTemplate.getForEntity(baseUrl.concat("/fetchAccDetailByCustId/{custId}"), BankAccountDetails.class, "CUST10001");
        } catch (Exception e) {
            ObjectMapper mapper = new ObjectMapper();
            String responseString = ((HttpClientErrorException.NotAcceptable) e).getResponseBodyAsString();
            Map<String,String> result = mapper.readValue(responseString, Map.class);
            Assertions.assertAll(
                    () -> Assertions.assertEquals(HttpStatus.NOT_ACCEPTABLE, ((HttpClientErrorException.NotAcceptable) e).getStatusCode()),
                    () -> Assertions.assertEquals(Constants.FAILURE, result.get("transactionStatus"))
            );
        }
    }

    @Test
    @Sql(statements = "INSERT INTO BANK_ACCOUNT_DETAILS (BANK_ACCOUNT_DETAILS_ID,ACCOUNT_NUMBER,ACCOUNT_HOLDER_NAME,ACCOUNT_TYPE,IFSC_CODE,EMAIL_ID,MOBILE_NUMBER,CUSTOMER_ID,ACCOUNT_BALANCE) VALUES (1,'1234','Shiv1','Savings','ICIC2345','Hello@gmail.com','8976479745','CUST10001',5000.0)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO BANK_ACCOUNT_DETAILS (BANK_ACCOUNT_DETAILS_ID,ACCOUNT_NUMBER,ACCOUNT_HOLDER_NAME,ACCOUNT_TYPE,IFSC_CODE,EMAIL_ID,MOBILE_NUMBER,CUSTOMER_ID,ACCOUNT_BALANCE) VALUES (2,'4321','Shiv2','Savings','ICIC2345','Hello@gmail.com','8976479745','CUST10002',5000.0)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM BANK_ACCOUNT_DETAILS WHERE CUSTOMER_ID IN('CUST10001','CUST10002')", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testPerformFundTransfer() {

        MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest();
        moneyTransferRequest.setFromAccountNumber("1234");
        moneyTransferRequest.setToAccountNumber("4321");
        moneyTransferRequest.setAmount(1000.0);

        ResponseEntity<MoneyTransferResponse> response = restTemplate.exchange(baseUrl.concat("/fundTransfer"), HttpMethod.POST, new HttpEntity(moneyTransferRequest,new HttpHeaders()), MoneyTransferResponse.class);
        Assertions.assertAll(
                () -> Assertions.assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> Assertions.assertNotNull(response.getBody().getTransactionRefId()),
                () -> Assertions.assertEquals(4000, testH2Repo.findByCustomerId("CUST10001").get().getAccountBalance()),
                () -> Assertions.assertEquals(6000, testH2Repo.findByCustomerId("CUST10002").get().getAccountBalance()),
                () -> Assertions.assertEquals(Constants.SUCCESS, response.getBody().getTransactionStatus())
        );
    }

    @Test
    @Sql(statements = "INSERT INTO BANK_ACCOUNT_DETAILS (BANK_ACCOUNT_DETAILS_ID,ACCOUNT_NUMBER,ACCOUNT_HOLDER_NAME,ACCOUNT_TYPE,IFSC_CODE,EMAIL_ID,MOBILE_NUMBER,CUSTOMER_ID,ACCOUNT_BALANCE) VALUES (1,'1234','Shiv1','Savings','ICIC2345','Hello@gmail.com','8976479745','CUST10001',5000.0)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM BANK_ACCOUNT_DETAILS WHERE CUSTOMER_ID IN('CUST10001','CUST10002')", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testPerformFundTransfer_businessValidation_failure() throws JsonProcessingException {
        // Send Same accounts
        MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest();
        moneyTransferRequest.setFromAccountNumber("1234");
        moneyTransferRequest.setToAccountNumber("1234");
        moneyTransferRequest.setAmount(1000.0);

        try {
            ResponseEntity<MoneyTransferResponse> response = restTemplate.exchange(baseUrl.concat("/fundTransfer"), HttpMethod.POST, new HttpEntity(moneyTransferRequest,new HttpHeaders()), MoneyTransferResponse.class);
        } catch (Exception e) {
            ObjectMapper mapper = new ObjectMapper();
            String responseString = ((HttpClientErrorException.NotAcceptable) e).getResponseBodyAsString();
            Map<String,String> result = mapper.readValue(responseString, Map.class);
            Assertions.assertAll(
                    () -> Assertions.assertEquals(HttpStatus.NOT_ACCEPTABLE, ((HttpClientErrorException.NotAcceptable) e).getStatusCode()),
                    () -> Assertions.assertEquals(Constants.FAILURE, result.get("transactionStatus")),
                    () -> Assertions.assertEquals(Constants.ERROR_MSG_SAME_ACCOUNTS, result.get("transactionMessage")),
                    () -> Assertions.assertNull(result.get("transactionRefId"))
            );
        }
    }

    @Test()
    void testPerformFundTransfer_invalid_data() throws JsonProcessingException {
        try {
            MoneyTransferResponse response = restTemplate.postForObject(baseUrl.concat("/fundTransfer"), new MoneyTransferRequest(), MoneyTransferResponse.class);
        } catch (Exception e) {
            ObjectMapper mapper = new ObjectMapper();
            String responseString = ((HttpClientErrorException.BadRequest) e).getResponseBodyAsString();
            Map<String,String> result = mapper.readValue(responseString, Map.class);
            Assertions.assertAll(
                    () -> Assertions.assertEquals(HttpStatus.BAD_REQUEST, ((HttpClientErrorException.BadRequest) e).getStatusCode()),
                    () -> Assertions.assertEquals(3, result.keySet().size())
            );
        }
    }
}
