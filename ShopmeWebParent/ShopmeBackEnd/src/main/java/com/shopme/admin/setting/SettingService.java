package com.shopme.admin.setting;

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
	
	public GeneralSettingBag getGeneralSettingBag() {
		List<Setting> settings = new ArrayList<>();
		List<Setting> generalSettings = settingRepository.findByCategory(SettingCategory.GENERAL);
		List<Setting> currencySettings = settingRepository.findByCategory(SettingCategory.CURRENCY);
		
		settings.addAll(generalSettings);
		settings.addAll(currencySettings);
		
		return new GeneralSettingBag(settings);
	}
	
	public void saveAll(Iterable<Setting> settings) {
		settingRepository.saveAll(settings);
	}
	
	public List<Setting> getMailServerSettings() {
		return settingRepository.findByCategory(SettingCategory.MAIL_SERVER);
	}

	public List<Setting> getMailTemplateSettings() {
		return settingRepository.findByCategory(SettingCategory.MAIL_TEMPLATES);
	}	
}
