package com.bank.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.bank.model.Users;

public interface UserRepo extends CrudRepository<Users, Long> {

	Users findByUsername(String username);

	Users findByid(long id);

}