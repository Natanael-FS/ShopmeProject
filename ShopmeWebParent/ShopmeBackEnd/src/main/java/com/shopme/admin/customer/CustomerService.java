package com.shopme.admin.customer;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.admin.country.CountryRepository;
import com.shopme.admin.paging.PagingandSortingHelper;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;
import com.shopme.common.exception.CustomerNotFoundException;
import com.shopme.common.exception.ProductNotFoundException;

@Service
public class CustomerService {
	public static final int CUSTOMERS_PER_PAGE = 10;
	@Autowired
	private	CustomerRepository customerRepository;
	
	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public List<Customer> listAll() {
	 return customerRepository.findAllCustomers();	
	}
	
	//after refactor
	public void listByPage(int pageNum, PagingandSortingHelper helper){
		helper.listEntities(pageNum, CUSTOMERS_PER_PAGE, customerRepository);
	}
	
/*	
 * 	//before refactor
	public Page<Customer> listByPage(int pageNum, String sortField, String sortDir, String keyword){
		Sort sort = Sort.by(sortField);
		
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNum-1, CUSTOMERS_PER_PAGE, sort);

		
		if (keyword != null) {
			return customerRepository.findAll(keyword, pageable);
		}

		return customerRepository.findAll(pageable);	}
	
*/
	public Customer save(Customer customerInForm) {
		Customer customerInDB = customerRepository.findById(customerInForm.getId()).get();
		
		
		if (!customerInForm.getPassword().isEmpty()) {
			String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
			customerInForm.setPassword(encodedPassword);			
		} else {
			customerInForm.setPassword(customerInDB.getPassword());
		}
		
		customerInForm.setEnabled(customerInDB.getEnabled());
		customerInForm.setCreatedTime(customerInDB.getCreatedTime());
		customerInForm.setVerificationCode(customerInDB.getVerificationCode());
		
		return customerRepository.save(customerInForm);

	}
	
	public List<Country> listAllCountries(){
		return countryRepository.findAllByOrderByNameAsc(); 
	}

	public void updateCustomerEnabledStatus(Integer id, boolean enabled) throws CustomerNotFoundException {
		customerRepository.updateEnabledStatus(id, enabled);
	}
	
	public Customer get(Integer id) throws CustomerNotFoundException {
		try {
			return customerRepository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new CustomerNotFoundException("Could not find Customer with Id : " + id);
		}
	}

	public void delete(Integer id) throws CustomerNotFoundException {

		Long countById = customerRepository.countById(id);

		if (countById == null || countById == 0) {
			throw new CustomerNotFoundException("Could not find any customer with ID " + id);			
		}

		customerRepository.deleteById(id);		
	}
	
	public boolean isEmailUnique(Integer id, String email) {
		List<Customer> customersWithEmaiList = customerRepository.findByEmail(email);
		Customer existCustomer = customersWithEmaiList.get(0);

		if (existCustomer != null && existCustomer.getId() != id) {
			// found another customer having the same email
			return false;
		}

		return true;
		
	}
}
