package com.shopme.admin.product;

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
import com.shopme.admin.security.ShopmeUserDetail;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductImage;

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
		return listByPage(1,"asc","name",model, null, 0);
	}

	@GetMapping("/products/page/{pageNum}")
	public String listByPage(@PathVariable(name="pageNum") int pageNum, 
			@Param("sortDir") String sortDir, 
			@Param("sortField") String sortField,
			Model model, 
			@Param("keyword") String keyword, 
			@Param("categoryId") Integer categoryId) {
		
		System.out.println("catID : "+categoryId);
		
		Page<Product> page = productService.listByPage(pageNum,sortField,sortDir,keyword,categoryId);
		List<Product> listProducts = page.getContent();
		List<Category> listCategories = categoriesService.listCategoriesUsedInForm();
		
		String reverseSortDirString = sortDir.equals("asc") ? "desc" : "asc";
//		System.out.println("reverseSortDirString : "+reverseSortDirString);

		long startCount = (pageNum-1) * productService.PRODUCTS_PER_PAGE + 1;
		long endCount = startCount + productService.PRODUCTS_PER_PAGE - 1;
		
		if(endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		

		if (categoryId != null) {
			model.addAttribute("categoryId", categoryId);
		}
		
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDirString", reverseSortDirString);
		model.addAttribute("keyword", keyword);
		
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

		if (loggedUser.hasRole("Salesperson")) {
			System.out.println("this is a salesperson");
			productService.saveProductPrice(product);
			ra.addFlashAttribute("messageSuccess", "The product has been saved successfully.");
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
		} catch (ProductNotFoundExecption e) {
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
		} catch (ProductNotFoundExecption e) {
			ra.addFlashAttribute("message",e.getMessage());
			return "redirect:/products";
		}
		
	}
	
	
	@GetMapping("/products/{id}/enabled/{status}")
	public String updateProductEnabledStatus(@PathVariable("id") Integer id, 
			@PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) throws ProductNotFoundExecption  
	{
		productService.updateProductEnabledStatus(id, enabled);
		
		String status = enabled ? "enabled" : "disabled";
		String message = "The Product id " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		
		return "redirect:/products";
	}
	
	@GetMapping("products/delete/{id}")
	public String deleteProduct(@PathVariable(name = "id")Integer id, RedirectAttributes ra) throws ProductNotFoundExecption
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
		} catch (ProductNotFoundExecption e) {
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
