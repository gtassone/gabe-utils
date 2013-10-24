package com.gmail.gtassone.util.data.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Vector;

/**
 * File importer for CSV files. This importer reads the entire data contents
 * into memory, and is not appropriate for very large files. In order to support
 * very large files, you would need to follow an approach similar to
 * {@link FixedFieldFileImporter}, adapted to support variable-length rows. For
 * example, the file could be parsed, with each row being mapped to a temporary
 * file which records the byte offset for the rows in a fixed field format. then
 * the temporary index file can be accessed as a RandomAccessFile, to determine
 * byte offset for a given row.
 * 
 * @author <a href=mailto:gtassone@gmail.com>gtassone</a>
 * @version $Revision$
 */
public class CSVImporter extends AbstractImporter implements RecordFileImporter {

  /**
   * constructor.
   */
  public CSVImporter() {
    this.filePath = null;
    this.columnNames = null;
    this.columnIndexMap = null;
    firstLineIndex = 0;
    lastLineIndex = null;
  }

  /**
   * constructor.
   * 
   * @param filePath
   *        file.
   */
  public CSVImporter(String filePath) {
    this();
    this.filePath = filePath;
  }

  /**
   * constructor.
   * 
   * @param file
   *        file.
   */
  public CSVImporter(File file) {
    this();
    this.file = file;
    this.filePath = file.getAbsolutePath();
  }

  /**
   * constructor.
   * 
   * @param columnNames
   *        columns.
   */
  public CSVImporter(String[] columnNames) {
    this();
    this.columnNames = columnNames;
    buildColumnIndexMap();
  }

  /**
   * constructor.
   * 
   * @param filePath
   *        file.
   * @param columnNames
   *        columns.
   */
  public CSVImporter(String filePath, String[] columnNames) {
    this(filePath);
    this.columnNames = columnNames;
    buildColumnIndexMap();
  }

  /**
   * constructor.
   * 
   * @param file
   *        file.
   * @param columnNames
   *        columns.
   */
  public CSVImporter(File file, String[] columnNames) {
    this(file);
    this.columnNames = columnNames;
    buildColumnIndexMap();
  }

  /**
   * opens file, and reads data contents into memory.
   * 
   * @return true if file opened successfully.
   */
  public final boolean openFile() {

    if (filePath == null || "".equals(filePath)) {
      System.out.println("filepath is empty");
      return false;
    }
    File csvFile = new File(filePath);
    if (!csvFile.exists() || !csvFile.canRead()) {
      System.out.println("file does not exist or cannot be read");
      return false;
    }

    try {
      FileReader fileReader = new FileReader(csvFile);
      BufferedReader lineReader = new BufferedReader(fileReader);
      rowList = new Vector<String[]>();

      String line;
      int linecount = 0;

      while ((line = lineReader.readLine()) != null) {

        String[] row = line.split(",");
        // rowMap.put(linecount, row);

        if (linecount >= firstLineIndex) {
          rowList.add(row);
        }
        linecount++;

      }

    } catch (Exception e) {
      e.printStackTrace(System.out);
      return false;
    }

    return true;
  }

  @Override
  public final Row getRow(int rowIndex) {
    if (rowList.size() > rowIndex) {
      return new RowImpl(rowList.get(rowIndex));
    } else {
      return null;
    }
  }

  @Override
  public final int getRowCount() {
    if (rowList == null) {
      return 0;
    }
    return rowList.size();
  }

  @Override
  public final String getValue(int rowIndex, int colIndex) {

    if (rowList.size() > rowIndex) {
      if (rowList.get(rowIndex) != null
          && rowList.get(rowIndex).length > colIndex) {
        return rowList.get(rowIndex)[colIndex].trim();
      }
    }

    return null;
  }

  @Override
  public final String getValue(int rowIndex, String columnName) {
    Integer colIndex = columnIndexMap.get(columnName);

    if (colIndex != null) {
      return getValue(rowIndex, colIndex);
    }

    return null;
  }

  @Override
  public final Iterator<Row> getRowIterator() {
    return new RowIter(rowList.iterator());
  }

  /**
   * Iterator implementation which wraps another iterator.
   * 
   * @author <a href=mailto:gtassone@cougaarsoftware.com>gtassone</a>
   * @version $Revision$
   */
  private final class RowIter implements Iterator<Row> {

    private Iterator<String[]> recordIter;

    public RowIter(Iterator<String[]> iter) {
      this.recordIter = iter;
    }

    @Override
    public boolean hasNext() {
      return recordIter.hasNext();
    }

    @Override
    public Row next() {
      return new RowImpl(recordIter.next());
    }

    @Override
    public void remove() {
      recordIter.remove();
    }
  }

  /**
   * Row implementation which wraps a String array.
   * 
   * @author <a href=mailto:gtassone@cougaarsoftware.com>gtassone</a>
   * @version $Revision$
   */
  private final class RowImpl implements Row {

    private String[] vals;

    public RowImpl(String[] row) {
      vals = row;
    }

    @Override
    public int getColCount() {
      return vals.length;
    }

    @Override
    public String getValue(int col) {
      if (vals.length < (col + 1)) {
        return null;
      }
      return vals[col];
    }

    @Override
    public String getValue(String colName) {
      Integer col = columnIndexMap.get(colName);
      return (col == null ? null : getValue(col));
    }

    @Override
    public boolean isValid() {
      if (rv == null) {
        return true;
      }
      return rv.isValid(this);
    }

  }

}
