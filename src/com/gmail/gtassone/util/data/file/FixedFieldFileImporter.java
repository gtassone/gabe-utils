package com.gmail.gtassone.util.data.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.Iterator;

/**
 * importer for fixed field files.
 * 
 * @author <a href=mailto:gtassone@cougaarsoftware.com>gtassone</a>
 * @version $Revision$
 */
public class FixedFieldFileImporter extends AbstractImporter {

  private RandomAccessFile raf;

  private int lineLen;

  private int lineCount;

  private int[] fieldWidths;

  private int[] fieldOffsets;

  private int eolLength;

  /**
   * constructor.
   * 
   * @param columnNames
   *        columns.
   * @param indices
   *        indices.
   * @param fieldWidths
   *        widths.
   * @param fieldOffsets
   *        offsets.
   * @param eolLength
   *        length of newline.
   */
  public FixedFieldFileImporter(String[] columnNames, int[] indices,
      int[] fieldWidths, int[] fieldOffsets, int eolLength) {
    super();
    super.setColumns(columnNames, indices);
    this.fieldWidths = fieldWidths;
    this.fieldOffsets = fieldOffsets;
    this.eolLength = eolLength;
  }

  /**
   * constructor.
   * 
   * @param columnNames
   *        names.
   * @param indices
   *        indices.
   * @param fieldWidths
   *        widths.
   * @param eolLength
   *        length of newline.
   */
  public FixedFieldFileImporter(String[] columnNames, int[] indices,
      int[] fieldWidths, int eolLength) {
    super();
    super.setColumns(columnNames, indices);
    this.fieldWidths = fieldWidths;
    fieldOffsets = new int[fieldWidths.length];
    this.eolLength = eolLength;

    int offset = 0;
    fieldOffsets[0] = 0;
    for (int i = 1; i < fieldWidths.length; i++) {
      fieldOffsets[i] = offset += fieldWidths[i - 1];
    }
  }

  /**
   * constructor.
   * 
   * @param columnNames
   *        columns.
   * @param indices
   *        indices.
   * @param fieldWidths
   *        widths.
   * @param filePath
   *        file.
   * @param eolLength
   *        length of newline.
   */
  public FixedFieldFileImporter(String[] columnNames, int[] indices,
      int[] fieldWidths, String filePath, int eolLength) {
    this(columnNames, indices, fieldWidths, eolLength);
    this.setFilePath(filePath);
  }

  /**
   * constructor.
   * 
   * @param columnNames
   *        columns.
   * @param indices
   *        indices.
   * @param fieldWidths
   *        widths.
   * @param file
   *        file.
   * @param eolLength
   *        length of newline.
   */
  public FixedFieldFileImporter(String[] columnNames, int[] indices,
      int[] fieldWidths, File file, int eolLength) {
    this(columnNames, indices, fieldWidths, eolLength);
    this.setFile(file);
  }

  /**
   * constructor.
   * 
   * @param columnNames
   *        columns.
   * @param indices
   *        indices.
   * @param fieldWidths
   *        widths.
   * @param fieldOffsets
   *        offsets.
   * @param filePath
   *        file.
   * @param eolLength
   *        length of newline.
   */
  public FixedFieldFileImporter(String[] columnNames, int[] indices,
      int[] fieldWidths, int[] fieldOffsets, String filePath, int eolLength) {
    this(columnNames, indices, fieldWidths, fieldOffsets, eolLength);
    this.setFile(new File(filePath));
  }

  /**
   * constructor.
   * 
   * @param columnNames
   *        columns.
   * @param indices
   *        indices.
   * @param fieldWidths
   *        widths.
   * @param fieldOffsets
   *        offsets.
   * @param file
   *        file.
   * @param eolLength
   *        length of newline.
   */
  public FixedFieldFileImporter(String[] columnNames, int[] indices,
      int[] fieldWidths, int[] fieldOffsets, File file, int eolLength) {
    this(columnNames, indices, fieldWidths, fieldOffsets, eolLength);
    this.setFile(file);
  }

  @Override
  public final boolean openFile() throws Exception {

    try {
      raf = new RandomAccessFile(file, "r");

      BufferedReader fileReader = new BufferedReader(new FileReader(file));

      String line = fileReader.readLine();
      lineLen = line.length();
      lineCount = 1;
      while (fileReader.readLine() != null) {
        lineCount++;
      }
      /*
       * System.out.println("Line Length : " + lineLen);
       * System.out.println("Line Count : " + lineCount);
       */
      fileReader.close();

      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public final int getRowCount() {
    return lineCount;
  }

  @Override
  public final String getValue(int row, int col) {
    try {

      raf.seek((lineLen + eolLength) * row + fieldOffsets[col]);

      byte[] chars = new byte[fieldWidths[col]];
      raf.readFully(chars);

      return new String(chars).trim();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public final String getValue(int row, String colName) {
    int col = columnIndexMap.get(colName);
    return getValue(row, col);
  }

  @Override
  public final Row getRow(int index) {
    try {
      raf.seek((lineLen + eolLength) * index);

      /*
       * System.out.println("position : " + raf.getFilePointer());
       */
      String line = raf.readLine();

      /*
       * System.out.println("line : " + line); System.out.println("position : "
       * + raf.getFilePointer());
       */

      return new RowImpl(line);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public final Iterator<Row> getRowIterator() {
    return new RowIterator();
  }

  /**
   * RowIterator implementation which tracks row position.
   * 
   * @author <a href=mailto:gtassone@cougaarsoftware.com>gtassone</a>
   * @version $Revision$
   */
  private final class RowIterator implements Iterator<Row> {

    private int rowCount;

    public RowIterator() {
      rowCount = 0;
    }

    @Override
    public boolean hasNext() {
      return rowCount < lineCount;
    }

    @Override
    public Row next() {
      return getRow(rowCount++);
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }

  }

  /**
   * Row implementation which wraps row String.
   * 
   * @author <a href=mailto:gtassone@cougaarsoftware.com>gtassone</a>
   * @version $Revision$
   */
  private class RowImpl implements Row {

    private String row;

    public RowImpl(String row) {
      this.row = row;
    }

    @Override
    public boolean isValid() {
      return row != null && row.length() == lineLen && rv.isValid(this);
    }

    @Override
    public int getColCount() {
      return fieldWidths.length;
    }

    @Override
    public String getValue(int col) {

      return row.substring(fieldOffsets[col],
          fieldOffsets[col] + fieldWidths[col]).trim();
    }

    @Override
    public String getValue(String colName) {

      return getValue(columnIndexMap.get(colName));
    }

  }

}
