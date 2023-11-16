package com.shopme.admin.brand;

import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.admin.paging.PagingandSortingHelper;
import com.shopme.common.entity.Brand;

@Service
public class BrandService {
	public static final int BRANDS_PER_PAGE = 10;
	@Autowired
	BrandRepository repository;

	public List<Brand> listAll() {
	 return (List<Brand>)repository.findAll(Sort.by("name").ascending());	
	}
	
	public void listByPage(int pageNum, PagingandSortingHelper helper){
		helper.listEntities(pageNum, BRANDS_PER_PAGE, repository);
	}
	
	public Brand save(Brand brand) {
		return repository.save(brand);		
	}
	
	public Brand get(Integer id) throws BrandNotFoundException {
		try {
			return repository.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new BrandNotFoundException("Could not find any brand with ID " + id);
		}
	}
	
	public void delete(Integer id) throws BrandNotFoundException{
		Long countById = repository.countById(id);

		if (countById == null || countById == 0) {
			throw new BrandNotFoundException("Could not find any brand with ID " + id);			
		}

		repository.deleteById(id);
	}

	public String checkUnique(Integer id, String name) {
		boolean isCreatingNew = (id == null || id == 0);
		Brand brandByName = repository.findByName(name);
		System.out.println("param id "+id+", name : "+name+"  isCreating>>"+isCreatingNew+" || "+brandByName.toString());
		if (isCreatingNew) {
			if (brandByName != null) 
				return "Duplicate";
		}else {
			if (brandByName != null && brandByName.getId() != id) {
				return "Duplicate";
			}
		}
		return "OK";
	}
}
