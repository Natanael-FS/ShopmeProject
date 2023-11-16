package com.shopme.admin.product;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.shopme.admin.brand.BrandService;
import com.shopme.admin.categories.CategoriesService;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.paging.PagingandSortingHelper;
import com.shopme.admin.security.ShopmeUserDetail;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductImage;
import com.shopme.common.exception.ProductNotFoundException;

@Controller
public class ProductController {
	
	@Autowired
	private ProductService productService;
	@Autowired
	private BrandService brandService;
	@Autowired
	private CategoriesService categoriesService;
	
	private static final Logger log = LoggerFactory.getLogger(ProductController.class);

	@GetMapping("/products")
	public String listAll(Model model) {
//		List<Product> listProducts = productService.listAll();
//		return listByPage(1,"asc","name",model, null, 0);
		return "redirect:/products/page/1?sortField=name&sortDir=asc";
	}

	@GetMapping("/products/page/{pageNum}")
	public String listByPage(@PagingAndSortingParam(listName = "listProducts", moduleURL = "/products") PagingandSortingHelper helper,
			@PathVariable(name="pageNum") int pageNum, Model model,
			@Param("categoryId") Integer categoryId) {

		productService.listByPage(pageNum, helper, categoryId);
		List<Category> listCategories = categoriesService.listCategoriesUsedInForm();
		
		if (categoryId != null) {
			model.addAttribute("categoryId", categoryId);
		}
		model.addAttribute("listCategories", listCategories);

		

		return "Product/products";
	}
	
	@GetMapping("/products/new")
	public String newProduct(Model model) {
		List<Brand> listBrands = brandService.listAll();
		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);
		Integer numberOfExistingExtraImage = product.getImages().size();
		
