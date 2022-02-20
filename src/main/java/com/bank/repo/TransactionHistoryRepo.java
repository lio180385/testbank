package com.bank.repo;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.bank.model.TrasactionHistory;

public interface TransactionHistoryRepo extends CrudRepository<TrasactionHistory,Long>{

	List<TrasactionHistory> findByActivitydate(Timestamp date);
	 @Query(value ="SELECT   * from trasactionhistory  where  to_char(activitydate, 'DD-MM-YYYY') BETWEEN ?2 AND ?3 and trasactionhistory.users_id= ?1", 
			   nativeQuery = true)
	 List<TrasactionHistory> findSearchDate(Long user, String string, String string2);

}
