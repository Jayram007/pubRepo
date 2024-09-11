package com.ExpenceManager.expencemanager.controller;

import com.ExpenceManager.expencemanager.dto.CategoryDto;
import com.ExpenceManager.expencemanager.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @PostMapping("/AddNewCategory")
    public String newCategory(@ModelAttribute("categoryDto") CategoryDto categoryDto,Model model){

        categoryService.addNewCategory(categoryDto);
        model.addAttribute("categoryDtoList",categoryService.getCategoryList());
        return "addNewCategoryForm";
    }

    @GetMapping("/addCategory")
    public  String addCategoryForm(Model model){

        model.addAttribute("categoryDtoList",categoryService.getCategoryList());
        return "addNewCategoryForm";
    }
}
