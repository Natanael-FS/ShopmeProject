package com.shopme.admin.categories;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.user.UserNotFoundException;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Controller
public class CategoryController {
	private static final Logger LOGGER = LoggerFactory.getLogger(CategoryController.class);
	
	@Autowired 
	CategoriesService categoriesService;
	
	@GetMapping("/categories")
	public String getCategory(Model model, @Param("keyword") String keyword) {
		List<Category> listCategories = categoriesService.findRoot();
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("keyword", keyword);		
		
		return"categories/categories";
	} 
	
	@GetMapping("/categories/new")
	public String newCategory(Model model, @Param("keyword") String keyword) {
		List<Category> listCategories = categoriesService.listCategoriesUsedInForm();
		Category category = new Category();
		category.setEnabled(true);
		System.out.println(listCategories.toString());
		
		for (Category category2 : listCategories) {
			System.out.println(category2.getName());
		}
		
		model.addAttribute("category",category);
		model.addAttribute("listCategories",listCategories);
		model.addAttribute("keyword", keyword);		
		model.addAttribute("pageTitle", "New Category");		

		return"categories/categories_form";
	} 
	
	@PostMapping("/categories/save")
	public String saveCategories(Category category, RedirectAttributes redirectAttributes, 
			@RequestParam("fileImage") MultipartFile multipartFile) throws IOException {
//		System.out.println(category.toString());
		
		if (!multipartFile.isEmpty()) {
			String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			System.out.println("filename>>"+filename);
			category.setImage(filename);
			
			Category savedCategory = categoriesService.save(category);
			String uploadDir = "../category-images/"+savedCategory.getId();
			
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, filename, multipartFile);	
		}else {
			categoriesService.save(category);
		}
			
		redirectAttributes.addFlashAttribute("message", "The Category has been saved successfully");
		
		return "redirect:/categories";
	}
	
	@GetMapping("/categories/{id}/enabled/{status}")
	public String updateUserEnabledStatus(@PathVariable("id") Integer id, 
			@PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) throws CategoryNotFoundException
	{
		categoriesService.updateCategoryEnabledStatus(id, enabled);
		
		String status = enabled ? "enabled" : "disabled";
		String message = "The Categories id " + id + "has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		
		return "redirect:/categories";
	}
	
	@GetMapping("/categories/edit/{id}")
	public String editCategories(@PathVariable(name = "id") Integer id,
		 Model model, RedirectAttributes redirectAttributes) throws CategoryNotFoundException
	{
		List<Category> listCategories = categoriesService.listCategoriesUsedInForm();
		Category category = categoriesService.getCategory(id);
		
		model.addAttribute("category",category);
		model.addAttribute("listCategories",listCategories);
		model.addAttribute("pageTitle", "Edit Category (ID : " + id + ")");

		return"categories/categories_form";
	}
	
}

