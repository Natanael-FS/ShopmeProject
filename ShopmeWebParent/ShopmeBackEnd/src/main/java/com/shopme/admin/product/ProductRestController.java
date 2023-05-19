package com.shopme.admin.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductRestController {
	
	@Autowired
	ProductService service;
	
	@PostMapping("/products/check_unique")
	public String checkUniqueness(@Param("id") Integer productId, @Param("name") String productName) {
		System.out.println("product-checkUnique-id : "+productId);

		String resultString = service.checkUniqueness(productId, productName);
		System.out.println("product-checkUnique : "+resultString);
		return resultString;
	};
}
