package com.shopme.admin.product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductImage;

public class ProductSaveHelper {
	private static final Logger log = LoggerFactory.getLogger(ProductSaveHelper.class);

	static void setMainImageName(MultipartFile multipartFile, Product product) {
		log.info("ProductController | setMainImageName | !multipartFile.isEmpty() : " + !multipartFile.isEmpty());
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			product.setMainImage(fileName);
		}
	}


	static void setExistingExtraImage(String[] imageIDs, String[] imageNames, Product product) {
		if (imageIDs == null || imageIDs.length == 0) return;
		
		Set<ProductImage> images= new HashSet<>();
		
		for (int i = 0; i < imageIDs.length; i++) {
			Integer id = Integer.parseInt(imageIDs[i]);
			String name = imageNames[i];
			
			images.add(new ProductImage(id, name, product));
		}
		product.setImages(images);
	}
	
	static void setNewExtraImageName(MultipartFile[] extraMultipartFiles, Product product) {
		log.info("ProductController | setNewExtraImageName | extraMultipartFiles.length : " + extraMultipartFiles.length);
		if (extraMultipartFiles.length > 0) {
			for (MultipartFile multipartFile : extraMultipartFiles) {
				if (!multipartFile.isEmpty()) {
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
					
					if (!product.containsImageName(fileName)) {
						product.addExtraImage(fileName);
					}
				}
			}
		}
	}

	static void setProductDetails(Integer[] detailIDs, String[] detailNames, String[] detailValue, Product product) {
		if (detailNames == null || detailNames.length==0) return;
				
		for(int count = 0; count < detailNames.length; count++) {
			String name = detailNames[count];
			String value = detailValue[count];
			Integer id = detailIDs[count];
			
			if (id != 0) {
				product.addProductDetail(id, name, value);
			} else if (!name.isEmpty() && !value.isEmpty()) {
				product.addProductDetail(name, value);
			}
		}
	}
	
	static void deleteRemovedExtraImage(Product product) {
		String extraImageString = "../product-images/" + product.getId() + "/extras" ;
		Path dirPath = Paths.get(extraImageString);
		try {
			Files.list(dirPath).forEach(file -> {
				String filename = file.toFile().getName();
				
				if (!product.containsImageName(filename)) {
					try {
						Files.delete(file);
						log.info("ProductController | saveProduct | deleteRemovedExtraImage : " + filename);

					}catch (Exception e) {
						log.error("Error ProductController | saveProduct | deleteRemovedExtraImage : cannot list directory " + filename);
					}
				}
			});
		} catch (Exception e) {
			log.error("Error ProductController | saveProduct | deleteRemovedExtraImage : error cannot list directory " + dirPath);
		}
	}
	
	static void saveUploadedImage(MultipartFile multipartFile, MultipartFile[] extraMultipartFiles, Product product) throws IOException {
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
				FileUploadUtil.saveFile(uploadDir, fileName, file);
			}
		}
		
	}

	
	static void setExtraImageName(MultipartFile[] extraMultipartFiles, Product product) {
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
	

}
