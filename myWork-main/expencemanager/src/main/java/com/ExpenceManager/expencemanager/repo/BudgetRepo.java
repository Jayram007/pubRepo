package com.ExpenceManager.expencemanager.repo;

import com.ExpenceManager.expencemanager.dto.MonthlyBudgetDto;
import com.ExpenceManager.expencemanager.entity.Budget;
import com.ExpenceManager.expencemanager.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepo extends JpaRepository<Budget,Long> {

    @Query(value = "SELECT sum(b.budgetAmount) FROM Budget b WHERE b.month= ?1 and b.year= ?2")
    Double sumTotalBudget(String month, String year);

    @Query(value = "SELECT sum(b.usedAmount) FROM Budget b WHERE b.month= ?1 and b.year= ?2")
    Double sumOfUsedAmount(String month, String year);

    List<Budget> findByYearAndMonth(String year, String month);

    @Query(value = "SELECT count(b) FROM Budget b WHERE b.year= ?1 and b.month= ?2 and b.totalBudgetAmount= 0")
    int count(String year, String month);

    Budget findByYearAndMonthAndCategory(String year, String month, Category category);

    @Query(value = "SELECT NEW com.ExpenceManager.expencemanager.dto.MonthlyBudgetDto( b.year,b.month,sum(b.usedAmount),b.totalBudgetAmount) FROM Budget b WHERE b.year = ?1 and b.month = ?2 group by ?1 , ?2")
    MonthlyBudgetDto findTotalMonthlyBudget(String year, String month);

    @Query(value = "SELECT max(b.totalBudgetAmount) FROM Budget b WHERE b.year= ?1 and b.month= ?2")
    Double totalBudget(String year, String month);

    double countByCategory(Category category);
}
