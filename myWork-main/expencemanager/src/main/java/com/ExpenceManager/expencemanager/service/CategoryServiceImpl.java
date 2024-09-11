package com.ExpenceManager.expencemanager.service;

import com.ExpenceManager.expencemanager.dto.CategoryDto;
import com.ExpenceManager.expencemanager.entity.Category;
import com.ExpenceManager.expencemanager.repo.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements  CategoryService{

    @Autowired
    CategoryRepo categoryRepo;

    @Override
    public Category dtoToEntity(CategoryDto categoryDto) {

        Category category=new Category();
        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());
        category.setTransaction(categoryDto.getTransaction());
        return category;
    }

    @Override
    public CategoryDto entityToDto(Category category) {

        CategoryDto categoryDto=new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        return categoryDto;
    }


    public void addNewCategory(CategoryDto categoryDto){

        Category category=dtoToEntity(categoryDto);
        Category category1= categoryRepo.findByName(category.getName());
        if(category1==null) {
            categoryRepo.save(category);
        }
    }

    @Override
    public List<Category> getAllCategory() {
        return  categoryRepo.findAll();
    }

    @Override
    public List<CategoryDto> getCategoryList() {

        CategoryDto categoryDto=new CategoryDto();
        List<Category>categoryList=getAllCategory();

        List<CategoryDto> allCategoryList=new ArrayList<CategoryDto>();

        int i=1;
        for (Category category: categoryList
             ) {
            categoryDto=entityToDto(category);
            if(categoryDto.getBudgetAmount()==null) {
                categoryDto.setBudgetAmount(0D);
            }
            allCategoryList.add(categoryDto);
        }
       return allCategoryList;
    }


}
