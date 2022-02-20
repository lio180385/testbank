package com.bank.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bank.exception.TypeIdMismatchException;
import com.bank.exception.TypeNotFoundException;
import com.bank.model.TransactionType;
import com.bank.repo.TrasactionTypeRepo;
 

@RestController
@RequestMapping("/api/type")
public class TransactionTypeController {
	@Autowired
	private  TrasactionTypeRepo typeRepo;
	

	@GetMapping
    public Iterable findAll(){
		return typeRepo.findAll();
		
	}
	 
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionType create(@RequestBody TransactionType type) {
        return typeRepo.save(type);
    }
    
    @GetMapping("/transactionname/{type}")
    public List findByTypeName(@PathVariable String name) {
        return typeRepo.findByTransactionname(name);
    }
 
    @GetMapping("/{id}")
    public TransactionType findOne(@PathVariable Long id) {
        return typeRepo.findById(id)
          .orElseThrow(TypeNotFoundException::new);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
    	typeRepo.findById(id)
          .orElseThrow(TypeNotFoundException::new);
        typeRepo.deleteById(id);
    }
 
    @PutMapping("/{id}")
    public TransactionType updateType(@RequestBody TransactionType type, @PathVariable Long id) throws TypeIdMismatchException {
        if (type.getId() != id) {
          throw new TypeIdMismatchException();
        }
        typeRepo.findById(id)
          .orElseThrow(TypeNotFoundException::new);
        return typeRepo.save(type);
    }
}
