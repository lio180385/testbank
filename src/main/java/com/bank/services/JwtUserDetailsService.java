package com.bank.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bank.model.Users;
import com.bank.repo.UserRepo;
import com.bank.request.UserDTO;



@Service
public class JwtUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepo userDao;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Users user = userDao.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPasswordhash(),
				new ArrayList<>());
	}


	public Users save(UserDTO user) {
		Users newUser = new Users();
		newUser.setUsername(user.getUsername());
		newUser.setPasswordhash(bcryptEncoder.encode(user.getPassword()));
		newUser.setAccountnumber(user.getAccount_number());
		return userDao.save(newUser);
	}
}