package com.shopme.admin.product;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.annotation.processing.Filer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;

@Controller
public class ProductController {
	
	@Autowired
	ProductService productService;
	@Autowired
	BrandService brandService;
	
	private static final Logger log = LoggerFactory.getLogger(ProductController.class);

	@GetMapping("/products")
	public String listAll(Model model) {
		List<Product> listProducts = productService.listAll();
		
		model.addAttribute("listProducts", listProducts);
		
		return "Product/products";
	}
	
	@GetMapping("/products/new")
	public String newProduct(Model model) {
		List<Brand> listBrands = brandService.listAll();
		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);
		
		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("pageTitle", "Create New Product");
		return "Product/products_form";
	}
	
	@PostMapping("/products/save")
	public String saveProduct(Product product, RedirectAttributes ra,
			@RequestParam("fileImage") MultipartFile multipartFile, 
			@RequestParam("extraImage") MultipartFile[] extraMultipartFiles) throws IOException {

		log.info("ProductController | saveProduct is started");
		log.info("ProductController | saveProduct | multipartFile.isEmpty() : " + multipartFile.isEmpty());
		log.info("ProductController | saveProduct | extraMultipartFiles size : " + extraMultipartFiles.length);
		
		setMainImageName(multipartFile, product);
		setExtraImageName(extraMultipartFiles, product);
		
		Product savedProduct = productService.save(product);
		saveUploadedImage(multipartFile, extraMultipartFiles, product);
		
		ra.addFlashAttribute("messageSuccess", "The product has been saved successfully.");
		
		return "redirect:/products";
	}
	
	private void saveUploadedImage(MultipartFile multipartFile, MultipartFile[] extraMultipartFiles, Product product) throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			String uploadDir = "../product-images/" + product.getId();
			product.setMainImage(fileName);
			log.info("ProductController | saveProduct | uploadDir : " + uploadDir);

			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}
		
		if (extraMultipartFiles.length > 0) {
			String uploadDir = "../product-images/"+ product.getId() + "/extras";
			for (MultipartFile file : extraMultipartFiles) {
				if (file.isEmpty()) continue;
				
				String fileName = StringUtils.cleanPath(file.getOriginalFilename());
				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			}
		}
		
	}

	private void setMainImageName(MultipartFile multipartFile, Product product) {
		log.info("ProductController | setMainImageName | !multipartFile.isEmpty() : " + !multipartFile.isEmpty());
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			product.setMainImage(fileName);
		}
	}
	
	private void setExtraImageName(MultipartFile[] extraMultipartFiles, Product product) {
		log.info("ProductController | setExtraImageName | extraMultipartFiles.length : " + extraMultipartFiles.length);
		if (extraMultipartFiles.length > 0) {
			for (MultipartFile multipartFile : extraMultipartFiles) {
				if (!multipartFile.isEmpty()) {
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
					product.addExtraImage(fileName);
				}
			}
		}
	}
	
	@GetMapping("products/edit/{id}")
	public String editProduct(Model model, @PathVariable(name = "id") Integer id, RedirectAttributes ra) {
		try {
			Product product = productService.get(id);
			List<Brand> listBrands = brandService.listAll();

			productService.save(product);

			model.addAttribute("listBrands", listBrands);
			model.addAttribute("product", product);
			model.addAttribute("tesCategory", product.getCategory());
			model.addAttribute("pageTitle", "Edit Product (ID : " + id + ")");

			return "Product/products_form";
		} catch (Exception e) {
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
		productService.delete(id);
		String productImagesDir = "../product-images/" + id ;
		String productExtraImagesDir = "../product-images/" + id + "/extras" ; 
		
		log.info("ProductController | deleteProduct | productImagesDir : " + productImagesDir);
		log.info("ProductController | deleteProduct | productExtraImagesDir : " + productExtraImagesDir);

		FileUploadUtil.removeDir(productImagesDir);
		FileUploadUtil.removeDir(productExtraImagesDir);
		
		String message = "The Product id " + id + " has been deleted" ;
		ra.addFlashAttribute("message", message);
		
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
