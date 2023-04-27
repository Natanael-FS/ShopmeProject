package com.shopme.admin.brand;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.User;

import net.bytebuddy.asm.Advice.Return;

@Service
public class BrandService {
	public static final int BRANDS_PER_PAGE = 10;
	@Autowired
	BrandRepository repository;

	public List<Brand> listAll() {
	 return (List<Brand>)repository.findAll(Sort.by("firstName").ascending());	
	}
	
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
		Long countById = repository.countById(id);

		if (countById == null || countById == 0) {
			throw new BrandNotFoundExecption("Could not find any brand with ID " + id);			
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
