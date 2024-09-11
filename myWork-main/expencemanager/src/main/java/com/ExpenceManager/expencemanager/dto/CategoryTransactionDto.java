package com.ExpenceManager.expencemanager.dto;

import com.ExpenceManager.expencemanager.entity.Category;
import com.ExpenceManager.expencemanager.service.CategoryService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
@NoArgsConstructor
public class CategoryTransactionDto {

    @Autowired
    CategoryService categoryService;

    private String type;
    private double usedAmount;
    private String note;
    private Category category;
    private  double averagePerMonth;

    public CategoryTransactionDto(String type,Double usedAmount,String note,Category category){
        this.type=type;
        this.usedAmount=usedAmount;
        this.note=note;
        this.category=category;
    }


}
