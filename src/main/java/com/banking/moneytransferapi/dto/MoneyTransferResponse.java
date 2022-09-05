package com.banking.moneytransferapi.dto;

import lombok.Data;

@Data
public class MoneyTransferResponse {
    private String transactionStatus;
    private String transactionMessage;
    private String transactionRefId;
}
