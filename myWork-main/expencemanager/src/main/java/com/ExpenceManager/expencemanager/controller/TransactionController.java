package com.ExpenceManager.expencemanager.controller;

import com.ExpenceManager.expencemanager.dto.TransactionDto;
import com.ExpenceManager.expencemanager.service.CategoryService;
import com.ExpenceManager.expencemanager.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;


@RequestMapping
@Controller
public class TransactionController {


    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/addTransaction")
    public String addTransaction(@ModelAttribute("transactionDto") TransactionDto transactionDto,Model model) throws ParseException {

        transactionService.addNewTransaction(transactionDto);
        model.addAttribute("categoryDtoList",categoryService.getCategoryList());
       return "addTransactionForm";
    }

    @GetMapping("/addTransactionForm")
    public  String addTransactionForm(Model model){
        model.addAttribute("categoryDtoList",categoryService.getCategoryList());
        return "addTransactionForm";
    }

    @GetMapping("/updateTransaction/{id}")
    public  String updateTransaction(@PathVariable("id") long id, Model model) throws ParseException {

        TransactionDto transactionDto=transactionService.getTransactionById(id,true) ;
        model.addAttribute("transactionDto",transactionDto);
        model.addAttribute("categoryDtoList",categoryService.getCategoryList());
        return "updateTransactionForm";
    }
    @GetMapping("/deleteTransaction/{id}")
    public String deleteTransaction(@PathVariable("id") long id){
        transactionService.deleteTransaction(id);
        return "redirect:/";
    }

    @GetMapping("/seeAllTransactions/{id}")
    public String seeAllTransactions(@PathVariable("id") long id,Model model){

        List<TransactionDto> allTransactionList = transactionService.getAllTransactions(id);
        model.addAttribute("transactionDtoList", allTransactionList);
        model.addAttribute("message", transactionService.getMessage(id));
        return "AllTransactionsList";
    }

    @GetMapping("/saveFileTransaction")
    public String addFileTransaction() {
        transactionService.addFileTransaction();
        return "addTransactionForm";
    }

    @GetMapping("/deleteFileTransaction")
    public String deleteFileTransaction() {
        transactionService.deleteBatchTransaction();
        return "redirect:/";
    }
}
