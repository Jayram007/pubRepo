package com.ExpenceManager.expencemanager.dto;

import com.ExpenceManager.expencemanager.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    private long id;

    private String name;

    private List<Transaction> transaction;

    private Double budgetAmount;

    private Double usedAmount=0D;
}
