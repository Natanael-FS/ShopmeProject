package com.shopme.admin.user.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.util.StringUtils;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.paging.PagingandSortingHelper;
import com.shopme.admin.user.UserNotFoundException;
import com.shopme.admin.user.UserService;
import com.shopme.admin.user.export.UserCsvExporter;
import com.shopme.admin.user.export.UserExcelExporter;
import com.shopme.admin.user.export.UserPdfExporter;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;



@Controller
public class UserController {
	@Autowired	
	private UserService userService;
	
	
	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	
	@GetMapping("/users")
	public String listFirstPage() {
		return "redirect:/users/page/1?sortField=firstName&sortDir=asc";
	}
	
	@GetMapping("/users/page/{pageNum}")
	public String listByPage(@PagingAndSortingParam(listName = "listUsers", moduleURL = "/users") PagingandSortingHelper helper,
			@PathVariable (name = "pageNum") int pageNum) {
		
		userService.listByPage(pageNum, helper);
		
		return"users/users";
	}
	
	
	@GetMapping("users/new")
	public String newUser(Model model) {
		List<Role> listRoles = userService.listRoles();
		
		User user = new User();
		user.setEnabled(true);
		model.addAttribute("user",user);
		model.addAttribute("listRoles", listRoles);
		model.addAttribute("pageTitle", "Create New User");
		return"users/users_form";
	}
	
	
	@PostMapping("/users/save")
	public String saveuser(User user, RedirectAttributes redirectAttributes, @RequestParam("Image") MultipartFile multipartFile) throws IOException {

		if(!multipartFile.isEmpty()) {
			String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			
			user.setPhotos(filename);
			
			User savedUser = userService.save(user);
			
			String uploadDir = "user-photos/"+savedUser.getId();
			
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, filename, multipartFile);
		}
		else {
			if (user.getPhotos().isEmpty())
				user.setPhotos(null);
				 userService.save(user);
		}
				
		userService.save(user);
		
		redirectAttributes.addFlashAttribute("message", "The user has been saved successfully");
		
		return getRedirectUrlToAffectedUser(user);
	}

	private String getRedirectUrlToAffectedUser(User user) {
		String firstPartEmailString = user.getEmail().split("@")[0];
		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword="+firstPartEmailString;
	}
	
	
	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id,
		 Model model, RedirectAttributes redirectAttributes)
	{
		try {
			List<Role> listRoles = userService.listRoles();

			User user = userService.get(id);
			model.addAttribute("user", user);
			model.addAttribute("pageTitle", "Edit User (ID : " + id + ")");
			model.addAttribute("listRoles", listRoles);

			return "users/users_form";			
		} catch (UserNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message",ex.getMessage());
			return "redirect:/users";
		}
	}
	
	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id,
		 Model model, RedirectAttributes redirectAttributes)
	{
		try {
			 userService.delete(id);

				redirectAttributes.addFlashAttribute("message", "The User ID " +id + " has been deleted successfully");

		} catch (UserNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message",ex.getMessage());
		}
		
		return "redirect:/users";

	}
	
	@GetMapping("/users/{id}/enabled/{status}")
	public String updateUserEnabledStatus(@PathVariable("id") Integer id, 
			@PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes)
	{
		userService.updateUserEnabledStatus(id, enabled);
		
		String status = enabled ? "enabled" : "disabled";
		String message = "The User id " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		
		return "redirect:/users";
	}
	
	@GetMapping("/users/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<User> listusers = userService.listAll();
		
		UserCsvExporter exporter = new UserCsvExporter();
		log.info("UserController | exportToCSV | export is starting");
		exporter.export(listusers, response);
		log.info("UserController | exportToCSV | export completed");		
	}
	
	
	@GetMapping("/users/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		List<User> listusers = userService.listAll();
		
		UserExcelExporter exporter = new UserExcelExporter();
		log.info("UserController | exportToExcel | export is starting");
		exporter.export(listusers, response);
		log.info("UserController | exportToExcel | export completed");		
	}		

	
	@GetMapping("/users/export/pdf")
	public void exportToPdf(HttpServletResponse response) throws IOException {
		List<User> listusers = userService.listAll();
		
		UserPdfExporter exporter = new UserPdfExporter();
		log.info("UserController | exportToPdf | export is starting");
		exporter.export(listusers, response);
		log.info("UserController | exportToPdf | export completed");		
	}
		
}
