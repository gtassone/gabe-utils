package com.gmail.gtassone.util.data.file_old;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * 
 * @author <a href=mailto:gtassone@gmail.com>gtassone</a>
 * @version $Revision$
 */
public class RecordFileImporterFactory {

  public static final String LOG_PROPS_PATH_KEY = "log.props.path";

  private static Logger log = null;

  private static final HashMap<String, Class<? extends RecordFileImporter>> fileExtensionImporterClassMap;
  static {

    fileExtensionImporterClassMap = new HashMap<String, Class<? extends RecordFileImporter>>();
    fileExtensionImporterClassMap.put(".xlsx", ExcelImporter.class);
    fileExtensionImporterClassMap.put(".csv", CSVImporter.class);
    fileExtensionImporterClassMap.put(".xls", ExcelImporter.class);
  }

  public static RecordFileImporter getRecordFileImporter(File file,
      String[] colNames, int[] colIndices) {

    if (log == null)
      initLoggers();
    Class<? extends RecordFileImporter> importerClass = fileExtensionImporterClassMap
        .get(file.getName().substring(file.getName().lastIndexOf('.')));

    if (importerClass == null) {
      log.error("no Importer class available for file type : "
          + file.getName().substring(file.getName().lastIndexOf('.')));
      return null;
    }

    try {
      RecordFileImporter importer = importerClass.newInstance();
      importer.setFile(file);
      importer.setColumns(colNames, colIndices);
      return importer;
    } catch (Exception e) {
      log.error(e);
      return null;
    }
  }

  public static RecordFileImporter getRecordFileImporter(String filepath,
      String[] colNames, int[] colIndices) {
    if (log == null)
      initLoggers();
    File file = new File(filepath);
    return getRecordFileImporter(file, colNames, colIndices);
  }

  /**
   * initialize the Loggers.
   */
  private static void initLoggers() {

    log = org.apache.log4j.Logger.getLogger(RecordFileImporterFactory.class
        .getName());

  }

}
