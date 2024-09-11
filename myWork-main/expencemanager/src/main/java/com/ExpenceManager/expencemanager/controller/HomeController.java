package com.ExpenceManager.expencemanager.controller;

import com.ExpenceManager.expencemanager.constants.Constants;
import com.ExpenceManager.expencemanager.dto.*;
import com.ExpenceManager.expencemanager.entity.Transaction;
import com.ExpenceManager.expencemanager.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    TransactionService transactionService;

    @Autowired
    BudgetService budgetService;

    @Autowired
    CategoryService categoryService;
    @Autowired
    EmailService emailService;

    @RequestMapping("")
    public String home(Model model) {

        if(!EmailServiceImpl.isLoginEmailSent)
            emailService.sendLoginEmail("someone");
        List<TransactionDto> transactionDtoList = transactionService.getRecentTransactions();
        List<BudgetDto> monthlyBudgetList= budgetService.getCurrentBudgetList();

        List<BudgetDto> leftList = new ArrayList<>();
        List<BudgetDto> rightList = new ArrayList<>();
        int i = 1;
        for (BudgetDto budgetDto : monthlyBudgetList
        ) {
            if (i % 2 == 0) {
                rightList.add(budgetDto);
            } else {
                leftList.add(budgetDto);
            }
            i++;
        }
        Double expenseAmount = transactionService.getExpenseAmount(Constants.TRANSACTION_TYPE_EXPENSE);
        Double incomeAmount = transactionService.getExpenseAmount(Constants.TRANSACTION_TYPE_INCOME);
        Double balanceAmount= transactionService.getBalance(expenseAmount,incomeAmount);
        model.addAttribute("transactionDtoList", transactionDtoList);
        model.addAttribute("listOfBudgetlf", leftList);
        model.addAttribute("listOfBudgetrt", rightList);
        model.addAttribute("sumOfExpense", expenseAmount);
        model.addAttribute("sumOfIncome", incomeAmount);
        model.addAttribute("balanceAmount", balanceAmount);
        model.addAttribute("monthlyBudget",budgetService.getCurrentMonthlyBudgetDto());
        return "homePage";
    }

    @PostMapping("/searchTransaction")
    public  String searchTransaction(@ModelAttribute("String") SearchDto search,Model model){

        ViewDto viewDto= transactionService.getSearchedTransactions(search);
        model.addAttribute("view",viewDto);
        return "SearchView";
    }
    @GetMapping("/customView")
    public String showViewForm(Model model){
        ViewDto viewDto = transactionService.getView(new ArrayList<Transaction>());
        List<CategoryDto> categoryDtoList = categoryService.getCategoryList();
        categoryDtoList.add(new CategoryDto(0l, "All Categories", null, 0d, 0d));
        model.addAttribute("view", viewDto);
        model.addAttribute("categoryDtoList", categoryDtoList);
        return "CustomViewForm";
    }


    @PostMapping("/filterTransactions")
    public String getFilteredTransactions(@ModelAttribute("filterTransactionDto") FilterTransactionDto filterTransactionDto, Model model) throws ParseException {

        ViewDto viewDto=transactionService.getFilteredTransactions(filterTransactionDto);
        List<CategoryDto> categoryDtoList = categoryService.getCategoryList();
        categoryDtoList.add(new CategoryDto(0l, "All Categories", null, 0d, 0d));
        model.addAttribute("view",viewDto);
        model.addAttribute("categoryDtoList", categoryDtoList);
        model.addAttribute(Constants.INCOME_PIE_CHART_DATA, viewDto.getIncomeGraphData());
        model.addAttribute(Constants.EXPENSE_PIE_CHART_DATA, viewDto.getExpenseGraphData());
        model.addAttribute("isLineChartAvailable", true);
        model.addAttribute("imageUrli",viewDto.getIncomeChartURL());
        model.addAttribute("imageUrle",viewDto.getExpenseChartURL());
        return "CustomViewForm";
    }
    @GetMapping("/allTime")
    public String showFullView(Model model) throws IOException {
        ViewDto viewDto=transactionService.getFullView();
        model.addAttribute("view",viewDto);
        model.addAttribute(Constants.INCOME_COLUMN_CHART_DATA, viewDto.getYearlyIncomeChart());
        model.addAttribute(Constants.EXPENSE_COLUMN_CHART_DATA, viewDto.getYearlyExpenseChart());
        model.addAttribute(Constants.INCOME_PIE_CHART_DATA, viewDto.getIncomeGraphData());
        model.addAttribute(Constants.EXPENSE_PIE_CHART_DATA, viewDto.getExpenseGraphData());
        model.addAttribute("isLineChartAvailable", true);
        model.addAttribute("imageUrli",viewDto.getIncomeChartURL());
        model.addAttribute("imageUrle",viewDto.getExpenseChartURL());
        return "CustomView";
    }

    @GetMapping("/yearlyView")
    public String showYearlyView(Model model) throws IOException {
        ViewDto viewDto=transactionService.getYearlyView(0L);
        model.addAttribute("view",viewDto);
        model.addAttribute(Constants.INCOME_COLUMN_CHART_DATA, viewDto.getMonthlyIncomeChart());
        model.addAttribute(Constants.EXPENSE_COLUMN_CHART_DATA, viewDto.getMonthlyExpenseChart());
        model.addAttribute(Constants.INCOME_PIE_CHART_DATA, viewDto.getIncomeGraphData());
        model.addAttribute(Constants.EXPENSE_PIE_CHART_DATA, viewDto.getExpenseGraphData());
        model.addAttribute("isLineChartAvailable", true);
        model.addAttribute("imageUrli",viewDto.getIncomeChartURL());
        model.addAttribute("imageUrle",viewDto.getExpenseChartURL());
        return "CustomView";
    }

    @GetMapping("/monthlyView")
    public String showMonthlyView(Model model){
        ViewDto viewDto=transactionService.getMonthlyView(0L);
        model.addAttribute("view",viewDto);
        model.addAttribute(Constants.INCOME_PIE_CHART_DATA, viewDto.getIncomeGraphData());
        model.addAttribute(Constants.EXPENSE_PIE_CHART_DATA, viewDto.getExpenseGraphData());
        return "MonthlyView";
    }

    @GetMapping("/getPreviousView/{id}")
    public String showPreviousView(@PathVariable("id") long id,Model model) throws IOException {
        ViewDto viewDto=new ViewDto();
        if(id>10000L){
            if(id/10000==1)
                viewDto = transactionService.getMonthlyView(109999+id);
            else
               viewDto = transactionService.getMonthlyView(id - 10000);
            model.addAttribute(Constants.INCOME_PIE_CHART_DATA, viewDto.getIncomeGraphData());
            model.addAttribute(Constants.EXPENSE_PIE_CHART_DATA, viewDto.getExpenseGraphData());
            model.addAttribute("view",viewDto);
            return "MonthlyView";
        }
        else if (id == -1) {
            viewDto = transactionService.getFullView();
            model.addAttribute(Constants.INCOME_COLUMN_CHART_DATA, viewDto.getYearlyIncomeChart());
            model.addAttribute(Constants.EXPENSE_COLUMN_CHART_DATA, viewDto.getYearlyExpenseChart());
            model.addAttribute(Constants.INCOME_PIE_CHART_DATA, viewDto.getIncomeGraphData());
            model.addAttribute(Constants.EXPENSE_PIE_CHART_DATA, viewDto.getExpenseGraphData());
            model.addAttribute("isLineChartAvailable", true);
            model.addAttribute("imageUrli",viewDto.getIncomeChartURL());
            model.addAttribute("imageUrle",viewDto.getExpenseChartURL());
        }
        else {
            viewDto = transactionService.getYearlyView(id - 1);
            model.addAttribute(Constants.INCOME_COLUMN_CHART_DATA, viewDto.getMonthlyIncomeChart());
            model.addAttribute(Constants.EXPENSE_COLUMN_CHART_DATA, viewDto.getMonthlyExpenseChart());
            model.addAttribute(Constants.INCOME_PIE_CHART_DATA, viewDto.getIncomeGraphData());
            model.addAttribute(Constants.EXPENSE_PIE_CHART_DATA, viewDto.getExpenseGraphData());
        }
        model.addAttribute("view",viewDto);
        model.addAttribute("isLineChartAvailable", true);
        model.addAttribute("imageUrli",viewDto.getIncomeChartURL());
        model.addAttribute("imageUrle",viewDto.getExpenseChartURL());
        return "CustomView";

    }

    @GetMapping("/getNextView/{id}")
    public String showNextView(@PathVariable("id") long id,Model model) throws IOException {
        ViewDto viewDto;
        if(id>10000L){
            if(id/120000==1)
                viewDto = transactionService.getMonthlyView(id-109999);
            else
                viewDto = transactionService.getMonthlyView(id + 10000);
            model.addAttribute(Constants.INCOME_PIE_CHART_DATA, viewDto.getIncomeGraphData());
            model.addAttribute(Constants.EXPENSE_PIE_CHART_DATA, viewDto.getExpenseGraphData());
            model.addAttribute("view",viewDto);
            return "MonthlyView";
        }
        else if (id == -1) {
            viewDto = transactionService.getFullView();
            model.addAttribute(Constants.INCOME_COLUMN_CHART_DATA, viewDto.getYearlyIncomeChart());
            model.addAttribute(Constants.EXPENSE_COLUMN_CHART_DATA, viewDto.getYearlyExpenseChart());
            model.addAttribute(Constants.INCOME_PIE_CHART_DATA, viewDto.getIncomeGraphData());
            model.addAttribute(Constants.EXPENSE_PIE_CHART_DATA, viewDto.getExpenseGraphData());
        }
        else{
            viewDto = transactionService.getYearlyView(id + 1);
            model.addAttribute(Constants.INCOME_COLUMN_CHART_DATA, viewDto.getMonthlyIncomeChart());
            model.addAttribute(Constants.EXPENSE_COLUMN_CHART_DATA, viewDto.getMonthlyExpenseChart());
            model.addAttribute(Constants.INCOME_PIE_CHART_DATA, viewDto.getIncomeGraphData());
            model.addAttribute(Constants.EXPENSE_PIE_CHART_DATA, viewDto.getExpenseGraphData());
        }
        model.addAttribute("view",viewDto);
        model.addAttribute("isLineChartAvailable", true);
        model.addAttribute("imageUrli",viewDto.getIncomeChartURL());
        model.addAttribute("imageUrle",viewDto.getExpenseChartURL());
        return "CustomView";

    }
}
