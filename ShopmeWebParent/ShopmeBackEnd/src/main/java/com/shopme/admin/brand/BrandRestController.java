package com.shopme.admin.brand;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.admin.user.UserService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@RestController
public class BrandRestController {
	
	@Autowired
	private  BrandService brandService;
	
	@PostMapping("/brands/check_unique")
	public String checkDuplicateEmail(@Param("id") Integer id, @Param ("name") String name) {
		String resultString = brandService.checkUnique(id, name);
		System.out.println("brand-checkUnique : "+resultString);
		return resultString;
	}
	
	@GetMapping("/brands/{id}/categories")
	public List<CategoryDTO> listCategoriesByBrand(@PathVariable(name = "id") Integer brandId) throws BrandNotFoundRestException, BrandNotFoundException{
		List<CategoryDTO> listCategories = new ArrayList<>(); 

		try {
			Brand brand = brandService.get(brandId);
			Set<Category> categories = brand.getCategories();

			for (Category category : categories) {
				CategoryDTO dto = new CategoryDTO(category.getId(), category.getName());
				listCategories.add(dto);
			}

			return listCategories;
		} catch (BrandNotFoundException e) {
			throw new BrandNotFoundRestException();
		}
	}
	
}
