package com.example.onboarding.domain;
import com.example.onboarding.domain.account.Account;
import com.example.onboarding.domain.exception.InsufficientBalanceException;
import com.example.onboarding.domain.exception.PaymentFailedException;
import com.example.onboarding.domain.exception.InvalidAmountException;
import com.example.onboarding.domain.payment.PaymentMethod;
import com.example.onboarding.domain.payment.CashPayment;
import com.example.onboarding.domain.payment.CardPayment;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class AccountAndPaymentTest {
    @Test
    @DisplayName("Should create account with valid initial balance")

    void testAccountCreation_Success() {
        Account account = new Account("Alice", new BigDecimal("100.00"));
        assertNotNull(account.getAccountId());
        assertEquals("Alice", account.getAccountHolderName());
        assertEquals(new BigDecimal("100.00"), account.getBalance());
    }


    @Test
    @DisplayName("Should throw exception when creating account with negative balance")

    void testAccountCreation_NegativeBalance_ThrowsException(){
        assertThrows(InvalidAmountException.class, () ->
                new Account("Bob", new BigDecimal("-50.00"))
        );
    }


    @Test
    @DisplayName("Should deposit money successfully")

    void testDeposit_Success(){
        Account account = new Account("Charlie", new BigDecimal("50.00"));
        account.deposit(new BigDecimal("25.00"));
        assertEquals(new BigDecimal("75.00"), account.getBalance());
    }



    @Test
    @DisplayName("Should throw exception on zero or negative deposit")
    void testDeposit_InvalidAmount_ThrowsException() {
        Account account = new Account("David", new BigDecimal("50.00"));
        assertThrows(InvalidAmountException.class, () -> account.deposit(BigDecimal.ZERO));
        assertThrows(InvalidAmountException.class, () -> account.deposit(new BigDecimal("-10.00")));
    }


    @Test
    @DisplayName("Should withdraw money successfully within available balance")
    void testWithdraw_Success() {
        Account account = new Account("Eve", new BigDecimal("100.00"));
        account.withdraw(new BigDecimal("40.00"));
        assertEquals(new BigDecimal("60.00"), account.getBalance());
    }

    @Test
    @DisplayName("Should throw exception when withdrawing more than balance")
    void testWithdraw_InsufficientFunds_ThrowsException() {
        Account account = new Account("Frank", new BigDecimal("30.00"));
        assertThrows(InsufficientBalanceException.class, () ->
                account.withdraw(new BigDecimal("50.00"))
        );
    }

    @Test
    @DisplayName("Should process cash payment when cash provided is sufficient")
    void testCashPayment_Success() throws PaymentFailedException {
        CashPayment cashPayment = new CashPayment(new BigDecimal("100.00"));
        assertTrue(cashPayment.processPayment(new BigDecimal("75.00")));
        assertEquals(new BigDecimal("25.00"), cashPayment.getChange(new BigDecimal("75.00")));
    }


    @Test
    @DisplayName("Should fail cash payment when cash provided is insufficient")
    void testCashPayment_InsufficientCash_ThrowsException() {
        CashPayment cashPayment = new CashPayment(new BigDecimal("20.00"));
        assertThrows(PaymentFailedException.class, () ->
                cashPayment.processPayment(new BigDecimal("50.00"))
        );
    }


    @Test
    @DisplayName("Should process card payment within limit")
    void testCardPayment_Success() throws PaymentFailedException {
        PaymentMethod cardPayment = new CardPayment("1234567812345678", new BigDecimal("500.00"));
        assertTrue(cardPayment.processPayment(new BigDecimal("250.00")));
        assertEquals("CARD", cardPayment.getPaymentType());
    }


    @Test
    @DisplayName("Should fail card payment when amount exceeds card limit")
    void testCardPayment_ExceedsLimit_ThrowsException() {
        PaymentMethod cardPayment = new CardPayment("1234567812345678", new BigDecimal("100.00"));
        assertThrows(PaymentFailedException.class, () ->
                cardPayment.processPayment(new BigDecimal("150.00"))
        );
    }


}
