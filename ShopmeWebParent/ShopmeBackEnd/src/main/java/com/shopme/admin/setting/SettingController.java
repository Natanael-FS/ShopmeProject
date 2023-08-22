package com.shopme.admin.setting;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.coyote.http11.HttpOutputBuffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.currency.CurrencyRepository;
import com.shopme.common.entity.Currency;
import com.shopme.common.entity.Setting;

@Controller
public class SettingController {
	@Autowired
	private SettingService service;
	
	@Autowired
	private CurrencyRepository currencyRepo;
	
	@GetMapping("/settings")
	public String listAll(Model model) {
		List<Setting> listSettings = service.listAllSetting();
		List<Currency> listCurrencies = currencyRepo.findAllByOrderByNameAsc();
		System.out.println(listCurrencies.toString());
		
		model.addAttribute("listSettings", listSettings);
		model.addAttribute("listCurrencies", listCurrencies);
		
		for (Setting setting: listSettings) {
			System.out.println(setting.toString());
			model.addAttribute(setting.getKey(), setting.getValue());
		}
		
		return "settings/settings";
	}
	
	@PostMapping("/settings/save_general")
	public String saveGeneralSettings(@RequestParam("fileImage") MultipartFile multipartFile, HttpServletRequest request, RedirectAttributes ra) throws IOException {
		
		GeneralSettingBag settingBag = service.getGeneralSettingBag();
		
		SettingHelper.saveSiteLogo(multipartFile, settingBag);
		SettingHelper.saveCurrencySymbol(request, settingBag, currencyRepo);
		SettingHelper.updateSettingValuesFromForm(request, settingBag.list(), service);
		
		ra.addAttribute("meassage", "General Settings have been saved");
		
		return "redirect:/settings";
	}

}
