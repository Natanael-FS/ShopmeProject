package com.shopme.admin.categories.export;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.User;

public class CategoryPdfExporter extends AbstractExporter{

	public void export(List<Category> listcategories, HttpServletResponse response) throws IOException {
		super.setResponseHeader( response, "application/pdf", ".pdf", "Categories_");
		
		Document document = new Document(PageSize.A4);
		PdfWriter.getInstance(document, response.getOutputStream());
		
		document.open();
		
		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		font.setSize(18);
		font.setColor(Color.BLUE);
		
		Paragraph paragraph = new Paragraph("List Of Categories", font);
		paragraph.setAlignment(Paragraph.ALIGN_CENTER);
		
		document.add(paragraph);
		
		PdfPTable table = new PdfPTable(3);
		table.setWidthPercentage(100f);
		table.setSpacingBefore(10);
		table.setHorizontalAlignment(Paragraph.ALIGN_CENTER);
		table.setWidths(new float[] {1.5f,3.0f,3.0f} );
		
		writeTableHeader(table);
		writeTableData(table ,listcategories);
		
		document.add(table);
		document.close();
	}

	private void writeTableData(PdfPTable table, List<Category> listcategories) {
		for (Category categories : listcategories) {
			table.addCell(String.valueOf (categories.getId()));
			table.addCell(categories.getName());
			table.addCell(categories.getAlias());
			}
	}

	private void writeTableHeader(PdfPTable table) {
		PdfPCell cell = new PdfPCell(); 
		cell.setBackgroundColor(Color.BLUE);
		cell.setPadding(5);
		
		Font font = FontFactory.getFont(FontFactory.HELVETICA);
		font.setColor(Color.WHITE);
		
		cell.setPhrase(new Phrase("Category ID", font));
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("Category Name", font));
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("Category Alias", font));
		table.addCell(cell);
	}

}
