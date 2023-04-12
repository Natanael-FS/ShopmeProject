package com.shopme.admin.categories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryRestController {

	@Autowired
	private CategoriesService service;
	
	@PostMapping("/categories/check_unique")
	public String checkUnique(@Param("id") Integer id, @Param("name") String name, @Param("alias") String alias) {
		String result = service.checkUnique(id, name, alias);
		return result;
	}
}
