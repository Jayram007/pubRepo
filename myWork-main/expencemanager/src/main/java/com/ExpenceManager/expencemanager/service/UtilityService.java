package com.ExpenceManager.expencemanager.service;


import com.ExpenceManager.expencemanager.entity.Category;

import java.util.Date;

public interface UtilityService {

    String getCurrentMonth();
    Date getCurrentMonthsFirstDate();

    Date getCurrentMonthsLastDate();

    int getPerDayUse(Double usedAmount, double v);

    String getYearByDate(Date date);

    String getMonthByDate(Date date);

    Date getMonthFirstDate(Date date);

    Date getLastDate(Date date);

    String getCurrentYear();

    Double getCurrentDate();

    Double getAveragePerTransactionAmount(Double amount, int transactionAmount);

    String getMonthString(Date startDate);
    String getMonthString(int month);

    Date getYearFirstDateByDetailsId(double detailId);

    Date getYearLastDateByDetailId(double detailId);

    String setTruncatedData(Double amount);
}
