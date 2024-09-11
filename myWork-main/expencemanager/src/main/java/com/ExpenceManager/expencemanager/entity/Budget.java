package com.ExpenceManager.expencemanager.entity;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Table(name = "Budget")
@Entity
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name = "budget_id")
    private long id;

    @Column(name = "Year")
    private String year;

    @Column(name = "Month")
    private String month;

    @Column(name = "Allocated_Budget_Amount")
    private Double budgetAmount=0D;

    @Column(name = "Total_Budget_Amount")
    private Double totalBudgetAmount=0D;

    @Column(name = "Used_Amount")
    private Double usedAmount=0D;

    @NotNull
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "Category_id")
    private Category category;

}
