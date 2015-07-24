package com.gmail.gtassone.util.data.db;

import java.io.File;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.gmail.gtassone.util.data.AbstractImporter;
import com.gmail.gtassone.util.data.file_old.FieldFilterRowValidator;
import com.gmail.gtassone.util.data.file_old.FixedFieldFileImporter;
import com.gmail.gtassone.util.data.file_old.RecordFileImporter.Row;

/**
 * reads a properties file to access a Fixed field data file, and import it into
 * a database table.
 * 
 * @author <a href=mailto:gtassone@gmail.com>gtassone</a>
 * @version $Revision$
 */
public class DBImporter extends AbstractImporter {

  /**
   * property suffix for field to validate rows by.
   */
  public static final String FILTER_FIELD_KEY = ".filter.field";

  /**
   * property suffix for filter file.
   */
  public static final String FILTER_FILE_KEY = ".filter.file";

  /**
   * property suffix for RowValidator class to use.
   */
  public static final String ROW_VALIDATOR_KEY = ".row.validator";

  /**
   * property suffix for comma separated list of fields in a row.
   */
  public static final String FIELDS_KEY = ".fields";

  /**
   * property suffix for the width of a field.
   */
  public static final String WIDTH_KEY = ".width";

  /**
   * property suffix for the offset of a field.
   */
  public static final String OFFSET_KEY = ".offset";

  /**
   * property suffix for the table to use in the database.
   */
  public static final String TABLE_KEY = ".table";

  /**
   * property suffix for the primary key.
   */
  public static final String PKEY_KEY = ".pkey";

  /**
   * property suffix for the database type for the field.
   */
  public static final String COL_TYPE_KEY = ".type";

  /**
   * property suffix for the data file.
   */
  public static final String FILE_KEY = ".file";

  /**
   * property suffix for the batch size.
   */
  private static final String BATCH_SIZE_KEY = ".batch.size";

  private static final int DEFAULT_BATCH_SIZE = 1000;

  private Logger log;

  private String prefix;

  private String tableName;

  private String pkey;

  private String[] fields;

  private int batchSize;

  private int[] columnIndices;

  private int[] fieldWidths;

  private int[] fieldOffsets;

  private String[] fieldWidthKeys;

  private String[] fieldOffsetKeys;

  private String[] columnTypeKeys;

  private String[] columnTypes;

  private String driverClass;

  private String connString;

  private String user;

  private String password;

  private Connection conn;

  /**
   * constructor.
   * 
   * @param prefix
   *        the prefix for all properties for this importer.
   * @param props
   *        the properties.
   */
  public DBImporter(String prefix, Properties props) {
    super(props);
    this.prefix = prefix;
  }

  /**
   * allows external manager application to provide a database connection.
   * 
   * @param connection
   *        the db connection.
   */
  public final void setConnection(Connection connection) {
    this.conn = connection;
  }

  /**
   * allows external manager application to provide a batch size.
   * 
   * @param batch
   *        batch size.
   */
  public final void setBatchSize(int batch) {
    batchSize = batch;
  }

