package com.gmail.gtassone.util.data.file;

/**
 * In the case of an invalid row, we want to be able to provide details. Instead
 * of returning boolean, we can return a result
 * 
 * @author gabriel
 *
 */
public interface RowValidator {

	public interface RowValidationResult {
		public boolean isValid();
		public String getMessage();
	}

	public RowValidationResult validate(Row row);
}
