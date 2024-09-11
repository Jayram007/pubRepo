package com.ExpenceManager.expencemanager.service;

import com.ExpenceManager.expencemanager.dto.FileTransactionDto;
import com.ExpenceManager.expencemanager.entity.BatchTransactions;
import com.ExpenceManager.expencemanager.entity.Category;
import com.ExpenceManager.expencemanager.repo.BatchTransactionRepo;
import com.ExpenceManager.expencemanager.repo.CategoryRepo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Transactional
public class FileServiceImpl implements FileService{

    @Autowired
    CategoryRepo categoryRepo;

    @Autowired
    BatchTransactionRepo batchTransactionRepo;

    @Override
    public boolean checkExcelFormat(MultipartFile file) {

        String contentType = file.getContentType();
        return contentType != null && contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }
    @Override
    public List<FileTransactionDto> saveFile(MultipartFile file) {
        try {
            List<FileTransactionDto> list =  convertExcelToListOfTransactions(file.getInputStream());
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public List<FileTransactionDto> convertExcelToListOfTransactions(InputStream inputStream) {
        List<FileTransactionDto> list = new ArrayList<>();

        if(batchTransactionRepo.count()<1) {

            try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {

                XSSFSheet sheet = workbook.getSheet("data");

                int rowNumber = 0;
                Iterator<Row> iterator = sheet.iterator();

                while (iterator.hasNext()) {
                    Row row = iterator.next();

                    if (rowNumber == 0) {
                        rowNumber++;
                        continue;
                    }

                    Iterator<Cell> cells = row.iterator();
                    int cid = 0;
                    FileTransactionDto transaction = new FileTransactionDto();

                    while (cells.hasNext()) {
                        Cell cell = cells.next();

                        switch (cid) {
                            case 0:
                                transaction.setDate(cell.getLocalDateTimeCellValue());
                                break;
                            case 1:
                                transaction.setCategoryName(cell.getStringCellValue());
                                break;
                            case 2:
                                transaction.setAmount(cell.getNumericCellValue());
                                break;
                            case 3:
                                transaction.setNote(cell.getStringCellValue());
                                break;
                            case 4:
                                transaction.setType(cell.getStringCellValue());
                                break;
                            default:
                                break;
                        }
                        cid++;
                    }
                    list.add(transaction);
                    batchTransactionRepo.save(fileDtoToEntity(transaction));
                }
            } catch (Exception e) {
                System.out.println("problem with record number : " + list.size());
                e.printStackTrace();
            } finally {
                System.out.println("Records uploaded : " + list.size());
            }
        }
        return list;

    }

    @Override
    public BatchTransactions fileDtoToEntity(FileTransactionDto fileTransaction) {

        BatchTransactions transaction = new BatchTransactions();
        transaction.setDate(java.sql.Timestamp.valueOf(fileTransaction.getDate()));
        transaction.setAmount(fileTransaction.getAmount());
        Category category = categoryRepo.findByName(fileTransaction.getCategoryName());
        if (category != null) {
            transaction.setCategory(category);
        } else {
            Category newCategory = new Category();
            newCategory.setName(fileTransaction.getCategoryName());
            categoryRepo.save(newCategory);
            newCategory = categoryRepo.findByName(fileTransaction.getCategoryName());
            transaction.setCategory(newCategory);
        }
        transaction.setNote(fileTransaction.getNote());
        transaction.setType(fileTransaction.getType());
        return transaction;
    }
}
