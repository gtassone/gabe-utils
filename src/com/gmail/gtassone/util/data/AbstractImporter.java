package com.gmail.gtassone.util.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.gmail.gtassone.util.data.file.RecordFileImporter;
import com.gmail.gtassone.util.data.file.RecordFileImporter.Row;

/**
 * this class was intended to capture the common behavior of different
 * importers, but due to time constraints or not enough similarities, it is
 * fairly useless. consider improving or removing.
 * 
 * @author <a href=mailto:gtassone@gmail.com>gtassone</a>
 * @version $Revision$
 */
public abstract class AbstractImporter {

  /**
   * hook interface for row validation.
   * 
   * @author <a href=mailto:gtassone@cougaarsoftware.com>gtassone</a>
   * @version $Revision$
   */
  public interface RowValidator {

    public boolean isValid(Row row);
  }
  
  private Logger log;

  /**
   * props.
   */
  protected Properties props;

  /**
   * rv.
   */
  protected RowValidator validator;

  /**
   * file importer.
   */
  protected RecordFileImporter fileImporter;

  /**
   * path.
   */
  protected String filepath;

  /**
   * file.
   */
  protected File dataFile;

  /**
   * The importer may be run standalone, or by logging properties key.
   */
  public static final String LOG_PROPERTIES_FILEPATH_KEY = "log.properties";

  /**
   * provides the properties prefix for this importer.
   * 
   * @return the prefix.
   */
  public abstract String getPrefix();

  /**
   * hook for accessing the required properties.
   * 
   * @return true on success.
   */
  public abstract boolean loadProperties();

  /**
   * hook for opening the file.
   * 
   * @return true on success.
   */
  public abstract boolean openFile();

  /**
   * @param properties
   *        properties.
   */
  public final void setProperties(Properties properties) {
    this.props = properties;
  }

  /**
   * @param filePath
   *        path.
   */
  public final void setDataFilePath(String filePath) {
    this.filepath = filePath;
  }

  /**
   * @param rv
   *        rv.
   */
  public final void setRowValidator(RowValidator rv) {
    this.validator = rv;
  }

  /**
   * @param file
   *        file.
   */
  public final void setDataFile(File file) {
    this.dataFile = file;
  }

  /**
   * constructor.
   * 
   * @param props
   *        props.
   */
  public AbstractImporter(Properties props) {
    setProperties(props);
  }

  /**
   * run as main.
   * 
   * @param args
   *        first is properties file, second is logging properties file
   */
  public static void main(String[] args) {

    String logPropsPath = null;
    if (args != null && args.length > 1) {
      logPropsPath = args[1];
    }
    if (logPropsPath == null) {
      logPropsPath = System.getenv(LOG_PROPERTIES_FILEPATH_KEY);
    }
    if (logPropsPath == null) {
      logPropsPath = System.getProperty(LOG_PROPERTIES_FILEPATH_KEY,
          "log.properties");
    }

    System.out.println("Log Properties Path (" + LOG_PROPERTIES_FILEPATH_KEY
        + "): " + logPropsPath);

    configureLogger(logPropsPath);

    String propsPath = null;

    if (args != null && args.length > 0) {
      propsPath = args[0];
    }
    if (propsPath == null) {
      propsPath = System.getenv("importer.properties");
    }
    if (propsPath == null) {
      propsPath = System.getProperty("importer.properties", "importer.properties");
    }

    Properties props = new Properties();
    try {
      props.load(new FileReader(propsPath));

    } catch (Exception e) {
      // log.error(e);
      e.printStackTrace();
    }
  }

  // *****************************************************************
  // util methods
  // *****************************************************************

  /**
   * initialize the Loggers.
   * 
   * @param logPropsPath
   *        path.
   */
  public static void configureLogger(String logPropsPath) {

    try {
      File logProps = new File(logPropsPath);
      Properties logProperties = new Properties();
      logProperties.load(new FileInputStream(logProps));
      PropertyConfigurator.configure(logProperties);
    } catch (Exception e) {
      e.printStackTrace();
      BasicConfigurator.configure();
    }
  }

  /**
   * helper method for loading a set of integers properties.
   * 
   * @param props
   *        the properties.
   * @param keys
   *        the keys for the integer props.
   * @return the integer values for the given keys.
   */
  public static int[] loadIntegerProperties(Properties props, String[] keys) {
    int[] values = new int[keys.length];
    for (int i = 0; i < keys.length; i++) {
      try {
        String value = props.getProperty(keys[i]);
        if (value == null) {
          return null;
        }
        values[i] = Integer.parseInt(value);
      } catch (Exception e) {
        // log.error(e);
        e.printStackTrace();
      }
    }
    return values;
  }

}
