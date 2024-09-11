package com.ExpenceManager.expencemanager.service;

import com.ExpenceManager.expencemanager.dto.FileTransactionDto;
import com.ExpenceManager.expencemanager.entity.BatchTransactions;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface FileService {
    boolean checkExcelFormat(MultipartFile file);

    List<FileTransactionDto> saveFile(MultipartFile file);

    List<FileTransactionDto> convertExcelToListOfTransactions(InputStream inputStream);

    BatchTransactions fileDtoToEntity(FileTransactionDto fileTransaction);

}
