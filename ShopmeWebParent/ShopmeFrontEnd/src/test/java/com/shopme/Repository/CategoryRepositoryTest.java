package com.shopme.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.shopme.category.CategoryRepository;
import com.shopme.common.entity.Category;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class CategoryRepositoryTest {
	@Autowired
	private CategoryRepository repo;
	
	@Test
	public void testListEnabledCategories() {
		List<Category> list = repo.findAllEnabled();
		list.forEach(category -> {
//			String cat = (category.getParent().getName()==null) ? "0" : category.getParent().getName();			
//			System.out.println(category.getName() + " (" +category.getEnabled() + ") , " + cat );
			
			System.out.println(category.getName() + " (" +category.isEnabled() + ") ");
		});
	}
	
	@Test
	public void testListEnabledCategories2() {
		List<Category> listNoChildrenCategories = listNoChildrenCategories();
		System.out.println(listNoChildrenCategories.toString());
	}
	
	public List<Category> listNoChildrenCategories() {
		List<Category> listNoChildrenCategories = new ArrayList<>();

		List<Category> listEnabledCategories = repo.findAllEnabled();

		listEnabledCategories.forEach(category -> {
			Set<Category> children = category.getChildren();
			if (children == null || children.size() == 0) {
				listNoChildrenCategories.add(category);
			}
		});

		return listNoChildrenCategories;
	}
	
}
