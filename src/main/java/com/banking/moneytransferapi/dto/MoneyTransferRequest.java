package com.banking.moneytransferapi.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MoneyTransferRequest {

    @NotBlank(message = "From Account Number is mandatory")
    private String fromAccountNumber;

    @NotBlank(message = "To Account Number is mandatory")
    private String toAccountNumber;

    @NotNull(message = "Please enter the amount to be transferred")
    @Min(value = 1, message = "Transferable amount should be greater than 0")
    private Double amount;
}
