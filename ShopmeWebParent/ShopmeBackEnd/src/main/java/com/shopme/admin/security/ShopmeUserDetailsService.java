package com.shopme.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.shopme.admin.user.UserRepository;
import com.shopme.common.entity.User;

public class ShopmeUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.getUserByEmail(email);
		if (user != null) {
			/*
			  System.out.println(user.toString()); boolean passwordsMatch = new
			  BCryptPasswordEncoder().matches("123456", user.getPassword()); boolean
			  passwordsMatch2 = new BCryptPasswordEncoder().matches("123456", "123456");
			  
			  System.out.println("isTrue :: "+passwordsMatch+" || "+passwordsMatch2);
			 */
			return new ShopmeUserDetail(user);
		}
		throw new UsernameNotFoundException("Could not find email ->" + email);
	}

}
