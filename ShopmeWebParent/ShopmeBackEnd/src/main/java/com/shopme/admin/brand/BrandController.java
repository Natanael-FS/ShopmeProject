package com.shopme.admin.brand;

import java.io.IOException;
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
import com.shopme.admin.categories.CategoriesService;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.paging.PagingandSortingHelper;
import com.shopme.admin.brand.export.BrandCsvExporter;
import com.shopme.admin.brand.export.BrandExcelExporter;
import com.shopme.admin.brand.export.BrandPdfExporter;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Brand;

@Controller
public class BrandController {
	private static final Logger log = LoggerFactory.getLogger(BrandController.class);

	@Autowired
	BrandService service;
	
	@Autowired
	CategoriesService categoriesService;
	
	
	@GetMapping("/brands")
	public String getBrand() {
		return "redirect:/brands/page/1?sortField=name&sortDir=asc";
	} 
	
	@GetMapping("/brands/page/{pageNum}")
	public String listByPage(@PagingAndSortingParam(listName = "listBrands", moduleURL = "/brands") PagingandSortingHelper helper,
			@PathVariable (name = "pageNum") int pageNum) {
		
		service.listByPage(pageNum, helper);

		return "Brand/brand";
	}
	
	@GetMapping("/brands/new")
	public String createNewBrand(Model model) {
		List<Category> listCategories = categoriesService.listCategoriesUsedInForm();
		
		Brand brand = new Brand();
		model.addAttribute("brand",brand);
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("pageTitle", "Create New Brand");
		return "brand/brand_form";
	}
	
	@PostMapping("/brands/save")
	public String savedBrands(Brand brand, @RequestParam("fileImage") MultipartFile multipartFile, RedirectAttributes ra) throws IOException{
		if (!multipartFile.isEmpty()) {
			String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			brand.setLogo(filename);
			
			Brand savedBrand = service.save(brand);
			String uploadDirString = "../brand-logos/"+ savedBrand.getId();
			FileUploadUtil.cleanDir(uploadDirString);
			FileUploadUtil.saveFile(uploadDirString, filename, multipartFile);
		}else {
			service.save(brand);
		}
		ra.addFlashAttribute("message", "The Brand has been saved successfully");
		return "redirect:/brands";
	}
	
	@GetMapping("/brands/edit/{id}")
	public String editBrandById(@PathVariable(name = "id")Integer id, 
			Model model, RedirectAttributes redirectAttributes) {
		try {
			Brand brand = service.get(id);
			List<Category> listCategories = categoriesService.listCategoriesUsedInForm();

			model.addAttribute("listCategories", listCategories);
			model.addAttribute("brand", brand);
			model.addAttribute("pageTitle", "Edit Brand (ID : " + id + ")");
			return "brand/brand_form";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/brands";
		}
	}
	
	
	@GetMapping("/brands/delete/{id}")
	public String deleteBrand(@PathVariable(name = "id")Integer id,
			Model model, RedirectAttributes redirectAttributes) {
		try {
			 service.delete(id);

			redirectAttributes.addFlashAttribute("message", "The Brand ID " +id + " has been deleted successfully");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/brands";
	}
	
	/*
	 @GetMapping("/brands/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<Brand> listbrands = service.listAll();
		
		BrandCsvExporter exporter = new BrandCsvExporter();
		log.info("BrandController | exportToCSV | export is starting");
		exporter.export(listbrands, response);
		log.info("BrandController | exportToCSV | export completed");		
	}
	
	
	@GetMapping("/brands/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		List<Brand> listbrands = service.listAll();
		
		BrandExcelExporter exporter = new BrandExcelExporter();
		log.info("BrandController | exportToExcel | export is starting");
		exporter.export(listbrands, response);
		log.info("BrandController | exportToExcel | export completed");		
	}		

	
	@GetMapping("/brands/export/pdf")
	public void exportToPdf(HttpServletResponse response) throws IOException {
		List<Brand> listbrands = brandService.listAll();
		
		BrandPdfExporter exporter = new BrandPdfExporter();
		log.info("BrandController | exportToPdf | export is starting");
		exporter.export(listbrands, response);
		log.info("BrandController | exportToPdf | export completed");		
	}
	
	 */
	
}