		model.addAttribute("numberOfExistingExtraImage", numberOfExistingExtraImage);
		
		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("pageTitle", "Create New Product");
		return "Product/products_form";
	}
	
	@PostMapping("/products/save")
	public String saveProduct(Product product, RedirectAttributes ra,
			@RequestParam(value = "fileImage", required = false) MultipartFile multipartFile, 
			@RequestParam(value = "extraImage",required = false) MultipartFile[] extraMultipartFiles,
			@RequestParam(name = "detailIDs", required = false) Integer[] detailIDs,
			@RequestParam(name = "detailNames", required = false ) String[] detailNames,
			@RequestParam(name = "detailValues", required = false ) String[] detailValue,
			@RequestParam(name = "imageIDs", required = false) String [] imageIDs,
			@RequestParam(name = "imageNames", required = false) String [] imageNames,
			@AuthenticationPrincipal ShopmeUserDetail loggedUser
			)throws IOException {

		if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
			if (loggedUser.hasRole("Salesperson")) {
				productService.saveProductPrice(product);				
				ra.addFlashAttribute("messageSuccess", "The product has been saved successfully.");
				return "redirect:/products";
			}
			
		}
		log.info("ProductController | saveProduct is started");
		log.info("ProductController | saveProduct | multipartFile.isEmpty() : " + multipartFile.isEmpty());
		log.info("ProductController | saveProduct | extraMultipartFiles size : " + extraMultipartFiles.length);
		
		ProductSaveHelper.setMainImageName(multipartFile, product);
		ProductSaveHelper.setExistingExtraImage(imageIDs, imageNames, product);
		ProductSaveHelper.setNewExtraImageName(extraMultipartFiles, product);
		ProductSaveHelper.setProductDetails(detailIDs, detailNames, detailValue, product);
		ProductSaveHelper.deleteRemovedExtraImage(product);
		System.out.println(product.toString());
		
		Product savedProduct = productService.save(product);
		ProductSaveHelper.saveUploadedImage(multipartFile, extraMultipartFiles, product);
		
		ra.addFlashAttribute("messageSuccess", "The product has been saved successfully.");
		
		return "redirect:/products";
	}
	

	@GetMapping("products/edit/{id}")
	public String editProduct(Model model, @PathVariable(name = "id") Integer id, RedirectAttributes ra) {
		try {
			Product product = productService.get(id);
			List<Brand> listBrands = brandService.listAll();
			productService.save(product);

			Integer numberOfExistingExtraImage = product.getImages().size();
			
			model.addAttribute("numberOfExistingExtraImage", numberOfExistingExtraImage);
			model.addAttribute("listBrands", listBrands);
			model.addAttribute("product", product);
			model.addAttribute("tesCategory", product.getCategory());
			model.addAttribute("pageTitle", "Edit Product (ID : " + id + ")");

			return "Product/products_form";
		} catch (ProductNotFoundException e) {
			ra.addFlashAttribute("message",e.getMessage());
			return "redirect:/products";
		}
		
	}

	
	@GetMapping("products/detail/{id}")
	public String detailProduct(Model model, @PathVariable(name = "id") Integer id, RedirectAttributes ra) {
		try {
			Product product = productService.get(id);
			model.addAttribute("product", product);
			
			return "Product/products_detail_modal";
		} catch (ProductNotFoundException e) {
			ra.addFlashAttribute("message",e.getMessage());
			return "redirect:/products";
		}
		
	}
	
	
	@GetMapping("/products/{id}/enabled/{status}")
	public String updateProductEnabledStatus(@PathVariable("id") Integer id, 
			@PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) throws ProductNotFoundException  
	{
		productService.updateProductEnabledStatus(id, enabled);
		
		String status = enabled ? "enabled" : "disabled";
		String message = "The Product id " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		
		return "redirect:/products";
	}
	
	@GetMapping("products/delete/{id}")
	public String deleteProduct(@PathVariable(name = "id")Integer id, RedirectAttributes ra) throws ProductNotFoundException
	{
		try {
			productService.delete(id);
			String productImagesDir = "../product-images/" + id ;
			String productExtraImagesDir = "../product-images/" + id + "/extras" ; 
			
			log.info("ProductController | deleteProduct | productImagesDir : " + productImagesDir);
			log.info("ProductController | deleteProduct | productExtraImagesDir : " + productExtraImagesDir);

			FileUploadUtil.removeDir(productImagesDir);
			FileUploadUtil.removeDir(productExtraImagesDir);
			
			String message = "The Product id " + id + " has been deleted" ;
			ra.addFlashAttribute("message", message);
		} catch (ProductNotFoundException e) {
			log.info("ProductController | deleteProduct | messageError : " + e.getMessage());
			
			ra.addFlashAttribute("messageError", e.getMessage());
		}

		return "redirect:/products";
	}
	


	private void saveUploadedImages(MultipartFile mainImageMultipart, 
			MultipartFile[] extraImageMultiparts, Product savedProduct) throws IOException {

		log.info("ProductController | saveUploadedImages is started");

		log.info("ProductController | setMainImageName | !mainImageMultipart.isEmpty() : " + !mainImageMultipart.isEmpty());

		if (!mainImageMultipart.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());

			log.info("ProductController | setMainImageName | fileName : " + fileName);

			String uploadDir = "../product-images/" + savedProduct.getId();

			log.info("ProductController | setMainImageName | uploadDir : " + uploadDir);

			FileUploadUtil.cleanDir(uploadDir);

			FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
		}

		log.info("ProductController | setMainImageName | extraImageMultiparts.length : " + extraImageMultiparts.length);

		if (extraImageMultiparts.length > 0) {

			String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";

			log.info("ProductController | setMainImageName | uploadDir : " + uploadDir);

			for (MultipartFile multipartFile : extraImageMultiparts) {

				log.info("ProductController | setMainImageName | multipartFile.isEmpty() : " + multipartFile.isEmpty());
				if (multipartFile.isEmpty()) continue;

				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

				log.info("ProductController | setMainImageName | fileName : " + fileName);

				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

			}
		}


		log.info("ProductController | saveUploadedImages is completed");
	}
}
