package com.ExpenceManager.expencemanager.service;

import com.ExpenceManager.expencemanager.dto.FilterTransactionDto;
import com.ExpenceManager.expencemanager.dto.SearchDto;
import com.ExpenceManager.expencemanager.dto.TransactionDto;
import com.ExpenceManager.expencemanager.dto.ViewDto;
import com.ExpenceManager.expencemanager.entity.Transaction;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface TransactionService {

    public Transaction dtoToEntity(TransactionDto transactionDto)throws ParseException;
    public TransactionDto entityToDto(Transaction transaction,boolean isUpdate)throws ParseException;

    void addNewTransaction(TransactionDto transactionDto) throws ParseException;
    void addNewFileTransaction(Transaction transaction);

    List<TransactionDto> getRecentTransactions();
    List<TransactionDto> getAllTransactions(Long id);

    Double getExpenseAmount(String type);

    TransactionDto getTransactionById(long id,boolean isUpdate);

    String getMessage(long id);

    Double getBalance(Double expenseAmount, Double incomeAmount);

    void deleteTransaction(long id);

    ViewDto getSearchedTransactions(SearchDto search);

    ViewDto getMonthlyView(long detailId);

    ViewDto getView(List<Transaction> transactionList);

    ViewDto getYearlyView(long detailId) throws IOException;

    ViewDto getFullView() throws IOException;

    void addFileTransaction();

    void deleteBatchTransaction();

    ViewDto getFilteredTransactions(FilterTransactionDto requredtransaction) throws ParseException;
}
