package com.example.onboarding.domain.payment;
import com.example.onboarding.domain.exception.InvalidAmountException;
import com.example.onboarding.domain.exception.PaymentFailedException;
import java.math.BigDecimal;



public class CashPayment implements PaymentMethod{

    private final BigDecimal cashProvided;

    public CashPayment(BigDecimal cashProvided){
        if (cashProvided == null || cashProvided.compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidAmountException("Cash provided must be greater than zero. ");
        }
        this.cashProvided = cashProvided;
    }
    @Override
    public boolean processPayment(BigDecimal amount) throws PaymentFailedException{
        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 ){
            throw new InvalidAmountException("Payment amount must be Positive.");
        }
        if (cashProvided.compareTo(amount) < 0){
            throw new PaymentFailedException("Insufficient cash Provided. Required "+ amount + ", Please provide "+cashProvided);
        }
        return true;
    }
    @Override
    public String getPaymentType() {
        return "CASH";
    }

    public BigDecimal getChange(BigDecimal amount) {
        return cashProvided.subtract(amount);
    }
}
