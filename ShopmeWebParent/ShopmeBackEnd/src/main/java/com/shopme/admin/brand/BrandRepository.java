package com.shopme.admin.brand;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.User;

public interface BrandRepository extends PagingAndSortingRepository<Brand, Integer>{
	
	@Query("SELECT u FROM Brand u WHERE CONCAT(u.id, ' ', u.name, ' ') LIKE %?1%")
	public Page<Brand> findAll(String keyword, Pageable pageable);
}
