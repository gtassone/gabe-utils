package com.gmail.gtassone.util.data.file;

/**
 * specifies a column by name, index and type.
 * @author gtassone
 *
 * @param <T>
 */
public interface ColumnKey<T> {

	public int getColumnIndex();

	public String getColumnName();

	public Class<T> getColumnType();
}
