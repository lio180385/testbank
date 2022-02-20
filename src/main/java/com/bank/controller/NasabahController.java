package com.bank.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bank.exception.UserIdMismatchException;
import com.bank.exception.UserNotFoundException;
import com.bank.model.Users;
import com.bank.repo.UserRepo;
import com.bank.request.UserDTO;
import com.bank.services.JwtUserDetailsService;
 

@RestController
@CrossOrigin
@RequestMapping("/api/nasabah")
public class NasabahController {

	@Autowired
	private JwtUserDetailsService userDetailsService;
	@Autowired
	private UserRepo userDao;

	@GetMapping
	public Iterable findAll() {
		return userDao.findAll();
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> saveUser(@RequestBody UserDTO user) throws Exception {
		return ResponseEntity.ok(userDetailsService.save(user));
	}
 
    @GetMapping("/{id}")
    public Users findOne(@PathVariable Long id) {
        return userDao.findById(id)
          .orElseThrow(UserNotFoundException::new);
    }
    
    
    @DeleteMapping("/{id}")
    public Optional<Users> delete(@PathVariable Long id) {
    	Optional<Users> users= userDao.findById(id);
    	userDao.deleteById(id);
    	return users;
        
    }
 
    @PutMapping("/{id}")
    public Users updateUser(@RequestBody Users user, @PathVariable Long id) throws UserIdMismatchException   {
        if (user.getId() != id) {
          throw new UserIdMismatchException();
        }
        userDao.findById(id)
          .orElseThrow(UserNotFoundException::new);
        return userDao.save(user);
    }

}
