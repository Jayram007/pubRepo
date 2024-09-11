package com.ExpenceManager.expencemanager.entity;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Table(name = "Important_values")
@Getter
@Setter
@Entity
public class ImpValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name = "Value_id")
    private long id;

    private String valueName;

    private Double impValue;
}
