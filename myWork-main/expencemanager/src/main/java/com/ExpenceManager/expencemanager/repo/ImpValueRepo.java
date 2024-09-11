package com.ExpenceManager.expencemanager.repo;

import com.ExpenceManager.expencemanager.entity.Category;
import com.ExpenceManager.expencemanager.entity.ImpValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ImpValueRepo extends JpaRepository<ImpValue, Long> {

    @Query("SELECT v FROM ImpValue v WHERE v.valueName = ?1")
    ImpValue findByValueName(String valueName);

}
