package com.ExpenceManager.expencemanager.service;


import com.ExpenceManager.expencemanager.constants.Constants;
import com.ExpenceManager.expencemanager.dto.*;
import com.ExpenceManager.expencemanager.entity.BatchTransactions;
import com.ExpenceManager.expencemanager.entity.Budget;
import com.ExpenceManager.expencemanager.entity.Transaction;
import com.ExpenceManager.expencemanager.repo.*;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {


    @Autowired
    TransactionRepo transactionRepo;

    @Autowired
    CategoryRepo categoryRepo;

    @Autowired
    BudgetRepo budgetRepo;

    @Autowired
    BudgetService budgetService;

    @Autowired
    UtilityService utilityService;

    @Autowired
    ChartService chartService;

    @Autowired
    BatchTransactionRepo batchTransactionRepo;

    @Autowired
    TransactionRepoBean transactionRepoBean;

       public Transaction dtoToEntity(TransactionDto transactionDto) throws ParseException {

        Transaction transaction = new Transaction();
        if (transactionDto.getId() != 0)
            transaction.setId(transactionDto.getId());
        transaction.setAmount(transactionDto.getAmount());
        transaction.setNote(transactionDto.getNote());
        transaction.setId(transactionDto.getId());
        transaction.setType(transactionDto.getType());
        transaction.setCategory(categoryRepo.findById(transactionDto.getCategoryId()));
        //transaction.setDate(LocalDate.parse(transactionDto.getDate()));
        transaction.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(transactionDto.getDate()));
        return transaction;
    }

    public TransactionDto entityToDto(Transaction transaction, boolean isUpdate) {

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setId(transaction.getId());
        transactionDto.setType(transaction.getType());
        transactionDto.setNote(transaction.getNote());
        //transactionDto.setDate(transaction.getDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
        if (isUpdate)
            transactionDto.setDate(new SimpleDateFormat("yyyy-MM-dd").format(transaction.getDate()));
        else
            transactionDto.setDate(new SimpleDateFormat("dd/MM/yyyy").format(transaction.getDate()));
        transactionDto.setAmount(transaction.getAmount());
        transactionDto.setCategoryId(transaction.getCategory().getId());
        transactionDto.setCategoryName(transaction.getCategory().getName());
        return transactionDto;
    }

    @Override
    public void addNewTransaction(TransactionDto transactionDto) throws ParseException {
        Transaction transaction = dtoToEntity(transactionDto);
        transactionRepo.save(transaction);
        budgetService.addBudgetTransaction(transaction);
    }

    @Override
    public void addNewFileTransaction(Transaction transaction) {
        transactionRepo.save(transaction);
        budgetService.addBudgetTransaction(transaction);
    }

    @Override
    public List<TransactionDto> getRecentTransactions() {
        return getTransactions(0, 9);
    }

    public List<TransactionDto> getTransactions(int pageNo, int pageSize) {
        //limit the records
        Page<Transaction> page = transactionRepo.findAll(
                PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "date").and(Sort.by(Sort.Direction.DESC, "id"))));
        List<TransactionDto> transactionDtoList = new ArrayList<TransactionDto>();
        for (Transaction transaction : page) {
            TransactionDto transactionDto = entityToDto(transaction, false);
            transactionDtoList.add(transactionDto);
        }
        return transactionDtoList;
    }

    @Override
    public List<TransactionDto> getAllTransactions(Long id) {
        List<Transaction> transactionList = new ArrayList<Transaction>();
        if (id == 0)//All Transactions
            return getTransactions(0, 1000);
        else if (id == -1) {//monthly income transactions
            transactionList = transactionRepo.findTransactionsOnDate(Constants.TRANSACTION_TYPE_INCOME, utilityService.getCurrentMonthsFirstDate(), utilityService.getCurrentMonthsLastDate());
            return listConversion(transactionList);
        } else if (id == -2) {
            transactionList = transactionRepo.findTransactionsOnDate(Constants.TRANSACTION_TYPE_EXPENSE, utilityService.getCurrentMonthsFirstDate(), utilityService.getCurrentMonthsLastDate());
            return listConversion(transactionList);
        } else if (id == -3) {
            transactionList = transactionRepo.findTransactions(Constants.TRANSACTION_TYPE_EXPENSE);
            return listConversion(transactionList);
        } else
            transactionList = transactionRepo.findTransactionByBudgetCategory(Constants.TRANSACTION_TYPE_EXPENSE, categoryRepo.findById(id), utilityService.getCurrentMonthsFirstDate(), utilityService.getCurrentMonthsLastDate());
        return listConversion(transactionList);
    }

    private List<TransactionDto> listConversion(List<Transaction> transactionList) {
        TransactionDto transactionDto = new TransactionDto();
        List<TransactionDto> transactionDtoList = new ArrayList<TransactionDto>();
        for (Transaction transaction : transactionList) {
            transactionDtoList.add(entityToDto(transaction, false));
        }
        return transactionDtoList;
    }

    @Override
    public Double getExpenseAmount(String type) {
        Double expenseAmount = transactionRepo.sumAmount(type, utilityService.getCurrentMonthsFirstDate(), utilityService.getCurrentMonthsLastDate());
        if (expenseAmount == null)//todo:optimize
            return 0D;
        return expenseAmount;
    }

    @Override
    public TransactionDto getTransactionById(long id, boolean isUpdate) {
        Transaction transaction = transactionRepo.findById(id);
        return entityToDto(transaction, isUpdate);
    }

    @Override
    public String getMessage(long id) {
        if (id == 1 || id== 0)
            return "All Transaction List";
        else if (id == 2 || id== -1)
            return "Income Transactions";
        else if (id == 3 || id== -2)
            return "Expense Transactions";
        else
            return "No message";
    }

    @Override
    public Double getBalance(Double expenseAmount, Double incomeAmount) {
        return (incomeAmount - expenseAmount);
    }

    @Override
    public void deleteTransaction(long id) {
        Transaction transaction = transactionRepo.findById(id);
        transactionRepo.deleteById(id);
        budgetService.addBudgetTransaction(transaction);
    }

    @Override
    public ViewDto getSearchedTransactions(SearchDto search) {

        List<Transaction> transactionList = transactionRepo.getTransactionsForSearch(search.getSearchKey());
        ViewDto viewDto = getView(transactionList);
        viewDto.setCategoryTransactionDtoList(transactionRepo.getCategoryWiseTransaction(search.getSearchKey()));
        return viewDto;
    }

    @Override
    public ViewDto getMonthlyView(long detailId) {
        Date startDate = utilityService.getCurrentMonthsFirstDate();
        Date endDate = utilityService.getCurrentMonthsLastDate();

        if (detailId != 0) {
            int month = (int) detailId / 10000;
            int year = (int) detailId % 10000;
            startDate.setDate(1);
            startDate.setMonth(month - 1);
            startDate.setYear(year - 1900);
            endDate = utilityService.getLastDate(startDate);
        }
        List<Transaction> transactionList = transactionRepo.findTransactionsBetweenDate(startDate, endDate);
        ViewDto viewDto = getView(transactionList);
        viewDto.setDetailId(createViewId(startDate));
        viewDto.setCategoryTransactionDtoList(transactionRepo.getCategoryWiseTransaction(startDate, endDate));
        viewDto.setDetailMessage(utilityService.getMonthString(startDate) + " " + utilityService.getYearByDate(startDate));
        viewDto.setAverageIncomePerMonth(setAveragePerMonthByDate(Double.parseDouble(viewDto.getIncome().replace(",","")), endDate.getMonth() + 1));
        viewDto.setAverageExpensePerMonth(setAveragePerMonthByDate(Double.parseDouble(viewDto.getExpense().replace(",","")), endDate.getMonth() + 1));
        chartService.setCategoryChartData(viewDto);

        return viewDto;
    }

    @Override
    public ViewDto getYearlyView(long detailId) throws IOException {
        Date startDate = utilityService.getYearFirstDateByDetailsId(detailId);
        Date endDate = utilityService.getYearLastDateByDetailId(detailId);
        List<Transaction> transactionList = transactionRepo.findTransactionsBetweenDate(startDate, endDate);
        ViewDto viewDto = getView(transactionList);
        viewDto.setDetailId((long) startDate.getYear() + 1900);
        viewDto.setCategoryTransactionDtoList(transactionRepo.getCategoryWiseTransaction(startDate, endDate));
        viewDto.setCategoryTransactionDtoList(viewDto.getCategoryTransactionDtoList().stream().map(categoryTransactionDto -> {
                categoryTransactionDto.setAveragePerMonth(Precision.round( categoryTransactionDto.getUsedAmount() / 12,2));
            return categoryTransactionDto;
        }).collect(Collectors.toList())
        );
        viewDto.setDetailMessage(utilityService.getYearByDate(startDate));
        viewDto.setAverageIncomePerMonth(setAveragePerMonthByDate(Double.parseDouble(viewDto.getIncome().replace(",","")), endDate.getMonth() + 1));
        viewDto.setAverageExpensePerMonth(setAveragePerMonthByDate(Double.parseDouble(viewDto.getExpense().replace(",","")), endDate.getMonth() + 1));
        chartService.setCategoryChartData(viewDto);
        chartService.getMothlyChart(viewDto,startDate.getYear());
        viewDto.setIncomeChartURL(chartService.setLineChartData(Constants.TRANSACTION_TYPE_INCOME));
        viewDto.setExpenseChartURL(chartService.setLineChartData(Constants.TRANSACTION_TYPE_EXPENSE));
        return viewDto;
    }

    @Override
    public ViewDto getFullView() throws IOException {
        List<Transaction> transactionList = transactionRepo.findAll();
        ViewDto viewDto = getView(transactionList);
        viewDto.setCategoryTransactionDtoList(transactionRepo.getAllTimeCategoryWiseTransaction());
        Optional<Budget> budget=budgetRepo.findById(1L);
            Period period =  Period.between(LocalDate.of(Integer.parseInt(budget.get().getYear()), Integer.parseInt( budget.get().getMonth()), 1),LocalDate.now());
        int months = period.getYears() * 12 + period.getMonths();

            viewDto.setCategoryTransactionDtoList(viewDto.getCategoryTransactionDtoList().stream().map(categoryTransactionDto -> {
                        categoryTransactionDto.setAveragePerMonth(Precision.round(categoryTransactionDto.getUsedAmount() / months, 2));
                        return categoryTransactionDto;
                    }).collect(Collectors.toList())
            );
        chartService.setCategoryChartData(viewDto);
        chartService.setYearlyChartData(viewDto);
        viewDto.setAverageIncomePerMonth(Precision.round( Double.parseDouble(viewDto.getIncome().replace(",","")) / months, 2));
        viewDto.setAverageExpensePerMonth(Precision.round( Double.parseDouble(viewDto.getExpense().replace(",","")) / months, 2));
        viewDto.setIncomeChartURL(chartService.setLineChartData(Constants.TRANSACTION_TYPE_INCOME));
        viewDto.setExpenseChartURL(chartService.setLineChartData(Constants.TRANSACTION_TYPE_EXPENSE));
        viewDto.setDetailId(-1L);
        return  viewDto;
    }

    @Override
    public void addFileTransaction() {
        List <BatchTransactions> transactions= batchTransactionRepo.findAll();
        for (BatchTransactions transaction:transactions
             ) {
            addNewFileTransaction(getFileTransaction(transaction) );
        }
        batchTransactionRepo.deleteAll();
    }

    @Override
    public void deleteBatchTransaction() {
        batchTransactionRepo.deleteAll();
    }

    private Transaction getFileTransaction(BatchTransactions transaction) {
        Transaction newTransaction = new Transaction();
        newTransaction.setType(transaction.getType());
        newTransaction.setCategory(transaction.getCategory());
        newTransaction.setDate(transaction.getDate());
        newTransaction.setAmount(transaction.getAmount());
        newTransaction.setNote(transaction.getNote());
        return newTransaction;
    }

    private Double setAveragePerMonthByDate(Double income, int months) {
        return income/months;
    }

    private Long createViewId(Date startDate) {
        long month = (startDate.getMonth()+1) * 10000l;
        long year= 1900l + startDate.getYear();
        return month+year;
    }

    @Override
    public ViewDto getView(List<Transaction> transactionList) {
        ViewDto viewDto = new ViewDto();
        int incomeTransaction = 0;
        int expenseTransaction = 0;
        List<TransactionDto> transactionDtoList = new ArrayList<>();
        for (Transaction transaction : transactionList) {
            transactionDtoList.add(entityToDto(transaction, false));
            if (transaction.getType().equals(Constants.TRANSACTION_TYPE_INCOME)) {
                incomeTransaction++;
                setIncomeView(viewDto,transaction);
            } else if (transaction.getType().equals(Constants.TRANSACTION_TYPE_EXPENSE)) {
                expenseTransaction++;
                setExpenseView(viewDto,transaction);
            }
        }
        viewDto.setAverageIncomePerTransaction(utilityService.getAveragePerTransactionAmount(Double.parseDouble(viewDto.getIncome()), incomeTransaction));
        viewDto.setAverageExpensePerTransaction(utilityService.getAveragePerTransactionAmount(Double.parseDouble(viewDto.getExpense()), expenseTransaction));
        viewDto.setTransactionDtoList(transactionDtoList);
        viewDto.setBalance(utilityService.setTruncatedData(Double.parseDouble(viewDto.getIncome()) - Double.parseDouble(viewDto.getExpense())));
        viewDto.setIncome(utilityService.setTruncatedData(Double.parseDouble(viewDto.getIncome())));
        viewDto.setExpense(utilityService.setTruncatedData(Double.parseDouble(viewDto.getExpense())));

        return viewDto;
    }

    private void setExpenseView(ViewDto viewDto, Transaction transaction) {
        viewDto.setExpense(String.valueOf(transaction.getAmount() + Double.parseDouble(viewDto.getExpense())));
        if (viewDto.getMaxExpense() == null || viewDto.getMaxExpense() < transaction.getAmount()) {
            viewDto.setMaxExpense(transaction.getAmount());
        }
        if (viewDto.getMinExpense() == null || viewDto.getMinExpense() > transaction.getAmount()) {
            viewDto.setMinExpense(transaction.getAmount());
        }
    }

    private void setIncomeView(ViewDto viewDto, Transaction transaction) {
        viewDto.setIncome(String.valueOf( transaction.getAmount() + Double.parseDouble(viewDto.getIncome())));
        if (viewDto.getMaxIncome() == null || viewDto.getMaxIncome() < transaction.getAmount()) {
            viewDto.setMaxIncome(transaction.getAmount());
        }
        if (viewDto.getMinIncome() == null || viewDto.getMinIncome() > transaction.getAmount()) {
            viewDto.setMinIncome(transaction.getAmount());}
    }
    @Override
    public ViewDto getFilteredTransactions(FilterTransactionDto requiredTransaction) throws ParseException {

        List<Transaction> transactionList = transactionRepoBean.findByCriteria(requiredTransaction);
        ViewDto viewDto = getView(transactionList);
        viewDto.setTransactionDtoList(this.listConversion(transactionList));
        viewDto.setDetailId(-1L);
        getCategorywiseData(viewDto, transactionList);
        return viewDto;
    }

    private void getCategorywiseData(ViewDto viewDto, List<Transaction> transactionList) {

        Map<String, Double> incomeCategoryTransactions = new HashMap<>();
        Map<String, Double> expenseCategoryTransactions = new HashMap<>();
        double usedAmount;

        for (Transaction transaction : transactionList
        ) {
            if (transaction.getType().equals(Constants.TRANSACTION_TYPE_INCOME)) {
                if (incomeCategoryTransactions.containsKey(transaction.getCategory().getName())) {
                    usedAmount = incomeCategoryTransactions.get(transaction.getCategory().getName());
                    usedAmount += transaction.getAmount();
                    incomeCategoryTransactions.replace(transaction.getCategory().getName(), usedAmount);
                } else {
                    incomeCategoryTransactions.put(transaction.getCategory().getName(), transaction.getAmount());
                }
            } else if (transaction.getType().equals(Constants.TRANSACTION_TYPE_EXPENSE)) {
                if (expenseCategoryTransactions.containsKey(transaction.getCategory().getName())) {
                    usedAmount = expenseCategoryTransactions.get(transaction.getCategory().getName());
                    usedAmount += transaction.getAmount();
                    expenseCategoryTransactions.replace(transaction.getCategory().getName(), usedAmount);
                } else {
                    expenseCategoryTransactions.put(transaction.getCategory().getName(), transaction.getAmount());
                }
            }
        }
        viewDto.setIncomeGraphData(incomeCategoryTransactions);
        viewDto.setExpenseGraphData(expenseCategoryTransactions);
    }
}
