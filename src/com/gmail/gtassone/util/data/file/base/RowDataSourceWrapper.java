package com.gmail.gtassone.util.data.file.base;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.gmail.gtassone.util.data.file.ColumnKey;
import com.gmail.gtassone.util.data.file.DataSourceWrapper;


/**
 * Abstract base DataSourceWrapper implementation for row-structured data saources.
 * 
 * @author gtassone
 * 
 * @param <D>
 */
public abstract class RowDataSourceWrapper<D>
        implements DataSourceWrapper<D> {

    /**
     * first line.
     */
    protected Integer firstLineIndex;

    /**
     * last line.
     */
    protected Integer lastLineIndex;

    /**
     * columns.
     */
    protected String[] columnNames;

    /**
     * indices.
     */
    protected int[] columnIndices;

    /**
     * column keys.
     */
    protected ColumnKey<?>[] columnKeys;

    // maps column name to corresponding integer index
    protected HashMap<String, Integer> columnIndexMap;

    /**
     * sets up column name => integer index map.
     */
    protected final void buildColumnIndexMap() {
        columnIndexMap = new HashMap<String, Integer>();
        if (columnNames != null) {

            for (int i = 0; i < columnNames.length; i++) {

                columnIndexMap.put(columnNames[i], (columnIndices == null ? i : columnIndices[i]));
            }
        }
    }

    // @Override
    public final void setFirstRowIndex(int index) {
        firstLineIndex = index;
    }

    // @Override
    public final void setLastRowIndex(int index) {
        lastLineIndex = index;
    }

    public final void setColumns(String[] colNames, int[] indices) {
        this.columnNames = colNames;
        this.columnIndices = indices;
        buildColumnIndexMap();
    }

    public final void setColumnKeys(List<ColumnKey<?>> colKeys) {
        columnKeys = colKeys.toArray(columnKeys);
    }

    public final void setColumnKeys(ColumnKey<?>[] colKeys) {
        columnKeys = Arrays.copyOf(colKeys, colKeys.length);
    }

    // @Override
    public List<String> getColumnNames() {
        return Arrays.asList(columnNames);
    }
}
