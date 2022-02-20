package com.bank.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.bank.model.TransactionType;
import com.bank.model.TrasactionHistory;
import com.bank.model.Users;

public interface TrasactionTypeRepo extends CrudRepository<TransactionType, Long> {
	List<TransactionType> findByTransactionname(String name);
	TransactionType findByid(long asLong);
	TransactionType findByUsers(Users users);
}
