package com.shopme.admin.brand;

import java.io.IOException;
import java.util.List;

import org.apache.poi.util.StringUtil;
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
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import com.shopme.common.entity.Brand;

@Controller
public class BrandController {
	private static final Logger log = LoggerFactory.getLogger(BrandController.class);

	@Autowired
	BrandService service;
	
	@Autowired
	CategoriesService categoriesService;
	
	
	@GetMapping("/brands")
	public String getBrand(@Param("sortDir") String sortDir, Model model) {
		return listByPage(1,"asc",model, null);
	} 
	
	@GetMapping("/brands/page/{pageNum}")
	public String listByPage(@PathVariable(name="pageNum") int pageNum, @Param("sortDir") String sortDir,
			Model model, @Param("keyword") String keyword) {

		System.out.println("SortDir :"+sortDir);
		
		Page<Brand> page = service.listByPage(pageNum,"name",sortDir,keyword);
		List<Brand> listBrands =  page.getContent();
		
		String reverseSortDirString = sortDir.equals("asc") ? "desc" : "asc";
		System.out.println("reverseSortDirString : "+reverseSortDirString);

		long startCount = (pageNum-1) * service.BRANDS_PER_PAGE + 1;
		long endCount = startCount + service.BRANDS_PER_PAGE - 1;
		
		if(endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("listBrands", listBrands);
//		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDirString", reverseSortDirString);
		model.addAttribute("keyword", keyword);
		
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
			String uploadDirString = "../brand/logos"+ savedBrand.getId();
			
			FileUploadUtil.cleanDir(uploadDirString);
			FileUploadUtil.saveFile(uploadDirString, filename, multipartFile);
		}else {
			service.save(brand);
		}
		
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
}
