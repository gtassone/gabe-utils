package com.gmail.gtassone.util.data.file;

import com.gmail.gtassone.util.data.file.RowValidator.RowValidationResult;

public interface InvalidRowHandler {

	public void handleInvalidRow(Row row, RowValidationResult result);
	
	public void handleInvalidRow(Row row, Exception e);
}
