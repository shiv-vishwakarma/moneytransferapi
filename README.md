
# Fund Transfer API

This service is a payment gateway service.


## Features

- Transfer Fund between two different account.
- Fetch Bank Account details based on CustomerID.
- Open new Bank Accounts.


## API Reference

#### Transfer Funds
This API accepts a payload having below parameters for transferring funds:

```http
  POST /api/fundTransfer
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `fromAccountNumber` | `String` |  Account Number from which amount is to be debited|
| `toAccountNumber` | `String` |  Account Number in which amount will be credited |
| `accountBalance` | `Double` |  Amount to be transferred|

#### Fetch Account Details
This API fetches account details based on the CustomerID:

```http
  GET /api/fetchAccDetailByCustId/{custId}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `custId`      | `String` |  Customer Id of the Account Holder |


#### Open Bank Account
This API accepts a payload having below parameters for creating new account:

```http
  POST /api/openAccount
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `accountHolderName` | `String` |  Account Holder's Name |
| `accountType` | `String` |  Account Type |
| `ifscCode` | `String` |  IFSC Code |
| `emailId` | `String` |  Email ID |
| `mobileNumber` | `String` |  Mobile Number |
| `customerId` | `String` |  Customer ID |
| `accountBalance` | `Double` |  Initial Amount to be deposited|





