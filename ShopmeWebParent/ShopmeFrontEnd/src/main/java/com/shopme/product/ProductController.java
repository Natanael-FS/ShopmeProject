package com.shopme.product;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shopme.category.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import com.shopme.common.exception.CategoryNotFoundException;
import com.shopme.common.exception.ProductNotFoundException;

@Controller
public class ProductController {
	@Autowired
	CategoryService categoryService;
	
	@Autowired
	ProductService productService;
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

	
	@GetMapping("/c/{category_alias}")
	public String viewCategory1(Model model, @PathVariable("category_alias") String path) {
		return viewCategory(model, path, 1);
	}
	
	@GetMapping("/c/{category_alias}/page/{pageNum}")
	public String viewCategory(Model model, @PathVariable("category_alias") String path, 
								@PathVariable("pageNum") int pageNum) {
		try {
			Category category = categoryService.getCategory(path);
			if (category == null) {
				return "error/404";
			}
			
			List<Category> listCategoriesParents = categoryService.getCategoryParents(category);
			Page<Product> page = productService.listByCategories(1, category.getId());
			List<Product> listProducts = page.getContent();

			long startCount = (pageNum-1) * productService.PRODUCTS_PER_PAGE + 1;
			long endCount = startCount + productService.PRODUCTS_PER_PAGE - 1;
			
			if(endCount > page.getTotalElements()) {
				endCount = page.getTotalElements();
			}
			
			/*
			String reverseSortDirString = sortDir.equals("asc") ? "desc" : "asc";
			System.out.println("reverseSortDirString : "+reverseSortDirString);

			model.addAttribute("sortField", sortField);
			model.addAttribute("sortDir", sortDir);
			model.addAttribute("reverseSortDirString", reverseSortDirString);
			model.addAttribute("keyword", keyword);
			 */
			
			model.addAttribute("currentPage", pageNum);
			model.addAttribute("totalPages", page.getTotalPages());
			model.addAttribute("startCount", startCount);
			model.addAttribute("endCount", endCount);
			model.addAttribute("totalItems", page.getTotalElements());
			model.addAttribute("category", category);
			model.addAttribute("pageTitle", category.getName());
			model.addAttribute("listCategoriesParents", listCategoriesParents);
			model.addAttribute("listProducts", listProducts);
			model.addAttribute("categories", category);
			
			return "product/product_by_category";
		} catch (CategoryNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "error/404";
		}

	}
	

	@GetMapping("/p/{product_alias}")
	public String viewProductDetail(@PathVariable("product_alias") String alias, Model model) {
		
		LOGGER.info("ProductController | viewProductDetail is called");
		
		try {
			Product product = productService.getProduct(alias);
			List<Category> listCategoryParents = categoryService.getCategoryParents(product.getCategory());
			LOGGER.info("ProductController | viewCategoryByPage | pageTitle : " + product.getShortName());
			
			model.addAttribute("listCategoryParents", listCategoryParents);
			model.addAttribute("product", product);
			model.addAttribute("pageTitle", product.getShortName());

			return "product/product_detail";
		} catch (ProductNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "error/404";
		}
	}
	
}
