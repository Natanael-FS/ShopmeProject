package com.shopme.admin.user;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;


public class AbstractExporter {
	public void setResponseHeader(HttpServletResponse response, 
			String contentType, String extention) throws IOException{
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd_HH-mm-ss");
		
		String timestamp = dateFormat.format(new Date());
		String filename  = "users_" + timestamp + extention;
		
		response.setContentType(contentType);
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename="+filename;
		
		response.setHeader(headerKey, headerValue);
	}
}
