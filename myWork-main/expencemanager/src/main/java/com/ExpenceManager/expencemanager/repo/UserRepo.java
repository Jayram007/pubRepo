package com.ExpenceManager.expencemanager.repo;

import com.ExpenceManager.expencemanager.entity.Transaction;
import com.ExpenceManager.expencemanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User,Long> {
  User findByFirstName(String username);
  String findEmailByFirstName(String name);
}
