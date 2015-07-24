package com.gmail.gtassone.util.data.file;

public interface Row {

	public boolean isValid();

	public int getColCount();

	public String getValue(int col);

	public String getValue(String colName);

	public <T> T getValue(ColumnKey<T> key);
	
	public int getIndex();
}
