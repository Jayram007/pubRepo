package com.ExpenceManager.expencemanager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterTransactionDto {

    private String fromDate;

    private String toDate;

    private String note;

    private Double amountFrom;

    private Double amountTo;

    private long categoryId;

    private String categoryName;

    private String type;
}
