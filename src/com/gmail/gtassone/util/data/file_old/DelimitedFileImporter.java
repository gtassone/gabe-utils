package com.gmail.gtassone.util.data.file_old;

import java.io.File;
import java.io.FileReader;
import java.util.Iterator;

/**
 * File importer for a delimited file, where the delimiter can be any character.
 * This class is not completed.
 * 
 * @author <a href=mailto:gtassone@gmail.com>gtassone</a>
 * @version $Revision$
 */
public class DelimitedFileImporter extends AbstractImporter {

  private FileReader reader;

  @Override
  public final boolean openFile() throws Exception {
    try {
      reader = new FileReader(file);
      reader.mark(0);

      File tmp = new File(file.getParent(), "tmp" + file.getName());
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return false;
  }

  @Override
  public final int getRowCount() {
    return 0;
  }

  @Override
  public final String getValue(int row, int col) {
    return null;
  }

  @Override
  public final String getValue(int row, String colName) {
    return null;
  }

  @Override
  public final Row getRow(int index) {
    return null;
  }

  @Override
  public final Iterator<Row> getRowIterator() {
    return null;
  }

}
