package com.ExpenceManager.expencemanager.dto;

import com.ExpenceManager.expencemanager.entity.Category;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BudgetDto {

    private long id;

    private String year;

    private String month;

    private Double budgetAmount=0D;

    private Double totalBudgetAmount=0D;

    private Double usedAmount=0D;

    private CategoryDto categoryDto;

    private long categoryId;

    private String message;

    private String percentage;

}
