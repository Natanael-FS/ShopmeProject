package com.shopme.admin.categories.export;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shopme.common.entity.Category;
import com.shopme.common.entity.User;

public class CategoryCsvExporter extends AbstractExporter{

	public void export(List<Category> listCategories, HttpServletResponse response) throws IOException{
		
		super.setResponseHeader(response,"text/csv",".csv", "categories_");
		
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), 
				CsvPreference.STANDARD_PREFERENCE);
		
		String [] csvHeader = {"Category Id", "Category Name", "Category Alias"} ;
		String [] fieldMapping = {"id", "name", "alias"};
		
		csvWriter.writeHeader(csvHeader);
		
		for (Category category: listCategories) {
			category.setName(category.getName().replace("--", "  "));
			
			csvWriter.write(category,fieldMapping);
		}
		
		csvWriter.close();
		}
	
	
}
