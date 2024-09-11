package com.ExpenceManager.expencemanager.controller;

import com.ExpenceManager.expencemanager.dto.FileTransactionDto;
import com.ExpenceManager.expencemanager.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;
import java.util.List;

@Controller
public class fileController {

    @Autowired
    FileService fileService;

    @GetMapping("/uploadFile")
    public String uploadFileForm(){
        return "UploadFileForm";
    }


    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file,Model model) {
        if (fileService.checkExcelFormat(file)) {

            List<FileTransactionDto> transactionDtoList= fileService.saveFile(file);
            model.addAttribute("transactionDtoList", transactionDtoList);
            if(!transactionDtoList.isEmpty())
                model.addAttribute("message", "File Transaction");
            else
                model.addAttribute("message", "No data");
            return"fileUploadedTransactions";

        }
        return"redirect:/";
    }
}
