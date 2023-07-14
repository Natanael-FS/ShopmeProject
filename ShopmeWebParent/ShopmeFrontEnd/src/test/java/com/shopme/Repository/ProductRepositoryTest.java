package com.shopme.Repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;

import com.shopme.category.CategoryRepository;
import com.shopme.category.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import com.shopme.product.ProductRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ProductRepositoryTest {
	
	@Autowired
	ProductRepository repo;
	
	@Test
	public void testListEnabledCategories() {
//		String alias = "canon-eos-m50";
		String alias = "AmazonBasics-Large-DSLR-Camera-Gadget-Bag";
		Product product = repo.findByAlias(alias);
		System.out.println(product.toString());
		
		assertThat(product).isNotNull();
	}
	
}