  /**
   * reads the file records and imports them into the database table.
   */
  public final void execute() {
    try {

      StringBuffer preparedTableBuf = new StringBuffer();
      preparedTableBuf.append("create table if not exists ");
      preparedTableBuf.append(tableName + " (");

      boolean isCompoundKey = false;
      boolean isAutoKey = "generated".equals(pkey);

      String[] pkeys = pkey.split(",");
      StringBuffer compoundKey = new StringBuffer();
      if (pkeys.length > 1) {
        isCompoundKey = true;
        compoundKey.append(", PRIMARY KEY (");
        for (int i = 0; i < pkeys.length; i++) {
          compoundKey.append(pkeys[i] + (i == pkeys.length - 1 ? ")" : ", "));
        }
      }

      if (isAutoKey) {
        preparedTableBuf
            .append("id integer NOT NULL PRIMARY KEY AUTO_INCREMENT, ");
      }
      for (int i = 0; i < fields.length; i++) {
        preparedTableBuf.append(fields[i]
            + " "
            + columnTypes[i]
            + (pkey.equals(fields[i]) ? " NOT NULL UNIQUE PRIMARY KEY" : "")
            + (i == fields.length - 1 ? (isCompoundKey ? compoundKey.toString()
                : "") + ")" : ", "));
      }

      PreparedStatement preparedTableCreate = conn
          .prepareStatement(preparedTableBuf.toString());

      log.info("Prepared Table Statement : " + preparedTableBuf.toString());
      preparedTableCreate.executeUpdate();
      conn.commit();

    } catch (Exception e) {
      log.error(e);
    }

    try {

      StringBuffer preparedInsertBuf = new StringBuffer();
      preparedInsertBuf.append("insert into ");
      preparedInsertBuf.append(tableName + " (");

      for (int i = 0; i < fields.length; i++) {
        preparedInsertBuf.append(fields[i]
            + (i == fields.length - 1 ? ")" : ", "));
      }

      preparedInsertBuf.append(" values(");

      for (int i = 0; i < fields.length; i++) {
        preparedInsertBuf.append("?" + (i == fields.length - 1 ? ")" : ", "));
      }

      PreparedStatement insertItemRecord = conn
          .prepareStatement(preparedInsertBuf.toString());

      log.info("Rows : " + fileImporter.getRowCount());
      log.info("Prepared Insert Statement : " + preparedInsertBuf.toString());

      Iterator<Row> riter = fileImporter.getRowIterator();

      int rows = 0;

      int[] result = null;

      while (riter.hasNext()) {
        Row row = riter.next();

        if (!row.isValid()) {
          continue;
        } else {
          rows++;
        }

        try {
          for (int i = 0; i < fields.length; i++) {
            if (columnTypes[i].contains("char")) {
              insertItemRecord.setString(i + 1, row.getValue(fields[i]));
            } else if ("integer".equals(columnTypes[i])) {
              try {
                Integer value = Integer.parseInt(row.getValue(fields[i]));
                insertItemRecord.setInt(i + 1, value);
              } catch (Exception e) {
                insertItemRecord.setInt(i + 1, 0);
              }
            } else {
              log.error("unsupported column type");
            }
          }

          insertItemRecord.addBatch();

          if (rows % batchSize == 0) {

            try {
              result = insertItemRecord.executeBatch();

              conn.commit();
              log.info("batch update : " + result.length);
            } catch (BatchUpdateException e) {
              log.error("Batch Update failed", e);
              log.error("result size: "
                  + (result == null ? "null" : result.length));

            }

          }
        } catch (Exception e) {
          log.error("couldn't process row properly", e);
        }
      }
      // the last set of updates will probably not mod to 0

      try {
        result = insertItemRecord.executeBatch();
        conn.commit();
        log.info("batch update : " + result.length);
      } catch (BatchUpdateException e) {
        log.error("Batch Update failed", e);
        log.error("");
      }

    } catch (Exception e) {
      log.error(e);
      e.printStackTrace();
    }
  }

  /**
   * initializes the importer.
   * 
   * @return true if everything went smoothly.
   */
  public final boolean init() {
    getLogger();
    if (loadProperties()) {
      if (loadConn()) {
        return openFile();
      }
    }
    return false;
  }

  private boolean loadConn() {
    return conn != null;
  }

  /**
   * accesses the file.
   * 
   * @return true if the file is opened successfully.
   */
  public final boolean openFile() {
    try {
      fileImporter = new FixedFieldFileImporter(fields, columnIndices,
          fieldWidths, fieldOffsets, dataFile, 2);

      if (validator != null) {
        fileImporter.setRowValidator(validator);
      } else {
        fileImporter.setRowValidator(new RowValidator() {

          @Override
          public boolean isValid(Row row) {
            return row != null;
          }
        });
      }

      return fileImporter.openFile();

    } catch (Exception e) {
      log.error(e);
    }
    return false;
  }

  private void getLogger() {
    log = Logger.getLogger(this.getClass().getName() + "." + getPrefix());
  }

  @Override
  public final String getPrefix() {
    return prefix;
  }

