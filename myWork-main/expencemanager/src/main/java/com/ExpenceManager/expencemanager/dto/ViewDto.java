package com.ExpenceManager.expencemanager.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ViewDto {

    private Long detailId;

    private String detailMessage;

    private  String income= "0";

    private  String expense="0";

    private  String balance="0";

    private  Double maxIncome=0D;

    private  Double maxExpense=0D;

    private  Double minIncome=0D;

    private  Double minExpense=0D;

    private  Double averageIncomePerTransaction=0D;

    private  Double averageExpensePerTransaction=0D;

    private Double averageIncomePerMonth=0D;

    private Double averageExpensePerMonth=0D;

    private List<TransactionDto> transactionDtoList;

    private List<CategoryDto> categoryDtoList;

    private List<CategoryTransactionDto>categoryTransactionDtoList;

    private Map<String, Double> incomeGraphData;

    private Map<String, Double> expenseGraphData;

    private Map<String, Double> yearlyIncomeChart;

    private Map<String, Double> yearlyExpenseChart;

    private Map<String, Double> monthlyIncomeChart;

    private Map<String, Double> monthlyExpenseChart;

    private String incomeChartURL="";

    private String expenseChartURL="";

}
