package com.ExpenceManager.expencemanager.service;

import com.ExpenceManager.expencemanager.constants.Constants;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@Service
@Transactional
public class UtilityServiceImpl implements UtilityService {
    @Override
    public String getCurrentMonth() {
        LocalDate now = LocalDate.now();
        now.getMonth();
        String currentMonth = now.toString();
        return currentMonth.substring(5, 7);
    }

    @Override
    public Date getCurrentMonthsFirstDate() {
        LocalDate now = LocalDate.now();
        return java.sql.Date.valueOf(now.withDayOfMonth(1));
    }

    @Override
    public Date getCurrentMonthsLastDate() {
        LocalDate now = LocalDate.now();
        now.getMonth();
        Date endDate = java.sql.Date.valueOf(now.withDayOfMonth(now.getMonth().length(now.isLeapYear())));
        return endDate;
    }

    @Override
    public int getPerDayUse(Double usedAmount, double days) {
        return (int) (usedAmount / days);
    }

    @Override
    public String getYearByDate(Date date) {
        int year = date.getYear();
        year += 1900;
        return String.valueOf(year);
    }

    @Override
    public String getMonthByDate(Date date) {
        int month = date.getMonth();
        month += 1;
        if (month < 10) {
            return "0" + String.valueOf(month);
        }
        return String.valueOf(month);
    }

    @Override
    public Date getMonthFirstDate(Date date) {
        LocalDate localDate = new java.sql.Date(date.getTime()).toLocalDate();
        return java.sql.Date.valueOf(localDate.withDayOfMonth(1));
    }

    @Override
    public Date getLastDate(Date date) {
        LocalDate localDate = new java.sql.Date(date.getTime()).toLocalDate();
        return java.sql.Date.valueOf(localDate.withDayOfMonth(localDate.getMonth().length(localDate.isLeapYear())));
    }

    @Override
    public String getCurrentYear() {
        LocalDate now = LocalDate.now();
        now.getYear();
        String currentMonth = now.toString();
        return currentMonth.substring(0, 4);
    }

    @Override
    public Double getCurrentDate() {
        LocalDate now = LocalDate.now();
        int today = now.getDayOfMonth();
        return (double) today;
    }

    @Override
    public Double getAveragePerTransactionAmount(Double amount, int transactionCount) {
        return Precision.round(amount / transactionCount,2);
    }

    @Override
    public String getMonthString(Date startDate) {
        if(startDate != null){
            switch (startDate.getMonth()) {
                case Calendar.JANUARY:
                    return "January ";
                case Calendar.FEBRUARY:
                    return "February ";
                case Calendar.MARCH:
                    return "March ";
                case Calendar.APRIL:
                    return "April ";
                case Calendar.MAY:
                    return "May ";
                case Calendar.JUNE:
                    return "June ";
                case Calendar.JULY:
                    return "July ";
                case Calendar.AUGUST:
                    return "August ";
                case Calendar.SEPTEMBER:
                    return "September  ";
                case Calendar.OCTOBER:
                    return "October ";
                case Calendar.NOVEMBER:
                    return "November ";
                case Calendar.DECEMBER:
                    return "December ";
                default:
                    return Constants.NO_MESSAGE;
            }
        }
        return Constants.NO_MESSAGE;
    }

    @Override
    public String getMonthString(int month) {
        switch (month) {
            case 1:
                return "January ";
            case 2:
                return "February ";
            case 3:
                return "March ";
            case 4:
                return "April ";
            case 5:
                return "May ";
            case 6:
                return "June ";
            case 7:
                return "July ";
            case 8:
                return "August ";
            case 9:
                return "September  ";
            case 10:
                return "October ";
            case 11:
                return "November ";
            case 12:
                return "December ";
            default:
                return Constants.NO_MESSAGE;
        }
    }

    @Override
    public Date getYearFirstDateByDetailsId(double detailId) {
        Date firstDate = new Date();
        firstDate.setDate(1);
        firstDate.setMonth(0);
        if (detailId == 0) {
            firstDate.setYear(getCurrentMonthsFirstDate().getYear());
        } else {
            firstDate.setYear((int) detailId - 1900);
        }
        return firstDate;
    }

    @Override
    public Date getYearLastDateByDetailId(double detailId) {
        Date lastDate = new Date();
        lastDate.setDate(31);
        lastDate.setMonth(11);
        if (detailId == 0) {
            lastDate.setYear(getCurrentMonthsFirstDate().getYear());
        } else {
            lastDate.setYear((int) detailId - 1900);
        }
        return lastDate;
    }

    @Override
    public String setTruncatedData(Double value) {

        value= Precision.round(value,2);
        String amount=value.toString();
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());

        // Set the grouping size to 2 to represent lakhs
        decimalFormat.applyPattern("#,##,##0.00");
        // decimalFormat.applyPattern();
        //NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH);

        // Format the amount using commas
        return decimalFormat.format(Double.parseDouble(amount));
    }
}
