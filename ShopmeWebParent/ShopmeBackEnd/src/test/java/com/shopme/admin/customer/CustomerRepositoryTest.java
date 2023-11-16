package com.shopme.admin.customer;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.shopme.common.entity.Customer;

@SpringBootTest
public class CustomerRepositoryTest {
	
	@Autowired
	CustomerRepository customerRepository;

	@Test
	public void isEmailUnique() {
		List<Customer> customersWithEmaiList = customerRepository.findByEmail("tes123");
		boolean isUnque = !(customersWithEmaiList.size()>0);
		System.out.println("size : " + customersWithEmaiList.size() + " >> " + isUnque);

	}
}
