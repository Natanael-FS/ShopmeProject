package com.shopme.admin.setting;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingCategory;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class SettingRepositoryTest {
	@Autowired
	SettingRepository repo;
	
	@Test
	public void testCreateGeneralTesting() {
//		Setting site = new Setting("SITE_NAME", "shopme", SettingCategory.GENERAL);
//		Setting siteLogo = new Setting("SITE_LOGO", "shopme.png", SettingCategory.GENERAL);
//		Setting copyRight = new Setting("COPYRIGHT", "Copyright (C) 2021 shopme Ltd.", SettingCategory.GENERAL);

//		repo.saveAll(List.of(siteLogo, copyRight));
//		Iterable<Setting> iterable = repo.findAll();
//		assertThat(iterable).size().isGreaterThan(0);
	}
	
	@Test
	public void testCreateCurrencySettings() {
		Setting currencyId = new Setting("CURRENCY_ID", "1", SettingCategory.CURRENCY);
		Setting symbol = new Setting("CURRENCY_SYMBOL", "$", SettingCategory.CURRENCY);
		Setting symbolPosition = new Setting("CURRENCY_SYMBOL_POSITION", "before", SettingCategory.CURRENCY);
		Setting decimalPointType = new Setting("DECIMAL_POINT_TYPE", "POINT", SettingCategory.CURRENCY);
		Setting decimalDigits = new Setting("DECIMAL_DIGITS", "2", SettingCategory.CURRENCY);
		Setting thousandsPointType = new Setting("THOUSANDS_POINT_TYPE", "COMMA", SettingCategory.CURRENCY);

		repo.saveAll(List.of(currencyId, symbol, symbolPosition, decimalPointType, 
				decimalDigits, thousandsPointType));
	}
		
	
	@Test
	public void testListSettingByCategory() {
		List<Setting> findByCategory = repo.findByCategory(SettingCategory.GENERAL);
		System.out.println(findByCategory.toString());
		
//		assertThat(findByCategory!=null);
		
	}

	
	
	@Test
	public void testRegisrty(ResourceHandlerRegistry registry) {
		/*
		  String pathPattern = "../product-images"; Path path = Paths.get(pathPattern);
		  String absolutePath = path.toFile().getAbsolutePath(); String logicalPath =
		  pathPattern.replace("../", "") + "/**";
		  registry.addResourceHandler(logicalPath).addResourceLocations("file:/"+absolutePath+"/"); 
		  System.out.println(registry.toString());
		 */		
		exposeDirectory("../product-images", registry);

	}
	
	private void exposeDirectory(String pathPattern, ResourceHandlerRegistry registry) {
		Path path =  Paths.get(pathPattern);
		String absolutePath = path.toFile().getAbsolutePath();
		String logicalPath = pathPattern.replace("../", "") + "/**";
		registry.addResourceHandler(logicalPath).addResourceLocations("file:/"+absolutePath+"/");	
		System.out.println(registry.toString());
	}

	
	@Test
	public void tesUpdateSetting() {
//		Setting setting = Se
//		System.out.println();
	}
}
