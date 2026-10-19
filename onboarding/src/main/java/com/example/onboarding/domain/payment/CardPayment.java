package com.example.onboarding.domain.payment;
import com.example.onboarding.domain.exception.InvalidAmountException;
import com.example.onboarding.domain.exception.PaymentFailedException;

import java.math.BigDecimal;



public class CardPayment implements PaymentMethod{
    private final String cardNumber;
    private final BigDecimal cardLimit;

    public CardPayment(String cardNumber, BigDecimal cardLimit){
        if (cardNumber == null || cardNumber.isBlank() || cardNumber.length() < 16){
            throw new IllegalArgumentException("Invalid card numbers");
        }
        this.cardNumber = cardNumber;
        this.cardLimit = cardLimit;
    }

    @Override

    public boolean processPayment (BigDecimal amount) throws  PaymentFailedException{
        if(amount == null || amount.compareTo(BigDecimal.ZERO)<= 0){
            throw new PaymentFailedException("Payment Amount must be positive. ");
        }
        if (amount.compareTo(cardLimit) > 0) {
            throw new PaymentFailedException("Transaction declined: Amount exceeds card limit of " + cardLimit);
        }
        return true;
    }
    @Override
    public String getPaymentType() {
        return "CARD";
    }
}
