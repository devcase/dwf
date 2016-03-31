package dwf.persistence.export;

import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

/**
 * Classe utilitária para usar o eventModel do apache poi, reduzindo a memória necessária para
 * o tratamento de arquivos Excel
 * @author hirata
 *
 */
public abstract class XssfEventModelRowExtractor implements SheetContentsHandler, Row {
	
	protected abstract void processRow(Row row);
	
	private int rowNum;

	@Override
	public void startRow(int rowNum) {
		this.rowNum = rowNum;
		
	}

	@Override
	public void endRow(int rowNum) {
		processRow(this);
	}

	@Override
	public void cell(String cellReference, String formattedValue, XSSFComment comment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void headerFooter(String text, boolean isHeader, String tagName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Iterator<Cell> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cell createCell(int column) {
        throw new UnsupportedOperationException();
	}

	@Override
	public Cell createCell(int column, int type) {
        throw new UnsupportedOperationException();
	}

	@Override
	public void removeCell(Cell cell) {
        throw new UnsupportedOperationException();
	}

	@Override
	public void setRowNum(int rowNum) {
        throw new UnsupportedOperationException();
	}

	@Override
	public int getRowNum() {
		return rowNum;
	}

	@Override
	public Cell getCell(int cellnum) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cell getCell(int cellnum, MissingCellPolicy policy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public short getFirstCellNum() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public short getLastCellNum() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPhysicalNumberOfCells() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setHeight(short height) {
        throw new UnsupportedOperationException();
	}

	@Override
	public void setZeroHeight(boolean zHeight) {
        throw new UnsupportedOperationException();
	}

	@Override
	public boolean getZeroHeight() {
        throw new UnsupportedOperationException();
	}

	@Override
	public void setHeightInPoints(float height) {
        throw new UnsupportedOperationException();
	}

	@Override
	public short getHeight() {
        throw new UnsupportedOperationException();
	}

	@Override
	public float getHeightInPoints() {
        throw new UnsupportedOperationException();
	}

	@Override
	public boolean isFormatted() {
		return false;
	}

	@Override
	public CellStyle getRowStyle() {
        throw new UnsupportedOperationException();
	}

	@Override
	public void setRowStyle(CellStyle style) {
        throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<Cell> cellIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Sheet getSheet() {
        throw new UnsupportedOperationException();
	}

	@Override
	public int getOutlineLevel() {
        throw new UnsupportedOperationException();
	}

}
