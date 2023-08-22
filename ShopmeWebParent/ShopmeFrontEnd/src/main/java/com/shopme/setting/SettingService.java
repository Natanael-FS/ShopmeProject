package com.shopme.setting;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingCategory;

@Service
public class SettingService {
	
	@Autowired
	SettingRepository settingRepository;
	
	public List<Setting>listAllSetting(){
		return (List<Setting>) settingRepository.findAll();
	}
	
	public List<Setting> getGeneralSettingBag() {
		return settingRepository.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
	}
	
}
