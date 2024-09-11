package com.ExpenceManager.expencemanager.dto;

import com.ExpenceManager.expencemanager.entity.Category;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
public class TransactionDto {


    private long id;

    private String date;

    private String note;

    private Double amount;

    private long categoryId;

    private String categoryName;

    private String type;
}
