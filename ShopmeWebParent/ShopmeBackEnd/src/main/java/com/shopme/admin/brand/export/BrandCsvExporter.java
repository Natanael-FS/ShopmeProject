package com.shopme.admin.brand.export;

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

import com.shopme.common.entity.Brand;

public class BrandCsvExporter extends AbstractExporter{

	public void export(List<Brand> listbrands, HttpServletResponse response) throws IOException{
		
		super.setResponseHeader(response,"text/csv",".csv" );
		
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), 
				CsvPreference.STANDARD_PREFERENCE);
		
		String [] csvHeader = {"Brand Id", "Brand Name", "Last Name", "Roles", "Enabled"} ;
		String [] fieldMapping = {"id", "email", "firstName", "lastName", "roles", "enabled"} ;
		
		csvWriter.writeHeader(csvHeader);
		
		for (Brand brand : listbrands) {
			csvWriter.write(brand,fieldMapping);
		}
		
		csvWriter.close();
		}
	
	
}
