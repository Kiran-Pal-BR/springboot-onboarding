package com.example.onboarding.domain.payment;
import com.example.onboarding.domain.exception.PaymentFailedException;
import java.math.BigDecimal;

public interface PaymentMethod {
    boolean processPayment(BigDecimal amount) throws PaymentFailedException;
    String getPaymentType();
}
