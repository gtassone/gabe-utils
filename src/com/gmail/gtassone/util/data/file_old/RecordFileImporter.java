package com.gmail.gtassone.util.data.file_old;

import java.io.File;
import java.util.Iterator;

import com.gmail.gtassone.util.data.AbstractImporter.RowValidator;

/**
 * interface for a generic file importer. presents an abstraction layer of the
 * file as a set of Rows accessible by column name or index. Provides
 * convenience accessor methods and a hook for row validation.
 * 
 * @author <a href=mailto:gtassone@gmail.com>gtassone</a>
 * @version $Revision$
 */
public interface RecordFileImporter {

  /**
   * Abstraction of a data entry row in a data file.
   * 
   * @author <a href=mailto:gtassone@cougaarsoftware.com>gtassone</a>
   * @version $Revision$
   */
  public interface Row {

    public boolean isValid();

    public int getColCount();

    public String getValue(int col);

    public String getValue(String colName);
  }



  /**
   * set the RowValidator for this importer.
   * 
   * @param v
   */
  public void setRowValidator(RowValidator v);

  /**
   * set the file path for this importer.
   * 
   * @param filePath
   */
  public void setFilePath(String filePath);

  /**
   * set the file for this importer.
   * 
   * @param file
   */
  public void setFile(File file);

  /**
   * declare the column names and indices. The two arrays must have the same
   * size, and indices[i] maps the index value to columnNames[i]
   * 
   * @param columnNames
   * @param indices
   */
  public void setColumns(String[] columnNames, int[] indices);

  /**
   * set the row to start processing in the file. defaults to 0.
   * 
   * @param index
   */
  public void setFirstRowIndex(int index);

  /**
   * set the row to stop processing in the file. defaults to eof.
   * 
   * @param index
   */
  public void setLastRowIndex(int index);

  /**
   * attempts to connect to the file.
   * 
   * @return
   * @throws Exception
   */
  public boolean openFile() throws Exception;

  /**
   * returns the number of rows in the file.
   * 
   * @return
   */
  public int getRowCount();

  /**
   * returns the value for given row, column location.
   * 
   * @param row
   * @param col
   * @return
   */
  public String getValue(int row, int col);

  /**
   * returns the value for given row, column location where column is identified
   * by name.
   * 
   * @param row
   *        the row index.
   * @param colName
   *        the column name, as specified in constructor columnNames array.
   * @return value of the data item for the given location.
   */
  public String getValue(int row, String colName);

  /**
   * returns row for given index.
   * 
   * @param index
   *        the row index.
   * @return
   */
  public Row getRow(int index);

  public Iterator<Row> getRowIterator();

}
