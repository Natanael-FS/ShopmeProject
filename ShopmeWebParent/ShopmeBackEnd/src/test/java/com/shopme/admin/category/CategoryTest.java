package com.shopme.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.categories.CategoryRepository;
import com.shopme.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryTest {
	@Autowired
	private CategoryRepository repository;
	private Category savedCategory;
	
//	@Test
//	public void tesCreateRootCategory() {
//		Category category = new Category("Computers");
//		Category savedCategory = repository.save(category);
//		
//		assertThat(savedCategory.getId()).isGreaterThan(0);
//	}
//	
/*
	@Test
	public void tesCreateSubCategory() {
		Category parent = new Category(5);
		Category subCategory = new Category("Memory", parent);
		Category savedCategory = repository.save(subCategory);
		
//		Category components = new Category("Smartphones", parent);
//		repository.saveAll(List.of(laptops, components));
		
		assertThat(savedCategory.getId()).isGreaterThan(0);

	} 
	
	@Test
	public void tesGetCategory() {
		Category category = repository.findById(2).get();
		
		System.out.println(category.getName());
		Set<Category> children = category.getChildren();
		
		for (Category subCategory : children) {
			System.out.println(subCategory.getName());
		}
		
		assertThat(children.size()).isGreaterThan(0);
	}

	@Test
	public void tesPrintHierarchicalCategories() {
		Iterable<Category> categories = repository.findAll();
		
		for (Category category : categories) {
			if (category.getParent() == null) {
				System.out.println(category.getName());
				
				Set<Category> children = category.getChildren();
				for (Category subCategory: children) {
					System.out.println("--"+subCategory.getName());
					printChildren(subCategory, 1);
				}
			}
		}
		
	}
	
	void printChildren(Category parent, int subLevel) {
		int subNewLvl = subLevel + 1;
		Set<Category> children = parent.getChildren();
		for (Category subCategory: children) {
			for (int i = 0; i < subNewLvl; i++) {
				System.out.print("--");
			}
			System.out.println(subCategory.getName());
			printChildren(subCategory, subNewLvl);
		}
	}
	

	@Test
	void tesFindRootCategories() {
		List<Category> categories = repository.listRootCategory();
		categories.forEach(cat -> System.out.println(cat.getName()));
//		System.out.println(categories.toString());
	}
	
*/	

	@Test
	void testFindByName() {
		String nameString = "Computers";
		
		Category category = repository.findByAlias("Computers1");
		System.out.println(category.getName() + "||" + category.getImage());
//		assertThat(category.getName(), name);
	}


}
