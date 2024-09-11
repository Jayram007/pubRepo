package com.ExpenceManager.expencemanager.controller;

import com.ExpenceManager.expencemanager.dto.BudgetDto;
import com.ExpenceManager.expencemanager.service.BudgetService;
import com.ExpenceManager.expencemanager.service.UtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class BudgetController {

    @Autowired
    BudgetService budgetService;
    @Autowired
    UtilityService utilityService;

    @GetMapping("/addBudgetForm")
    public  String addBudgetForm(){

        return "addBudgetForm";
    }

    @PostMapping("/addBudget")
    public String addNewBudget(@ModelAttribute("budgetDto") BudgetDto budgetDto,Model model) {
        if (budgetService.isValidBudget(budgetDto)){
            List<BudgetDto> newBudgetForm = budgetService.createNewBudget(budgetDto.getMonth().substring(0, 4), budgetDto.getMonth().substring(5), budgetDto.getTotalBudgetAmount());
        if (newBudgetForm != null) {
            model.addAttribute("newBudgetList", newBudgetForm);
            model.addAttribute("BudgetMessage", budgetService.getBudgetPageMessage(budgetDto.getMonth().substring(5)));
            model.addAttribute("BudgetAmountLeft", budgetService.getBudgetAmountLeft(budgetDto));
            return "newBudgetForm";
        }
    }
        return "test";
    }

    @PostMapping("/addNewBudget")
    public String addNewBudgetCategory(@ModelAttribute("budgetDto") BudgetDto budgetDto,Model model){

        String amountLeftInBudget = budgetService.createBudgetEntry(budgetDto);
        List<BudgetDto> newBudgetForm = budgetService.getbudgetCategoriesList(budgetDto.getMonth(), budgetDto.getYear(), budgetDto.getTotalBudgetAmount());
        model.addAttribute("BudgetMessage", budgetService.getBudgetPageMessage(budgetDto.getMonth()));
        model.addAttribute("newBudgetList", newBudgetForm);
        model.addAttribute("BudgetAmountLeft", amountLeftInBudget);
        return "newBudgetForm";
    }

    @GetMapping("/editCurrentBudget")
    public  String editBudgetForm(Model model){

        BudgetDto budgetDto=budgetService.getCurrentMonthlyBudgetToEdit();
        model.addAttribute("budgetDto",budgetDto);
        return "editBudgetForm";
    }

    @PostMapping("/editBudget")
    public String editBudget(@ModelAttribute("budgetDto") BudgetDto budgetDto, Model model){

        Double amountLeftInBudget= budgetService.editCurrentBudgetTotalAmount(budgetDto);
        List<BudgetDto> newBudgetForm=budgetService.getbudgetCategoriesList(utilityService.getCurrentMonth(),utilityService.getCurrentYear(),budgetDto.getTotalBudgetAmount());
        model.addAttribute("BudgetMessage",budgetService.getBudgetPageMessage(utilityService.getCurrentMonth()));
        model.addAttribute("newBudgetList",newBudgetForm);
        model.addAttribute("BudgetAmountLeft",amountLeftInBudget + " Rs. Left in Budget");
        return "newBudgetForm";

    }
}
