package com.ExpenceManager.expencemanager.repo;

import com.ExpenceManager.expencemanager.dto.CategoryTransactionDto;
import com.ExpenceManager.expencemanager.dto.ChartData;
import com.ExpenceManager.expencemanager.dto.SearchDto;
import com.ExpenceManager.expencemanager.dto.TransactionDto;
import com.ExpenceManager.expencemanager.entity.Category;
import com.ExpenceManager.expencemanager.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction,Long> {

    @Query("SELECT t FROM Transaction t WHERE t.id = ?1")
    Transaction findById(long transactionId);

    @Query(value = "SELECT sum(t.amount) FROM Transaction t WHERE t.type= ?1 and (t.date between ?2 and ?3)")
    Double sumAmount(String type, Date startDate, Date endDate);

    @Query(value = "SELECT t FROM Transaction t WHERE (t.type= ?1) and (t.date between ?2 and ?3) order by date desc, id desc")
    List<Transaction> findTransactionsOnDate(String type, Date startDate, Date endDate);

    @Query(value = "SELECT t FROM Transaction t WHERE t.type= ?1 order by date desc, id desc")
    List<Transaction> findTransactions(String type);

    @Query(value = "SELECT sum(t.amount) FROM Transaction t WHERE (t.category= ?1) and (t.date between ?2 and ?3) And (t.type= ?4)")
    Double findTransactionsSumOnDate(Category category, Date startDate, Date endDate,String type);

    List<Transaction> findAllByType(String type);

    @Query(value = "SELECT t FROM Transaction t WHERE(t.type= ?1) and (t.category= ?2) and (t.date between ?3 and ?4)")
    List<Transaction> findTransactionByBudgetCategory(String expense, Optional<Category> byId, Date currentMonthsFirstDate, Date currentMonthsLastDate);

    @Query(value = "SELECT t FROM Transaction t WHERE t.note like %?1% order by date desc, id desc")
    List<Transaction> getTransactionsForSearch(String search);

    @Query(value = "SELECT NEW com.ExpenceManager.expencemanager.dto.CategoryTransactionDto ( t.type, sum(t.amount), t.note, t.category ) FROM Transaction t WHERE t.note like %?1% group by type, id order by amount desc, type desc")
    List<CategoryTransactionDto>getCategoryWiseTransaction(String search);

    @Query(value = "SELECT NEW com.ExpenceManager.expencemanager.dto.CategoryTransactionDto ( t.type, sum(t.amount), t.note, t.category ) FROM Transaction t WHERE (t.date between ?1 and ?2) group by type, id order by type desc, amount desc")
    List<CategoryTransactionDto>getCategoryWiseTransaction(Date startDate, Date endDate);

    @Query(value = "SELECT t FROM Transaction t WHERE (t.date between ?1 and ?2) order by date desc, id desc")
    List<Transaction> findTransactionsBetweenDate(Date startDate, Date endDate);

    @Query(value = "SELECT NEW com.ExpenceManager.expencemanager.dto.CategoryTransactionDto ( t.type, sum(t.amount), t.note, t.category ) FROM Transaction t  group by type, id order by type desc, amount desc")
    List<CategoryTransactionDto> getAllTimeCategoryWiseTransaction();

    @Query(value = "SELECT NEW com.ExpenceManager.expencemanager.dto.ChartData ( year(t.date), sum(t.amount) ) FROM Transaction t WHERE t.type= ?1 group by year(t.date)")
    List<ChartData> getChartData(String type);

    @Query(value = "SELECT NEW com.ExpenceManager.expencemanager.dto.ChartData ( month(t.date), sum(t.amount) ) FROM Transaction t WHERE t.type= ?1 and year(t.date)=?2 group by month(t.date) order by month(t.date) ")
    List<ChartData> getChartMontData(String type,int Year);

    @Query(value = "SELECT NEW com.ExpenceManager.expencemanager.dto.ChartData ( year(t.date),month(t.date), sum(t.amount),t.type ) FROM Transaction t WHERE year(t.date) in ( ?1, ?2) and t.type= ?3 group by t.type, year(t.date), month(t.date) order by t.type,year(t.date), month(t.date) ")
    List<ChartData> getLineChartData(int currentYear, int previousYear,String type);
}