package com.shopme.admin.categories.export;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;


public class AbstractExporter {
	
	public void setResponseHeader(HttpServletResponse response, String contentType,
	String extension, String prefix) {
		
		DateFormat dateformatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-dd");
		String timestamp = dateformatter.format(new Date());
		String filename = prefix + timestamp + extension;
		
		response.setContentType(contentType);
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename="+filename;
		response.setHeader(headerKey, headerValue);		
	}

}