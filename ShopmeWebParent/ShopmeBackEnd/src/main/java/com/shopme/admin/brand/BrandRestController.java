package com.shopme.admin.brand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.admin.user.UserService;

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
}
