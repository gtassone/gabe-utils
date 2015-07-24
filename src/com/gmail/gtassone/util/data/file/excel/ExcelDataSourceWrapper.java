package com.gmail.gtassone.util.data.file.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.gmail.gtassone.util.data.file.ColumnKey;
import com.gmail.gtassone.util.data.file.Row;
import com.gmail.gtassone.util.data.file.base.RowDataSourceWrapper;


public abstract class ExcelDataSourceWrapper
        extends RowDataSourceWrapper<Workbook> {

    // in order to retrieve values by key;
    // we need a list of names by index and types by index
    // which should be settable
    //
    // i think we have a LibraryExcelImporterFY15 -
    // not singleton
    // employs a wrapper, and other components
    //

    private InputStream stream;

    private File file;

    /**
     * the Excel workbook object.
     */
    private Workbook workbook;

    /**
     * the Excel sheet object.
     */
    private Sheet sheet = null;

    public ExcelDataSourceWrapper(Workbook wb) {
        this.workbook = wb;
    }

    public ExcelDataSourceWrapper(File file) {
        this.file = file;
    }

    public ExcelDataSourceWrapper(InputStream is) {
        this.stream = is;
    }

    @Override
    public void openDataSource() throws InvalidFormatException, FileNotFoundException, IOException {
        if (workbook == null) {
            if (file != null) {
                workbook = WorkbookFactory.create(new FileInputStream(file));
            }
            else if (stream != null) {
                workbook = WorkbookFactory.create(stream);
            }
            else throw new RuntimeException("no datasource");
        }
        
        sheet = workbook.getSheetAt(0);
    }
    

   // @Override
    public final Row getRow(int rowIndex) {
//      if (rowIndex < firstLineIndex || (lastLineIndex != null && rowIndex > lastLineIndex)) {
//        return null;
//      }
      org.apache.poi.ss.usermodel.Row row = sheet.getRow(rowIndex);
      return new RowImpl(row);
    }

   // @Override
    public final int getRowCount() { // should handle start and end indices
      return sheet.getPhysicalNumberOfRows();
    }

   // @Override
    public final String getValue(int rowIndex, int colIndex) {

//      if (rowIndex < firstLineIndex || rowIndex > lastLineIndex) {
//        return null;
//      }

      org.apache.poi.ss.usermodel.Row row = sheet.getRow(rowIndex);

      Cell cell = row.getCell(colIndex);
      return getCellValue(cell);
    }
    

   // @Override
    public final String getValue(int rowIndex, String columnName) {

      Integer colIndex = columnIndexMap.get(columnName);
      return getValue(rowIndex, colIndex);
    }


    @Override
    public void preValidate() {
    }

    @Override
    public void closeDataSource() {
    }

    @Override
    public void setDataSource(Workbook dataSource) {
        this.workbook = dataSource;
    }

    @Override
    public Workbook getDataSource() {
        return workbook;
    }

    @Override
    public final Iterator<Row> getRowIterator() {
        Iterator<org.apache.poi.ss.usermodel.Row> iter = sheet.iterator();
        if (firstLineIndex != null && firstLineIndex > 0) {
            for (int i = 0; i < firstLineIndex; i++) {
                iter.next();
            }
        }
        return new RowIter(iter);
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        else if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
            return cell.getRichStringCellValue().getString().trim();
        }
        else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
            return Double.toString(cell.getNumericCellValue());
        }
        else if (Cell.CELL_TYPE_BOOLEAN == cell.getCellType()) {
            return Boolean.toString(cell.getBooleanCellValue());
        }
        return null;
    }

    /**
     * implements Row interface and wraps excel row.
     * 
     */
    private final class RowImpl
            implements Row {

        private org.apache.poi.ss.usermodel.Row row;

        private RowImpl(org.apache.poi.ss.usermodel.Row row) {
            this.row = row;
        }

        @Override
        public int getColCount() {
            return row.getPhysicalNumberOfCells();
        }

        @Override
        public String getValue(int col) {

            Cell cell = row.getCell(col);
            return getCellValue(cell);
        }

        @Override
        public String getValue(String colName) {
            return (colName == null ? null : getValue(columnIndexMap.get(colName)));
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public <T> T getValue(ColumnKey<T> key) {
            // this should return values only for the allowed cell types
            return null;

        }

        @Override
        public int getIndex() {
            return row.getRowNum();
        }
    }

    /**
     * Row Iterator.
     */
    private class RowIter
            implements Iterator<Row> {

        private Iterator<org.apache.poi.ss.usermodel.Row> rowIter;

        public RowIter(Iterator<org.apache.poi.ss.usermodel.Row> rowIter) {
            this.rowIter = rowIter;
        }

        @Override
        public boolean hasNext() {
            return rowIter.hasNext();
        }

        @Override
        public RowImpl next() {
            return new RowImpl(rowIter.next());
        }

        @Override
        public void remove() {
            rowIter.remove();
        }
    }
}
