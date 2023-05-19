package com.shopme.admin.product;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.admin.categories.CategoryNotFoundException;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.User;

import net.bytebuddy.asm.Advice.Return;

@Service
public class ProductService {
	public static final int BRANDS_PER_PAGE = 10;
	@Autowired
	ProductRepository repository;

	public List<Product> listAll() {
	 return repository.findAllProducts();	
	}
	
	public Page<Product> listByPage(int pageNum, String sortField, String sortDir, String keyword){
		Sort sort = Sort.by(sortField);
		
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNum-1, BRANDS_PER_PAGE, sort);
		
		if (keyword!=null) {
			return repository.findAll(keyword, pageable);
		}
		return repository.findAll(pageable);
	}
	
	public Product save(Product product) {
		if (product.getId() == null) {
			product.setCreatedTime(new Date());
		}
		if (product.getAlias() == null || product.getAlias().isEmpty()){
			String defaultAliaString = product.getName().replaceAll(" ", "-").toLowerCase();
			product.setAlias(defaultAliaString);
		}else {
			product.getAlias().replaceAll(" ", "-").toLowerCase();
		}
		product.setUpdatedTime(new Date());
		
		return repository.save(product);		
	}
	
	public Product get(Integer id) {
		return repository.findById(id).get();
		
	}
	
	public void delete(Integer id) throws ProductNotFoundExecption{
		Long countById = repository.countById(id);

		if (countById == null || countById == 0) {
			throw new ProductNotFoundExecption("Could not find any product with ID " + id);			
		}

		repository.deleteById(id);
	}

	public String checkUniqueness(Integer id, String name) {
		boolean productUniqueness = (id == null || id == 0 );
		System.out.println("param id "+id+", name : "+name + " || " + productUniqueness);
		
		Product productByName = repository.findByName(name);
		System.out.println("param id "+id+", name : "+name+"  isCreating>>"+productByName.getName()+" || "+ productByName.toString());
		if (productUniqueness) {
			if (productByName != null) 
				return "Duplicate";
		}else {
			if (productByName != null && productByName.getId() != id) {
				return "Duplicate";
			}
		}
		return "OK";
	}
	
	
	public void updateProductEnabledStatus(Integer id, boolean enabled) throws ProductNotFoundExecption {
		repository.updateEnabledStatus(id, enabled);
	}
}
