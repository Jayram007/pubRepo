package com.ExpenceManager.expencemanager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonthlyBudgetDto {

    private String year;

    private  String month;

    private double usedAmount;

    private double totalMonthlyBudgetDto;

     public MonthlyBudgetDto(String year,String month,Double usedAmount,Double totalMonthlyBudgetDto){
        this.year=year;
        this.month=month;
        this.usedAmount=usedAmount;
        this.totalMonthlyBudgetDto=totalMonthlyBudgetDto;
    }

}
