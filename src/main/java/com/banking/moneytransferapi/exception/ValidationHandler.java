package com.banking.moneytransferapi.exception;

import com.banking.moneytransferapi.dto.MoneyTransferResponse;
import com.banking.moneytransferapi.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ValidationHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> errorsMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errorsMap.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errorsMap, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = BusinessValidationException.class)
    protected ResponseEntity<MoneyTransferResponse> handleBusinessValidationException(BusinessValidationException ex) {
        MoneyTransferResponse moneyTransferResponse = new MoneyTransferResponse();
        moneyTransferResponse.setTransactionMessage(ex.getMessage());
        moneyTransferResponse.setTransactionStatus(Constants.FAILURE);
        return new ResponseEntity<>(moneyTransferResponse, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = MoneyTransferApiException.class)
    protected ResponseEntity<Object> handleMoneyTransferApiException(MoneyTransferApiException ex) {
        MoneyTransferResponse moneyTransferResponse = new MoneyTransferResponse();
        moneyTransferResponse.setTransactionMessage(ex.getMessage());
        moneyTransferResponse.setTransactionStatus(Constants.FAILURE);
        return new ResponseEntity<>(moneyTransferResponse, HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<Object> handleAllException(Exception ex) {
        MoneyTransferResponse moneyTransferResponse = new MoneyTransferResponse();
        moneyTransferResponse.setTransactionMessage(Constants.ERROR_MSG_UNEXPECTED);
        moneyTransferResponse.setTransactionStatus(Constants.FAILURE);
        return new ResponseEntity<>(moneyTransferResponse, HttpStatus.EXPECTATION_FAILED);
    }

}
