package com.shopme.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Product;
import com.shopme.common.exception.ProductNotFoundException;

@Service
public class ProductService {
	@Autowired
	ProductRepository productRepository;
	
	public static final int PRODUCTS_PER_PAGE = 10;
	
	public Page<Product> listByCategories(int pageNum, Integer categoryId) {
		String categoryIdMatch = "-" + categoryId + "-";
		Pageable pageable = PageRequest.of(pageNum-1, PRODUCTS_PER_PAGE);
		Page<Product> pageProduct = productRepository.listByCategory(categoryId, categoryIdMatch, pageable);

		return pageProduct;
	}
	
	public Product getProduct(String alias) throws ProductNotFoundException{
		Product product = productRepository.findByAlias(alias);
		if (product == null) {
			throw new ProductNotFoundException("Could not find product with alias " + alias);
		}
		return product;
	}
	
	
}
