package dwf.persistence.export;

import java.io.IOException;
import java.io.InputStream;

import javax.validation.ValidationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import dwf.persistence.dao.DAO;
import dwf.persistence.domain.BaseEntity;

public abstract class BaseImporter<D extends BaseEntity<Long>> implements Importer<D>, ApplicationContextAware {
	private ApplicationContext applicationContext;
	protected final Class<D> clazz;
	protected final String entityFullName;
	protected final String entityName;
	
	public BaseImporter(Class<D> clazz)  {
		super();
		this.clazz = clazz;
		this.entityFullName = clazz.getName();
		this.entityName = StringUtils.uncapitalize(clazz.getSimpleName());
	}
	
	
	
	@Override
	public void importFromExcel(InputStream inputStream) throws IOException {
		//read the source file
		XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
		@SuppressWarnings("unchecked")
		DAO<D> dao = (DAO<D>) applicationContext.getBean(entityName + "DAO", DAO.class);

		int numberOfSheets =  workbook.getNumberOfSheets();
		for(int sheetIdx = 0; sheetIdx < numberOfSheets; sheetIdx++) {
			Sheet sheet = workbook.getSheetAt(sheetIdx);
			for(int rowNum = sheet.getFirstRowNum() + 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
				try {
					Row row = sheet.getRow(rowNum);
					if(!ignoreLine(row)){ 
						D domain = readLine(row);
						dao.importFromFile(domain);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	protected boolean ignoreLine(Row row) {
		if(row.getPhysicalNumberOfCells() == 0) return true;
		return false;
	}
	
	protected abstract D readLine(Row row) throws ValidationException;


	
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	protected Long getValueAsLong(Row row, int cellNum) {
		Cell c = row.getCell(cellNum);
		if(c == null) return null;
		
		switch(c.getCellType()) {
		case Cell.CELL_TYPE_BLANK:
			return null;
		case Cell.CELL_TYPE_NUMERIC: 
			return Long.valueOf((long)c.getNumericCellValue());
		case Cell.CELL_TYPE_STRING:
			return Long.valueOf(c.getStringCellValue());
		case Cell.CELL_TYPE_FORMULA:
			return Long.valueOf((long)c.getNumericCellValue());
		default: throw new IllegalArgumentException("Not a numeric value");
		}
	}
	
	protected String getValueAsString(Row row, int cellNum) {
		Cell c = row.getCell(cellNum);
		if(c == null) return null;
		String val;
		switch(c.getCellType()) {
		case Cell.CELL_TYPE_BLANK:
			return null;
		case Cell.CELL_TYPE_NUMERIC: 
			val = String.valueOf((long)c.getNumericCellValue());
			break;
		case Cell.CELL_TYPE_STRING:
			val = c.getStringCellValue();
			break;
		case Cell.CELL_TYPE_FORMULA:
			val = c.getStringCellValue();
			break;
		default: throw new IllegalArgumentException("Not a numeric value");
		}
		
		if(val != null) val = val.trim();
		
		return val;
	}
}
