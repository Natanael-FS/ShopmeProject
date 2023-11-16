package com.shopme.admin.customer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.paging.PagingandSortingHelper;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;
import com.shopme.common.exception.CustomerNotFoundException;
import com.shopme.common.exception.ProductNotFoundException;

@Controller
public class CustomerController {
	
	@Autowired
	private CustomerService customerService;
	private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

	@GetMapping("/customers")
	public String listAll(Model model) {
//		return listByPage(1,"asc","firstName",model, null);
		return "redirect:/customers/page/1?sortField=name&sortDir=asc";

	}

	@GetMapping("/customers/page/{pageNum}")
	public String listByPage( @PagingAndSortingParam(listName = "listCustomers", moduleURL = "/customers") PagingandSortingHelper helper,
			@PathVariable(name="pageNum") int pageNum) {
		customerService.listByPage(pageNum, helper);
		
		return "Customer/customers";
	}
	
	@GetMapping("/customers/{id}/enabled/{status}")
	public String updateCustomerEnabledStatus(@PathVariable("id") Integer id, 
			@PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) throws CustomerNotFoundException  
	{
		customerService.updateCustomerEnabledStatus(id, enabled);
		
		String status = enabled ? "enabled" : "disabled";
		String message = "The Customer id " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		
		return "redirect:/customers";
	}

	@GetMapping("/customers/edit/{id}")
	public String editCustomer(Model model, @PathVariable(name = "id") Integer id, RedirectAttributes ra) {
		try {
			Customer customer = customerService.get(id);			
			List<Country> listCountries = customerService.listAllCountries();
			
			model.addAttribute("listCountries", listCountries);
			model.addAttribute("customer", customer);
			model.addAttribute("pageTitle", "Edit Customer (ID : " + id + ")");

			return "Customer/customers_form";
		} catch (CustomerNotFoundException e) {
			ra.addFlashAttribute("message",e.getMessage());
			return "redirect:/customers";
		}
		
	}

	@PostMapping("/customers/save")
	public String saveCustomer(Customer customer, Model model, RedirectAttributes ra) throws CustomerNotFoundException {
		Customer savedCustomer = customerService.save(customer);
		
		List<Country> listCountries = customerService.listAllCountries();
		model.addAttribute("listCountries", listCountries);
		
		ra.addFlashAttribute("message", "The Customer has been saved successfully");

		return "redirect:/customers";
	}
	
	@GetMapping("/customers/detail/{id}")
	public String detailCustomer(Model model, @PathVariable(name = "id") Integer id, RedirectAttributes ra) {
		try {
			Customer customer = customerService.get(id);
			model.addAttribute("customer", customer);
			
			return "Customer/customers_detail_modal";
		} catch (CustomerNotFoundException e) {
			ra.addFlashAttribute("message",e.getMessage());
			return "redirect:/customers";
		}
		
	}	
	
	@GetMapping("/customers/delete/{id}")
	public String deleteCustomer(@PathVariable(name = "id")Integer id, RedirectAttributes ra) throws CustomerNotFoundException
	{
		try {
			customerService.delete(id);
			
			String message = "The Customer id " + id + " has been deleted" ;
			ra.addFlashAttribute("message", message);
		} catch (CustomerNotFoundException e) {
			log.info("CustomerController | deleteCustomer | messageError : " + e.getMessage());
			
			ra.addFlashAttribute("messageError", e.getMessage());
		}

		return "redirect:/customers";
	}

}
