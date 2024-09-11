package com.ExpenceManager.expencemanager.entity;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Table(name = "Batch_Transaction")
@Entity
public class BatchTransactions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name = "Transaction_id")
    private long id;

    @NotNull
    @Column(name = "Date")
    private Date date;

    @NotNull
    @Column(name = "Note")
    private String note;

    @NotNull
    @Column(name = "Amount")
    private Double amount;

    @NotNull
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "Category_id")
    private Category category;

    @NotNull
    @Column(name = "Type")
    private String type;

}
