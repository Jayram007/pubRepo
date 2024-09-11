package com.ExpenceManager.expencemanager.service;

import com.ExpenceManager.expencemanager.dto.CategoryDto;
import com.ExpenceManager.expencemanager.entity.Category;

import java.util.List;

public interface CategoryService {

    public Category dtoToEntity(CategoryDto categoryDto);

    CategoryDto entityToDto(Category category);

    public void addNewCategory(CategoryDto categoryDto);

    List<Category> getAllCategory();

    List<CategoryDto> getCategoryList();
}
