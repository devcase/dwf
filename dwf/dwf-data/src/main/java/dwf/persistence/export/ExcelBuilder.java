package dwf.persistence.export;


import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelBuilder {

	private final Workbook workbook;
	private final Sheet principalSheet;
	private final CreationHelper createHelper;
	private final DataFormat dataFormat;
	
	private final CellStyle titleStyle;
	private final CellStyle defaultStyle;
	private final CellStyle hlinkStyle;
	private final CellStyle dateCellStyle;
	
	//current!
	private CellStyle currentStyle;
	private int currentRowNumber;
	private int currentColumnNumber;
	private Row currentRow;
	
	public ExcelBuilder(String sheetName) {
		this(sheetName, 25);
	}
	public ExcelBuilder(String sheetName, int defaultColumnWidth) {
		super();
		
		workbook = new XSSFWorkbook();
		principalSheet = workbook.createSheet(sheetName);
		createHelper = workbook.getCreationHelper();
		dataFormat = workbook.createDataFormat();
		/** Formatação */
		if(defaultColumnWidth > 0) {
			principalSheet.setDefaultColumnWidth(defaultColumnWidth);
		}

		this.defaultStyle = workbook.createCellStyle();
		defaultStyle.setWrapText(true);
		defaultStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		defaultStyle.setShrinkToFit(true);

		Font titleFont = workbook.createFont();
		titleFont.setFontHeightInPoints((short) 10);
		titleFont.setFontName("Arial");
		titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);

		this.titleStyle = workbook.createCellStyle();
		titleStyle.cloneStyleFrom(defaultStyle);
		titleStyle.setFont(titleFont);
		titleStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		titleStyle.setShrinkToFit(true);

		this.hlinkStyle = workbook.createCellStyle();
	    Font hlink_font = workbook.createFont();
	    hlink_font.setUnderline(Font.U_SINGLE);
	    hlink_font.setColor(IndexedColors.BLUE.getIndex());
	    hlinkStyle.setFont(hlink_font);
	    hlinkStyle.setWrapText(false);
	    hlinkStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
	    
	    dateCellStyle = workbook.createCellStyle();
	    dateCellStyle.cloneStyleFrom(defaultStyle);
	    dateCellStyle.setDataFormat(dataFormat.getFormat("[$-416]dd/mm/yyyy hh:mm"));
	    dateCellStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP); 
		
	    this.currentRowNumber = 0;
		// cria a 1a. linha
		currentRow = principalSheet.createRow(currentRowNumber);
		currentStyle = titleStyle;
		this.currentColumnNumber = 0;

	}
	

	public ExcelBuilder newLine() {
		this.currentRow = principalSheet.createRow(++currentRowNumber);
		this.currentStyle = defaultStyle;
		this.currentColumnNumber = 0;
		
		return this;
	}

	public ExcelBuilder column(Object object) {
		Cell cell = currentRow.createCell(currentColumnNumber++);
		if(object == null) {
			cell.setCellType(Cell.CELL_TYPE_BLANK);
			cell.setCellStyle(this.currentStyle);
		} else if(object instanceof Number) {
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell.setCellValue(((Number) object).doubleValue());
			cell.setCellStyle(this.currentStyle);
		} else if(object instanceof Calendar) {
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell.setCellValue((Calendar) object);
			cell.setCellStyle(this.dateCellStyle);
		} else if(object instanceof Date) {
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell.setCellValue((Date) object);
			cell.setCellStyle(this.dateCellStyle);
		} else if(object instanceof Boolean) {
			cell.setCellType(Cell.CELL_TYPE_BOOLEAN);
			cell.setCellValue((Boolean) object);
			cell.setCellStyle(this.currentStyle);
		} else  {
			cell.setCellType(Cell.CELL_TYPE_STRING);
			String val = object.toString();
			
			//se o conteúdo da coluna começar com http:// ou https://, cria um link
			if(val != null && (val.startsWith("http://") || val.startsWith("https://"))) {
				Hyperlink link = this.createHelper.createHyperlink(Hyperlink.LINK_URL);
			    link.setAddress(val);
			    cell.setHyperlink(link);
			    cell.setCellStyle(this.hlinkStyle);
			} else {
				cell.setCellStyle(this.currentStyle);
			}
			cell.setCellValue(val);
		}
		
		return this;
	}
	
	public void write(OutputStream os) throws IOException{
		workbook.write(os);
	}
	
}
