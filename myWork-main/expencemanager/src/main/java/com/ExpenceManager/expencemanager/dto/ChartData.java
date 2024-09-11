package com.ExpenceManager.expencemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChartData {

    int date;
    Double amount;
    int year;
    int month;
    String type;

    public ChartData(int date, Double amount){
        this.date=date;
        this.amount=amount;
    }

    public ChartData(int year, int month,Double amount,String type){
        this.year=year;
        this.month=month;
        this.amount=amount;
        this.type=type;
    }
}
