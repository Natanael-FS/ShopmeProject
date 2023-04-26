package com.shopme.admin.brand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.User;

@Service
public class BrandService {
	public static final int BRANDS_PER_PAGE = 4;
	@Autowired
	BrandRepository repository;
	
	
	public Page<Brand> listByPage(int pageNum, String sortField, String sortDir, String keyword){
		Sort sort = Sort.by(sortField);
		
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNum-1, BRANDS_PER_PAGE, sort);
		
		if (keyword!=null) {
			return repository.findAll(keyword, pageable);
		}
		return repository.findAll(pageable);
	}
	
	public Brand save(Brand brand) {
		return repository.save(brand);		
	}
	
	public Brand get(Integer id) {
		return repository.findById(id).get();
		
	}
	
	public void delete(Integer id) throws BrandNotFoundExecption{
		
	}
	
}
