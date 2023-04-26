package com.shopme.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class BrandTest {
	@Autowired
	private BrandRepository repository;
	private Brand savedBrand;
	
	@Test
	public void tesCreateNewBrand1() {
		Category category = new Category(6);
		Brand brand = new Brand("Acer");
		
		brand.getCategories().add(category);
		Brand savedBrand = repository.save(brand);
		
		assertThat(savedBrand).isNotNull();
		assertThat(savedBrand.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testFindAll() {
		Iterable<Brand> listBrands = new ArrayList<>();
		listBrands = repository.findAll();
		listBrands.forEach(System.out::println);
		
		assertThat(listBrands).isNotEmpty();
	}
	
	@Test
	public void testGetById() {
		Brand brand = repository.findById(1).get();
		
		assertThat(brand.getName()).isEqualTo("Acer");
	}
	
	@Test
	public void testUpdateName() {
		Brand brand = repository.findById(1).get();
		brand.setName("Asus");
		Brand savedBrand = repository.save(brand);
		assertThat(brand.getName()).isEqualTo("Acer");
	}	
	
	@Test
	public void testDeleteBrand() {
		repository.deleteById(2);
		Optional<Brand> result = repository.findById(2);
		
		assertThat(result).isEmpty();
	}
}
