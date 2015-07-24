package com.gmail.gtassone.util.data.file_old;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;

import org.apache.log4j.Logger;

/**
 * inclusion filter that reads a filter file for a list of accepted values.
 * 
 * @author <a href=mailto:gtassone@gmail.com>gtassone</a>
 * @version $Revision$
 */
public class InclusionFilterImpl implements InclusionFilter {

  private HashSet<String> inclusionSet;

  private boolean excludeMode;

  private Logger log;

  private Logger error;

  /**
   * constructor.
   * 
   * @param includeListFile
   *        the filter file.
   * @param exclude
   *        flag to reverse behavior to exclude.
   */
  public InclusionFilterImpl(File includeListFile, boolean exclude) {

    log = Logger.getLogger(this.getClass());
    error = Logger.getLogger(this.getClass().getName() + ".ERROR");

    // populate the inclusion set
    inclusionSet = new HashSet<String>();

    try {

      BufferedReader fIn = new BufferedReader(new FileReader(includeListFile));

      String line;

      while ((line = fIn.readLine()) != null) {
        line = line.trim();
        if (line != null && line.length() > 0) {
          inclusionSet.add(line);
        }
      }

      fIn.close();

    } catch (Exception e) {
      error.error("failed while reading inclusion list", e);
    }
    excludeMode = exclude;
  }

  @Override
  public final boolean include(String value) {

    return inclusionSet.contains(value) ^ excludeMode;
  }

}
