package com.shopme.admin.customer;

import java.util.List;
import java.util.Optional;

import javax.persistence.Entity;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.admin.paging.SearchRepository;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Customer;

public interface CustomerRepository extends SearchRepository<Customer, Integer>{
	public Optional<Customer> findById(Integer id);
	
	public Long countById(Integer id);
	
	@Query("SELECT c FROM Customer c")
	List<Customer> findAllCustomers();

	@Query("SELECT c FROM Customer c WHERE CONCAT(c.email, ' ', c.firstName, ' ', c.lastName, ' ', "
			+ "c.addressLine1, ' ', c.addressLine2, ' ', c.city, ' ', c.state, "
			+ "' ', c.postalCode, ' ', c.country.name) LIKE %?1%")
	public Page<Customer> findAll(String keyword, Pageable pageable);

/*	
	@Query("SELECT c FROM Customer c WHERE "
			+ "c.firstName LIKE %?1% " 
			+ "OR c.lastName LIKE %?1% "
			+ "OR c.email LIKE %?1% "
			+ "OR c.addressLine1 LIKE %?1% "
			+ "OR c.addressLine2 LIKE %?1% "
			+ "OR c.postalCode LIKE %?1% "
			+ "OR c.city LIKE %?1% "
			+ "OR c.state LIKE %?1% "
			+ "OR c.country.name LIKE %?1%")
	public Page<Customer> findAll(String keyword, Pageable pageable);
*/
	@Transactional
	@Query("UPDATE Customer p SET p.enabled = ?2 WHERE p.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);

	@Query("SELECT c FROM Customer c WHERE c.email LIKE %?1%")
	public List<Customer> findByEmail(String email) ;
	
}
