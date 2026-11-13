package com.example.onboarding.expense;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;

public class ExpenseManager {
    private static final Logger log = LoggerFactory.getLogger(ExpenseManager.class);
    private final Map<String, Expense> expenseStore = new HashMap<>();

    //Add an Expense
    public void addExpense(Expense expense){
        Objects.requireNonNull(expense, "Expense cannot be null");
        if(expenseStore.containsKey(expense.id())){
            log.error("Failed to add expense. ID {} already exists.", expense.id());
            throw new IllegalArgumentException("Expense with Id" + expense.id() + "already exists.");
        }
        expenseStore.put(expense.id(),expense);
        log.info("Successfully added expense ID: {}, Amount: {}, Category: {}",
                expense.id(), expense.amount(), expense.category());
    }
    //Get total expense amount across all records
    public BigDecimal getTotalExpenses(){
        log.debug("Calculating total expenses across {} records", expenseStore.size());
        return expenseStore.values().stream()
                .map(Expense::amount)
                .reduce(BigDecimal.ZERO,BigDecimal::add);
    }

    // Get total expense grouped/filter by category
    public BigDecimal getTotalCategory(ExpenseCategory category){
        Objects.requireNonNull(category,"Category cannot be null");
        log.debug("Calculating total expenses for category: {}", category);
        return expenseStore.values().stream()
                .filter(expense -> expense.category() == category)
                .map(Expense::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    //Filter expenses within a specific date range
    public List<Expense> getExpenseBetweenDates(LocalDate startDate, LocalDate endDate){
        Objects.requireNonNull(startDate,"Start date cannot be null");
        Objects.requireNonNull(endDate,"End date cannot be null");
        log.debug("Filtering expenses between dates: {} and {}", startDate, endDate);

        return expenseStore.values().stream()
                .filter(expense -> !expense.date().isBefore(startDate) && !expense.date().isAfter(endDate))
                .toList();
    }
    public int getExpenseCount(){
        return expenseStore.size();
    }
}
