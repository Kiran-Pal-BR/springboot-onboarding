package com.example.onboarding.domain.account;
import com.example.onboarding.domain.exception.InsufficientBalanceException;
import com.example.onboarding.domain.exception.InvalidAmountException;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {
    private static long idCounter = 1000L;
    private final String accountId;
    private final String accountHolderName;
    private BigDecimal balance;


    public Account(String accountHolderName, BigDecimal initialBalance) {
        if (initialBalance == null || initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException("Initial balance cannot be negative or null.");
        }
        this.accountId = "ACC-" + (++idCounter);
        this.accountHolderName = Objects.requireNonNull(accountHolderName, "Account holder name cannot be null.");
        this.balance = initialBalance;
    }

    public Account(String accountHolderName){
            this(accountHolderName,BigDecimal.ZERO);
    }

    public void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Deposit amount must be strictly greater than zero.");
        }
        this.balance = this.balance.add(amount);
    }


    public void withdraw(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be strictly greater than zero.");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient funds. Current balance: " + this.balance + ", Attempted: " + amount
            );
        }
        this.balance = this.balance.subtract(amount);
    }



    public String getAccountId() {
        return accountId;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

}
