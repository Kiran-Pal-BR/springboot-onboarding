package com.example.onboarding.expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;


public record Expense (
   String id,
   String description,
   BigDecimal amount,
   LocalDate date,
   ExpenseCategory category
){
    public Expense{
        Objects.requireNonNull(id,"Expense ID cannot be null.");
        Objects.requireNonNull(description, "Expense Description cannot be null");
        Objects.requireNonNull(amount, "Expense amount cannot be null");
        Objects.requireNonNull(date, "Expense date cannot be null");
        Objects.requireNonNull(category, "Expense category cannot be null");
        if(amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Expense amount must be strictly greater than zero.");
        }

    }
}