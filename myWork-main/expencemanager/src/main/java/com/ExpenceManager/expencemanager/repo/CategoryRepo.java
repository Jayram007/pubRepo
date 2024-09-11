package com.ExpenceManager.expencemanager.repo;

import com.ExpenceManager.expencemanager.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepo extends JpaRepository<Category,Long> {

    @Query("SELECT c FROM Category c WHERE c.id = ?1")
    Category findById(long categoryid);

    Category findByName(String name);
}