  /**
   * goal : to load the column names, the table name, the user/ pass, conn
   * string, what else?
   * 
   * @return true on success.
   */
  @Override
  public final boolean loadProperties() {
    if (props == null) {
      return false;
    }

    if (!loadFile()) {
      return false;
    }

    if (!loadFields()) {
      return false;
    }

    if (!loadFieldWidths()) {
      return false;
    }

    if (!loadTableName()) {
      return false;
    }

    if (!loadPkey()) {
      return false;
    }

    loadFieldOffsets();

    loadBatchSize();

    loadColumnTypes();

    loadRowValidator();
    // load column names. where they follow a given pattern.
    // load table name.

    return true;
  }

  private void loadRowValidator() {

    String rvClassName = props.getProperty(getPrefix() + ROW_VALIDATOR_KEY);
    String filterField = props.getProperty(getPrefix() + FILTER_FIELD_KEY);
    String filterFilePath = props.getProperty(getPrefix() + FILTER_FILE_KEY);

    validator = null;

    if (rvClassName != null) {
      try {
        Class<RowValidator> rvClass = (Class<RowValidator>) Class
            .forName(rvClassName);
        validator = rvClass.newInstance();
      } catch (Exception e) {
        log.error(e);
      }
    }

    if (filterField != null) {
      try {
        File filterFile = new File(filterFilePath);
        validator = new FieldFilterRowValidator(filterField, filterFile);
      } catch (Exception e) {
        log.error(e);
      }
    }

    if (validator == null) {
      validator = new RowValidator() {

        @Override
        public boolean isValid(Row row) {
          return row != null;
        }

      };
    }

  }

  private void loadBatchSize() {

    if (batchSize > 0) {
      return;
    }
    try {
      batchSize = Integer.parseInt(props.getProperty(getPrefix()
          + BATCH_SIZE_KEY));
    } catch (Exception e) {
      batchSize = DEFAULT_BATCH_SIZE;
    }
  }

  private boolean loadFile() {
    String filePath = props.getProperty(getPrefix() + FILE_KEY);
    if (filePath == null) {
      return false;
    }
    setDataFilePath(filePath);
    setDataFile(new File(filePath));
    return true;
  }

  private void loadColumnTypes() {
    columnTypeKeys = new String[fields.length];
    for (int i = 0; i < fields.length; i++) {
      columnTypeKeys[i] = getPrefix() + "." + fields[i] + COL_TYPE_KEY;
    }
    columnTypes = new String[fields.length];
    for (int i = 0; i < fields.length; i++) {
      String columnType = props.getProperty(columnTypeKeys[i]);
      if (columnType == null) {
        columnType = "char(" + fieldWidths[i] + ")";
      }
      columnTypes[i] = columnType;
    }
  }

  private boolean loadPkey() {
    pkey = props.getProperty(getPrefix() + PKEY_KEY);
    return pkey != null;
  }

  private boolean loadTableName() {
    tableName = props.getProperty(getPrefix() + TABLE_KEY);
    return tableName != null;
  }

  private void loadFieldOffsets() {
    fieldOffsetKeys = new String[fields.length];
    for (int i = 0; i < fields.length; i++) {
      fieldOffsetKeys[i] = getPrefix() + "." + fields[i] + OFFSET_KEY;
    }
    fieldOffsets = loadIntegerProperties(props, fieldOffsetKeys);
    if (fieldOffsets == null) {
      fieldOffsets = new int[fields.length];
      int offset = 0;
      fieldOffsets[0] = 0;
      for (int i = 1; i < fields.length; i++) {
        fieldOffsets[i] = offset += fieldWidths[i - 1];
      }
    }
  }

  private boolean loadFieldWidths() {
    fieldWidthKeys = new String[fields.length];
    for (int i = 0; i < fields.length; i++) {
      fieldWidthKeys[i] = getPrefix() + "." + fields[i] + WIDTH_KEY;
    }
    fieldWidths = loadIntegerProperties(props, fieldWidthKeys);
    return fieldWidths != null;
  }

  private boolean loadFields() {
    String fieldsStr = props.getProperty(getPrefix() + FIELDS_KEY);
    if (fieldsStr == null) {
      return false;
    }
    fields = fieldsStr.split(",");
    columnIndices = new int[fields.length];
    for (int i = 0; i < fields.length; i++) {
      columnIndices[i] = i;
    }
    return true;
  }

}
