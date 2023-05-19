package com.shopme.admin.categories;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

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
import com.shopme.admin.categories.export.CategoryCsvExporter;
import com.shopme.admin.categories.export.CategoryExcelExporter;
import com.shopme.admin.categories.export.CategoryPdfExporter;
import com.shopme.admin.user.UserNotFoundException;
import com.shopme.admin.user.controller.UserController;
import com.shopme.admin.user.export.UserExcelExporter;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Controller
public class CategoryController {
	private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

	@Autowired 
	CategoriesService categoriesService;
	
	@GetMapping("/categories")
	public String getCategory(@Param("sortDir") String sortDir, Model model) {
		return listByPage(1,"asc",model, null);
	} 
	
	@GetMapping("/categories/page/{pageNum}")
	public String listByPage(@PathVariable(name="pageNum") int pageNum, @Param("sortDir") String sortDir,
			Model model, @Param("keyword") String keyword) {
//		System.out.println(sortDir);
//		if (sortDir ==  null || sortDir.isEmpty() || sortDir.equalsIgnoreCase(null) ) {
//			System.out.println("truee");
//			sortDir = "asc";
//		}
//		System.out.println(sortDir);

		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

		CategoryPageInfo pageInfo = new CategoryPageInfo();
		List<Category> listCategories = categoriesService.listByPage(pageInfo, pageNum, sortDir, keyword);

		long startCount = (pageNum - 1) * categoriesService.ROOT_CATEGORIES_PER_PAGE + 1;
		long endCount = startCount + categoriesService.ROOT_CATEGORIES_PER_PAGE - 1;
		if (endCount > pageInfo.getTotalElements()) {
			endCount = pageInfo.getTotalElements();
		}
		
		model.addAttribute("totalPages", pageInfo.getTotalPages());
		model.addAttribute("totalItems", pageInfo.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", "name");
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);

		model.addAttribute("listCategories", listCategories);
		model.addAttribute("reverseSortDir", reverseSortDir);

		log.info("CategoryController | listAll | listCategories  " );
		log.info("CategoryController | listAll | reverseSortDir : " + reverseSortDir);
		
		return "categories/categories";
	}
	
	@GetMapping("/categories/new")
	public String newCategory(Model model, @Param("keyword") String keyword) {
		List<Category> listCategories = categoriesService.listCategoriesUsedInForm();
		Category category = new Category();
		category.setEnabled(true);
		
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
		String message = "The Categories id " + id + " has been " + status;
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

	@GetMapping("/categories/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id,
		 Model model, RedirectAttributes redirectAttributes)
	{
		try {
			 categoriesService.deleteCategory(id);

				redirectAttributes.addFlashAttribute("message", "The User ID " +id + "has been deleted successfully");

		} catch (CategoryNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message",ex.getMessage());
		}
		
		return "redirect:/categories";
	}
	
	@GetMapping("/categories/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		List<Category> listCategories = categoriesService.listAll();
		
		CategoryExcelExporter exporter = new CategoryExcelExporter();
		log.info("CategoryController | exportToExcel | export is starting");
		exporter.export(listCategories, response);
		log.info("CategoryController | exportToExcel | export completed");		
	}	
	
	@GetMapping("/categories/export/csv")
	public void exportTocsv(HttpServletResponse response) throws IOException {
		List<Category> listCategories = categoriesService.listAll();
		
		CategoryCsvExporter exporter = new CategoryCsvExporter();
		log.info("CategoryController | exportToCsv | export is starting");
		exporter.export(listCategories, response);
		log.info("CategoryController | exportToCsv | export completed");		
	}	
	
	@GetMapping("/categories/export/pdf")
	public void exportTopdf(HttpServletResponse response) throws IOException {
		List<Category> listCategories = categoriesService.listAll();
		
		CategoryPdfExporter exporter = new CategoryPdfExporter();
		log.info("CategoryController | exportToPdf | export is starting");
		exporter.export(listCategories, response);
		log.info("CategoryController | exportToPdf | export completed");		
	}	


}

