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

 
import com.bank.repo.TransactionHistoryRepo;
import com.bank.exception.HistoryNotFoundException;
import com.bank.exception.HistoyIdMismatchException;
import com.bank.model.TrasactionHistory;

@RestController
@RequestMapping("/api/history")
public class TransactionHistoryController {
	@Autowired
	private TransactionHistoryRepo historyRepo;
	
	
	
	@GetMapping
    public Iterable findAll(){
		return historyRepo.findAll();
		
	}
	 
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrasactionHistory create(@RequestBody TrasactionHistory type) {
        return historyRepo.save(type);
    }
    
//    @GetMapping("/history/{date}")
//    public List findByDate(@PathVariable String date) {
//        return historyRepo.findByTransactionactivity_date(date);
//    }
 
    @GetMapping("/{id}")
    public TrasactionHistory findOne(@PathVariable Long id) {
        return historyRepo.findById(id)
          .orElseThrow(HistoryNotFoundException::new);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
    	historyRepo.findById(id)
          .orElseThrow(HistoryNotFoundException::new);
    	historyRepo.deleteById(id);
    }
 
    @PutMapping("/{id}")
    public TrasactionHistory updateHistory(@RequestBody TrasactionHistory type, @PathVariable Long id) throws HistoyIdMismatchException {
        if (type.getId() != id) {
          throw new HistoyIdMismatchException();
        }
        historyRepo.findById(id)
          .orElseThrow(HistoryNotFoundException::new);
        return historyRepo.save(type);
    }

}
