package com.ExpenceManager.expencemanager.service;

import com.ExpenceManager.expencemanager.dto.BudgetDto;
import com.ExpenceManager.expencemanager.entity.Budget;
import com.ExpenceManager.expencemanager.entity.Transaction;

import java.util.List;


public interface BudgetService {

    Budget dtoToEntity(BudgetDto budgetDto);

    BudgetDto entityToDto(Budget budget);
    List<BudgetDto> getCurrentBudgetList();

    List<BudgetDto> getCategoryList();

    List<BudgetDto> createNewBudget(String year, String month, Double totalBudgetAmount);
    String getBudgetPageMessage(String month);

    String getBudgetAmountLeft(BudgetDto budgetDto);

    String createBudgetEntry(BudgetDto budgetDto);

    List<BudgetDto> getbudgetCategoriesList(String month, String year, Double totalBudgetAmount);

    void addBudgetTransaction(Transaction transaction);

    boolean isValidBudget(BudgetDto budgetDto);

    BudgetDto getCurrentMonthlyBudgetDto();

    BudgetDto getCurrentMonthlyBudgetToEdit();

    Double editCurrentBudgetTotalAmount(BudgetDto budgetDto);
}
