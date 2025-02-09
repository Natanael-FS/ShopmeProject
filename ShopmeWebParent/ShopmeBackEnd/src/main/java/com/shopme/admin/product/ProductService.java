package com.shopme.admin.product;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.admin.categories.CategoryNotFoundException;
import com.shopme.admin.paging.PagingandSortingHelper;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.User;
import com.shopme.common.exception.ProductNotFoundException;

import net.bytebuddy.asm.Advice.Return;

@Service
public class ProductService {
	public static final int PRODUCTS_PER_PAGE = 5;
	@Autowired
	ProductRepository repository;

	public List<Product> listAll() {
	 return repository.findAllProducts();	
	}

	public void listByPage(int pageNum, PagingandSortingHelper helper, Integer categoryId){
		Pageable pageable = helper.createPageable(PRODUCTS_PER_PAGE, pageNum);
		String keyword = helper.getKeyword();
		Page<Product> page =null;
		

		if (keyword != null && !keyword.isEmpty()) {

			if (categoryId != null && categoryId > 0) {
				String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
				page =  repository.searchInCategory(categoryId, categoryIdMatch, keyword, pageable);
			}else {
				page = repository.findAll(keyword, pageable);
			}
		}
		
		if (categoryId != null &&categoryId > 0) {
			String categoryIdMatchString = "-" + String.valueOf(categoryId) + "-";
//			System.out.println(categoryId+ " || " + categoryIdMatchString);
			page = repository.findAllInCategory(categoryId, categoryIdMatchString, pageable);
		}else {
			page = repository.findAll(pageable);
		}
		
//		return page;
		helper.updateModelAtrributes(pageNum, page);
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
	
	public void saveProductPrice(Product product) {
		Product productDb = repository.findById(product.getId()).get();
		productDb.setPrice(product.getPrice());
		productDb.setDiscountPercent(product.getDiscountPercent());
		productDb.setCost(product.getCost());
		
		repository.save(productDb);	
	}
	
	public Product get(Integer id) throws ProductNotFoundException {
		try {
			return repository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new ProductNotFoundException("Could not find Product with Id : " + id);
		}
	}
	
	public void delete(Integer id) throws ProductNotFoundException{
		Long countById = repository.countById(id);

		if (countById == null || countById == 0) {
			throw new ProductNotFoundException("Could not find any product with ID " + id);			
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
	
	
	public void updateProductEnabledStatus(Integer id, boolean enabled) throws ProductNotFoundException {
		repository.updateEnabledStatus(id, enabled);
	}
	
	
	/*	 
	public Page<Product> listByPage(int pageNum, String sortField, String sortDir, String keyword, Integer categoryId){
		Sort sort = Sort.by(sortField);
		
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNum-1, PRODUCTS_PER_PAGE, sort);
		
		//if (keyword!=null) { return repository.findAll(keyword, pageable); }
		
		if (keyword != null && !keyword.isEmpty()) {

			if (categoryId != null && categoryId > 0) {
				String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
				return repository.searchInCategory(categoryId, categoryIdMatch, keyword, pageable);
			}
		}
		
		if (categoryId != null &&categoryId > 0) {
			String categoryIdMatchString = "-" + String.valueOf(categoryId) + "-";
			System.out.println(categoryId+ " || " + categoryIdMatchString);
			return repository.findAllInCategory(categoryId, categoryIdMatchString, pageable);
		}
		
		return repository.findAll(pageable);
	}
	
	*/
}
