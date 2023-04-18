package com.shopme.admin.categories.export;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.shopme.common.entity.Category;
import com.shopme.common.entity.User;

public class CategoryExcelExporter extends AbstractExporter{
	XSSFWorkbook workbook ;
	XSSFSheet sheet; 
	
	public CategoryExcelExporter() {
		 workbook = new XSSFWorkbook(); 
	}
	
	private void writeHeaderLine() {
		 sheet = workbook.createSheet("Category"); 
		XSSFRow row = sheet.createRow(0);
		
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setFontHeight(14);
		cellStyle.setFont(font);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		
		createCell(row, 0, "Category Id", cellStyle);
		createCell(row, 1, "Category Name", cellStyle);
		createCell(row, 2, "Category Alias", cellStyle);
	}
	
	private void createCell(XSSFRow row, int coloumnIndex, Object value, CellStyle style) {
		XSSFCell cell = row.createCell(coloumnIndex);
		sheet.autoSizeColumn(coloumnIndex);
		if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		}else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		}else {
			cell.setCellValue((String) value);
		}
		
		cell.setCellStyle(style);
	}
	
	public void export(List<Category> listCategories, HttpServletResponse response) throws IOException{
	super.setResponseHeader( response, "application/octet-stream", ".xlsx", "Categories_");
	
		writeHeaderLine();
		writeDataLine(listCategories);
		
		ServletOutputStream outputStream = response.getOutputStream(); 
		workbook.write(outputStream) ; 
		workbook.close() ; 
		outputStream.close(); 
	
	}

	private void writeDataLine(List<Category> listCategories) {
		int rowIndex = 1;
		
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(false);
		font.setFontHeight(12);
		cellStyle.setFont(font);
		
		for(Category category : listCategories) {
			XSSFRow row = sheet.createRow(rowIndex++);
			int columnIndex = 0;
			createCell(row, columnIndex++, category.getId(), cellStyle);
			createCell(row, columnIndex++, category.getName(), cellStyle);
			createCell(row, columnIndex++, category.getAlias(), cellStyle);
		}
		
	}
}
