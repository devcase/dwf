package dwf.persistence.export;

import java.io.IOException;
import java.io.InputStream;

import javax.validation.ValidationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import dwf.persistence.dao.DAO;
import dwf.persistence.domain.BaseEntity;

public abstract class BaseImporter<D extends BaseEntity<?>> implements Importer<D> {
	private Log log = LogFactory.getLog(BaseImporter.class);
	
	@Autowired
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
	public void importFromExcel(InputStream inputStream, int size) throws IOException {
		log.info("Start loading excel file");
		//read the source file
		XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
		log.info("Excel file loading complete");
		try {
			@SuppressWarnings("unchecked")
			final DAO<D> dao = (DAO<D>) applicationContext.getBean(entityName + "DAO", DAO.class);
			
	
			int numberOfSheets =  workbook.getNumberOfSheets();
			log.debug("Number of sheets from workbook: " + numberOfSheets);
			for(int sheetIdx = 0; sheetIdx < numberOfSheets; sheetIdx++) {
				Sheet sheet = workbook.getSheetAt(sheetIdx);
				for(int rowNum = sheet.getFirstRowNum() + 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
					if(log.isDebugEnabled() && (rowNum < 100 || (rowNum < 1000 && rowNum % 10 == 0) || (rowNum % 100 == 0))) {
						log.debug("Reading rowNum " + rowNum);
					}
					try {
						Row row = sheet.getRow(rowNum);
						if(!ignoreLine(row)){ 
							D domain = readLine(row);
							dao.importFromFile(domain);
						}
					} catch (Exception ex) {
						log.error("Erro ao importar arquivo excel, linha " + (rowNum + 1), ex);
					}
				}
			}
		} finally {
			workbook.close();
		}
	}
	
	protected boolean ignoreLine(Row row) {
		if(row.getPhysicalNumberOfCells() == 0) return true;
		return false;
	}
	
	protected abstract D readLine(Row row) throws ValidationException;


	
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
	protected Integer getValueAsInteger(Row row, int cellNum) {
		Cell c = row.getCell(cellNum);
		if(c == null) return null;
		
		switch(c.getCellType()) {
		case Cell.CELL_TYPE_BLANK:
			return null;
		case Cell.CELL_TYPE_NUMERIC: 
			return Integer.valueOf((int)c.getNumericCellValue());
		case Cell.CELL_TYPE_STRING:
			return Integer.valueOf(c.getStringCellValue());
		case Cell.CELL_TYPE_FORMULA:
			return Integer.valueOf((int)c.getNumericCellValue());
		default: throw new IllegalArgumentException("Not a numeric value");
		}
	}

	protected Double getValueAsDouble(Row row, int cellNum) {
		Cell c = row.getCell(cellNum);
		if(c == null) return null;
		
		switch(c.getCellType()) {
		case Cell.CELL_TYPE_BLANK:
			return null;
		case Cell.CELL_TYPE_NUMERIC: 
			return Double.valueOf((double)c.getNumericCellValue());
		case Cell.CELL_TYPE_STRING:
			String value = c.getStringCellValue();
			if(StringUtils.isBlank(value)) return null;
			return Double.valueOf(value);
		case Cell.CELL_TYPE_FORMULA:
			return Double.valueOf((long)c.getNumericCellValue());
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
		default: throw new IllegalArgumentException("Not a valid cell type: " + c.getCellType());
		}
		
		if(val != null) val = val.trim();
		
		return val;
	}



	@Override
	public String getEntityName() {
		return entityName;
	}
	
	
}
