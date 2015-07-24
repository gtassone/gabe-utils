package com.gmail.gtassone.util.data.file;

/**
 * The row is processed if it is validated. However exceptions can still occur
 * in processRow and we should handle them at the Importer level.
 * 
 * @author gabriel
 *
 */
public interface RowProcessor<T> {

	public void processRow(Row row) throws Exception;
	
	public void postProcess() throws Exception;

	public T getResult();
}
