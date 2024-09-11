package com.ExpenceManager.expencemanager.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class FileTransactionDto {
    private long id;
    private LocalDateTime Date;
    private String CategoryName;
    private double amount;
    private String note;
    private String type;
}
