/**
 * Copyright 2001-2008, Cougaar Technologies, Inc. Originally authored under
 * agreement by Cougaar Software, Inc. with all Copyright and Rights
 * transferred. This file constitutes Proprietary Data. Cougaar Technologies,
 * Inc. reserves All Rights to this work product and any updates or derivatives.
 * This software is provided as part of the ActiveEdge product and use implies
 * acceptance of the ActiveEdge End-User License Agreement. Use of this software
 * or any derivative by any party outside of that License Agreement is strictly
 * prohibited. For a copy of the license, mail info@cougaarsoftware.com Cougaar
 * Software, Inc. is the exclusive reseller of Cougaar Technologies ActiveEdge
 * product through 2010 and is authorized as part of that agreement to re-brand
 * ActiveEdge as a Cougaar Software, Inc. product for purposes of bids,
 * marketing, sales and contracting. $LastChangedDate$ $LastChangedBy$
 * $Revision$
 */
package com.gmail.gtassone.util.data.file_old;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * File Importer which can access any Excel file, .xls or .xlsx.
 * 
 * @author <a href=mailto:gtassone@cougaarsoftware.com>gtassone</a>
 * @version $Revision$
 */
public class ExcelImporter extends AbstractImporter implements
    RecordFileImporter {

  /**
   * the Excel sheet object.
   */
  private Sheet sheet = null;

  /**
   * log.
   */
  protected static final Logger logger = Logger.getLogger(ExcelImporter.class);

  /**
   * set up logging independently.
   * 
   * @deprecated do logging configuration from a main application.
   * @param logPropertiesPath
   *        logging file path.
   */
  public final void setupLogging(String logPropertiesPath) {
    PropertyConfigurator.configure(logPropertiesPath);
  }

  /**
   * default constructor.
   */
  public ExcelImporter() {
    this.filePath = null;
    this.columnNames = null;
    this.columnIndexMap = null;
    firstLineIndex = 0;
    lastLineIndex = null;
  }

  /**
   * @param file
   *        the file to import.
   */
  public ExcelImporter(File file) {
    this();
    if (file != null) {
      setFile(file);
      setFilePath(file.getAbsolutePath());
    }
  }

  /**
   * opens the file.
   * 
   * @return if the file was successfully opened.
   */
  public final boolean openFile() {
    try {

      Workbook wb = null;

      if (file.getAbsolutePath().endsWith("xlsx")) {
        wb = new XSSFWorkbook(file.getAbsolutePath());
      } else if (file.getAbsolutePath().endsWith("xls")) {
        wb = WorkbookFactory.create(new FileInputStream(file));
      }

      sheet = wb.getSheetAt(0);
      return true;
    } catch (Exception e) {
      logger.error(e);
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public final Row getRow(int rowIndex) {
    if (rowIndex < firstLineIndex || rowIndex > lastLineIndex) {
      return null;
    }
    org.apache.poi.ss.usermodel.Row row = sheet.getRow(rowIndex);
    return new RowImpl(row);
  }

  @Override
  public final int getRowCount() { // should handle start and end indices
    return sheet.getPhysicalNumberOfRows();
  }

  @Override
  public final String getValue(int rowIndex, int colIndex) {

    if (rowIndex < firstLineIndex || rowIndex > lastLineIndex) {
      return null;
    }

    org.apache.poi.ss.usermodel.Row row = sheet.getRow(rowIndex);

    Cell cell = row.getCell(colIndex);
    return getCellValue(cell);
  }

  @Override
  public final String getValue(int rowIndex, String columnName) {

    Integer colIndex = columnIndexMap.get(columnName);
    return getValue(rowIndex, colIndex);
  }

  @Override
  public final Iterator<Row> getRowIterator() {
    return new RowImplIter(sheet.iterator());
  }

  private String getCellValue(Cell cell) {
    if (cell == null) {
      return null;
    } else if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
      return cell.getRichStringCellValue().getString().trim();
    } else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
      return Double.toString(cell.getNumericCellValue());
    }
    return null;
  }

  /**
   * implements Row interface and wraps excel row.
   * 
   * @author <a href=mailto:gtassone@cougaarsoftware.com>gtassone</a>
   * @version $Revision$
   */
  private final class RowImpl implements Row {

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
      if (rv == null) {
        return true;
      }
      return rv.isValid(this);
    }
  }

  /**
   * @author <a href=mailto:gtassone@cougaarsoftware.com>gtassone</a>
   * @version $Revision$
   */
  private class RowImplIter implements Iterator<Row> {

    private Iterator<org.apache.poi.ss.usermodel.Row> rowIter;

    public RowImplIter(Iterator<org.apache.poi.ss.usermodel.Row> rowIter) {
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
