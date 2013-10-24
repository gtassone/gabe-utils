package com.gmail.gtassone.util.data.file;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;

import com.gmail.gtassone.util.data.AbstractImporter.RowValidator;

/**
 * base class for file importers. helps manage RowValidator, columns, etc.
 * 
 * @author <a href=mailto:gtassone@gmail.com>gtassone</a>
 * @version $Revision$
 */
public abstract class AbstractImporter implements RecordFileImporter {

  /**
   * rv.
   */
  protected RowValidator rv;

  /**
   * path.
   */
  protected String filePath;

  /**
   * file.
   */
  protected File file;

  /**
   * first line.
   */
  protected Integer firstLineIndex;

  /**
   * last line.
   */
  protected Integer lastLineIndex;

  // you create/ reference columns by name
  /**
   * columns.
   */
  protected String[] columnNames;

  /**
   * indices.
   */
  protected int[] columnIndices;

  // holds rows as they are read ; we don't want large files if we're using this
  // at runtime, they'll take up memory
  protected Vector<String[]> rowList;

  // maps column name to corresponding integer index
  protected HashMap<String, Integer> columnIndexMap;

  /**
   * sets up column name => integer index map.
   */
  protected final void buildColumnIndexMap() {
    columnIndexMap = new HashMap<String, Integer>();
    if (columnNames != null) {

      for (int i = 0; i < columnNames.length; i++) {

        columnIndexMap.put(columnNames[i], (columnIndices == null ? i
            : columnIndices[i]));
      }
    }
  }

  @Override
  public final void setFirstRowIndex(int index) {
    firstLineIndex = index;
  }

  @Override
  public final void setLastRowIndex(int index) {
    lastLineIndex = index;
  }

  @Override
  public final void setColumns(String[] colNames, int[] indices) {
    this.columnNames = colNames;
    this.columnIndices = indices;
    buildColumnIndexMap();
  }

  @Override
  public final void setFile(File file) {
    this.file = file;
    this.setFilePath(file.getAbsolutePath());
  }

  @Override
  public final void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  @Override
  public final void setRowValidator(RowValidator v) {
    rv = v;
  }

}
