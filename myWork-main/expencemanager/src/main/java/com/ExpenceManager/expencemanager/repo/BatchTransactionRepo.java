package com.ExpenceManager.expencemanager.repo;

import com.ExpenceManager.expencemanager.entity.BatchTransactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchTransactionRepo extends JpaRepository<BatchTransactions,Long> {
}
