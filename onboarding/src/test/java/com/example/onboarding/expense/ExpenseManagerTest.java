package com.example.onboarding.expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseManagerTest {
    private ExpenseManager expenseManager;

    @BeforeEach
    void setup(){
        expenseManager = new ExpenseManager();
    }

    @Test
    @DisplayName("Should add expense and calculate total correctly")
    void testAddAndTotalExpense(){
        expenseManager.addExpense(new Expense("E-1","Lunch",new BigDecimal("150.00"),LocalDate.now(),ExpenseCategory.FOOD));
        expenseManager.addExpense(new Expense("E-2","Bus ticket",new BigDecimal("50.00"),LocalDate.now(),ExpenseCategory.TRANSPORT));

        assertEquals(2, expenseManager.getExpenseCount());
        assertEquals(new BigDecimal("200.00"), expenseManager.getTotalExpenses());
    }

    @Test
    @DisplayName("Should calculate total expenses by specific category")
    void testTotalByCategory(){
        expenseManager.addExpense(new Expense("E-1","Lunch",new BigDecimal("200.00"),LocalDate.now(),ExpenseCategory.FOOD));
        expenseManager.addExpense(new Expense("E-2","Dinner",new BigDecimal("300.00"),LocalDate.now(),ExpenseCategory.FOOD));
        expenseManager.addExpense(new Expense("E-3","Movie",new BigDecimal("500.00"),LocalDate.now(),ExpenseCategory.ENTERTAINMENT));

        BigDecimal foodTotal = expenseManager.getTotalCategory(ExpenseCategory.FOOD);
        assertEquals(new BigDecimal("500.00"),foodTotal);

    }
    @Test
    @DisplayName("Should filter expenses between start and end date")
    void testGetExpensesBetweenDates(){
        LocalDate today = LocalDate.now();
        LocalDate pastDate = today.minusDays(5);
        LocalDate futureDate = today.plusDays(5);

        expenseManager.addExpense(new Expense("E-1","Old Expense",new BigDecimal("100.00"),pastDate,ExpenseCategory.UTILITIES));
        expenseManager.addExpense(new Expense("E-2", "Today Expense", new BigDecimal("200.00"), today, ExpenseCategory.FOOD));

        List<Expense> results = expenseManager.getExpenseBetweenDates(today.minusDays(1), today.plusDays(1));
        assertEquals("Today Expense",results.get(0).description());



    }
}
