package dwf.persistence.export;

import java.io.IOException;
import java.io.InputStream;

import javax.validation.ValidationException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.model.CommentsTable;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import dwf.persistence.dao.DAO;
import dwf.persistence.domain.BaseEntity;

public abstract class AbstractStreamingImporter<D extends BaseEntity<?>> implements Importer<D> {
	private Log log = LogFactory.getLog(AbstractStreamingImporter.class);

	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private SessionFactory sessionFactory;
	protected final Class<D> clazz;
	protected final String entityFullName;
	protected final String entityName;

	public AbstractStreamingImporter(Class<D> clazz) {
		super();
		this.clazz = clazz;
		this.entityFullName = clazz.getName();
		this.entityName = StringUtils.uncapitalize(clazz.getSimpleName());
	}

	@Override
	public void importFromExcel(InputStream inputStream) throws IOException {
		@SuppressWarnings("unchecked")
		final DAO<D> dao = (DAO<D>) applicationContext.getBean(entityName + "DAO", DAO.class);
		final Session session = sessionFactory.getCurrentSession();

		SheetContentsHandler sheetContentsExtractor = new SheetContentsHandler() {
			@Override
			public void startRow(int arg0) {
			}

			@Override
			public void headerFooter(String arg0, boolean arg1, String arg2) {
			}

			@Override
			public void endRow(int arg0) {
				try {
					Row row = sheet.getRow(rowNum);
					if (!ignoreLine(row)) {
						D domain = readLine(row);
						domain = dao.importFromFile(domain);
					}
				} catch (Exception ex) {
					log.error("Erro ao importar arquivo excel, linha " + (rowNum + 1), ex);
				}

			}

			@Override
			public void cell(String arg0, String arg1, XSSFComment arg2) {
			}
		};
		
		
		try {
			OPCPackage pkg = OPCPackage.open(inputStream);

			ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
			XSSFReader xssfReader = new XSSFReader(pkg);
			StylesTable styles = xssfReader.getStylesTable();
			XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
			
		    DataFormatter formatter = new DataFormatter(); //TODO Como detectar o locale correto? É o locale do arquivo ou do usuário?
		    boolean formulasNotResults = true; //TODO fórmulas não vão funcionar
			
			while (iter.hasNext()) {
				InputStream sheetInputStream = iter.next();
				CommentsTable comments = iter.getSheetComments();

				InputSource sheetSource = new InputSource(sheetInputStream);
				try {
					XMLReader sheetParser = SAXHelper.newXMLReader();
					ContentHandler handler = new XSSFSheetXMLHandler(styles, comments, strings, sheetContentsExtractor, formatter, formulasNotResults);
					sheetParser.setContentHandler(handler);
					sheetParser.parse(sheetSource);
				} catch (ParserConfigurationException e) {
					throw new RuntimeException("SAX parser appears to be broken - " + e.getMessage());
				}

			}

		} catch (OpenXML4JException e) {
			throw new IOException(e);
		} catch (SAXException e) {
			throw new IOException(e);
		}
	}

	protected boolean ignoreLine(Row row) {
		if (row.getPhysicalNumberOfCells() == 0)
			return true;
		return false;
	}

	protected abstract D readLine(Row row) throws ValidationException;

	protected Long getValueAsLong(Row row, int cellNum) {
		Cell c = row.getCell(cellNum);
		if (c == null)
			return null;

		switch (c.getCellType()) {
		case Cell.CELL_TYPE_BLANK:
			return null;
		case Cell.CELL_TYPE_NUMERIC:
			return Long.valueOf((long) c.getNumericCellValue());
		case Cell.CELL_TYPE_STRING:
			return Long.valueOf(c.getStringCellValue());
		case Cell.CELL_TYPE_FORMULA:
			return Long.valueOf((long) c.getNumericCellValue());
		default:
			throw new IllegalArgumentException("Not a numeric value");
		}
	}

	protected Integer getValueAsInteger(Row row, int cellNum) {
		Cell c = row.getCell(cellNum);
		if (c == null)
			return null;

		switch (c.getCellType()) {
		case Cell.CELL_TYPE_BLANK:
			return null;
		case Cell.CELL_TYPE_NUMERIC:
			return Integer.valueOf((int) c.getNumericCellValue());
		case Cell.CELL_TYPE_STRING:
			return Integer.valueOf(c.getStringCellValue());
		case Cell.CELL_TYPE_FORMULA:
			return Integer.valueOf((int) c.getNumericCellValue());
		default:
			throw new IllegalArgumentException("Not a numeric value");
		}
	}

	protected Double getValueAsDouble(Row row, int cellNum) {
		Cell c = row.getCell(cellNum);
		if (c == null)
			return null;

		switch (c.getCellType()) {
		case Cell.CELL_TYPE_BLANK:
			return null;
		case Cell.CELL_TYPE_NUMERIC:
			return Double.valueOf((double) c.getNumericCellValue());
		case Cell.CELL_TYPE_STRING:
			return Double.valueOf(c.getStringCellValue());
		case Cell.CELL_TYPE_FORMULA:
			return Double.valueOf((long) c.getNumericCellValue());
		default:
			throw new IllegalArgumentException("Not a numeric value");
		}
	}

	protected String getValueAsString(Row row, int cellNum) {
		Cell c = row.getCell(cellNum);
		if (c == null)
			return null;
		String val;
		switch (c.getCellType()) {
		case Cell.CELL_TYPE_BLANK:
			return null;
		case Cell.CELL_TYPE_NUMERIC:
			val = String.valueOf((long) c.getNumericCellValue());
			break;
		case Cell.CELL_TYPE_STRING:
			val = c.getStringCellValue();
			break;
		case Cell.CELL_TYPE_FORMULA:
			val = c.getStringCellValue();
			break;
		default:
			throw new IllegalArgumentException("Not a numeric value");
		}

		if (val != null)
			val = val.trim();

		return val;
	}

	@Override
	public String getEntityName() {
		return entityName;
	}

}
