package com.shopme.admin.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Product;
import com.shopme.admin.brand.BrandRepository;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductTest {
	@Autowired
	private ProductRepository repo;
	@Autowired
	private BrandRepository brandRepository;

	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateProduct() {
		Brand brand = entityManager.find(Brand.class, 37);
		Category category = entityManager.find(Category.class, 5);

		Product product = new Product();
		product.setName("Acer Desktop");
		product.setAlias("Acer_desktop");
		product.setShortDescription("Short description for Acer");
		product.setFullDescription("Full description for Acer");

		product.setBrand(brand);
		product.setCategory(category);

		product.setPrice(878);
		product.setCost(700);
		product.setEnabled(true);
		product.setInStock(true);

		product.setCreatedTime(new Date());
		product.setUpdatedTime(new Date());

		Product savedProduct = repo.save(product);

		assertThat(savedProduct).isNotNull();
		assertThat(savedProduct.getId()).isGreaterThan(0);
	}

	@Test
	public void testListAllProducts() {
		Iterable<Product> iterableProducts = repo.findAll();

		iterableProducts.forEach(System.out::println);
	}

	@Test
	public void testListAllProducts2() {
		List<Product> list = repo.findAllProducts();
		
		list.forEach(System.out::println);
	}
	
	@Test
	public void testListAllBrands() {
		List<Brand> list = brandRepository.findAll();
		
		list.forEach(System.out::println);
	}
	
	@Test
	public void testGetProduct() {
		Integer id = 1;
		Product product = repo.findById(id).get();
		System.out.println(product);

		assertThat(product).isNotNull();
	}

	@Test
	public void testUpdateProduct() {
		Integer id = 1;
		Product product = repo.findById(id).get();
		product.setPrice(499);

		repo.save(product);

		Product updatedProduct = entityManager.find(Product.class, id);

		assertThat(updatedProduct.getPrice()).isEqualTo(499);
	}

	@Test
	public void testDeleteProduct() {
		Integer id = 1;
		repo.deleteById(id);

		Optional<Product> result = repo.findById(id);

		assertThat(!result.isPresent());
	}
	
	@Test
	public void testSaveProductWithImage() {
		Integer productId = 3;
		Product product = repo.findById(3).get();
		
		product.setMainImage("mainImage.jpg");
		product.addExtraImage("ExtraImage1.jpg");
		product.addExtraImage("ExtraImage2.jpg");
		product.addExtraImage("ExtraImage3.jpg");
		
		Product savedProduct = repo.save(product);
		
		assertThat(savedProduct.getImages().size()).isEqualTo(3);
	}
	
	@Test
	public void testSaveProductDetails() {
		Product product = repo.findById(3).get();
		
		product.addProductDetail("Device Memory", "128 GB");
		product.addProductDetail("CPU Model", "Mediatek");
		product.addProductDetail("OS", "Linux");
		
		Product savedProduct = repo.save(product);
		
		assertThat(savedProduct.getDetails().size()).isEqualTo(3);
		}
	/*
	 @Test
	public void testSaveProductWithImages() {
		Integer productId = 1;
		Product product = repo.findById(productId).get();

		product.setMainImage("main image.jpg");
		product.addExtraImage("extra image 1.png");
		product.addExtraImage("extra_image_2.png");
		product.addExtraImage("extra-image3.png");

		Product savedProduct = repo.save(product);

		assertThat(savedProduct.getImages().size()).isEqualTo(3);
	}
	
	@Test
	public void testSaveProductWithDetails() {
		Integer productId = 1;
		Product product = repo.findById(productId).get();

		product.addDetail("Device Memory", "128 GB");
		product.addDetail("CPU Model", "MediaTek");
		product.addDetail("OS", "Android 10");

		Product savedProduct = repo.save(product);
		assertThat(savedProduct.getDetails()).isNotEmpty();		
	}
	
	@Test
	public void testUpdateReviewCountAndAverageRating() {
		Integer productId = 1;
		repo.updateReviewCountAndAverageRating(productId);
	} 
	 */
	
}
