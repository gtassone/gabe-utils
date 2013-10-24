/**
 * Copyright 2001-2008, Cougaar Technologies, Inc. Originally authored under
 * agreement by Cougaar Software, Inc. with all Copyright and Rights
 * transferred. This file constitutes Proprietary Data. Cougaar Technologies,
 * Inc. reserves All Rights to this work product and any updates or derivatives.
 * This software is provided as part of the ActiveEdge product and use implies
 * acceptance of the ActiveEdge End-User License Agreement. Use of this software
 * or any derivative by any party outside of that License Agreement is strictly
 * prohibited. For a copy of the license, mail info@cougaarsoftware.com Cougaar
 * Software, Inc. is the exclusive reseller of Cougaar Technologies ActiveEdge
 * product through 2010 and is authorized as part of that agreement to re-brand
 * ActiveEdge as a Cougaar Software, Inc. product for purposes of bids,
 * marketing, sales and contracting. $LastChangedDate$ $LastChangedBy$
 * $Revision$
 */
package com.gmail.gtassone.util.data.file;

import java.io.File;

import com.gmail.gtassone.util.data.AbstractImporter.RowValidator;
import com.gmail.gtassone.util.data.file.RecordFileImporter.Row;

/**
 * {@link RowValidator} which checks a specified field in the Row against a set
 * of accepted values listed in a filter file.
 * 
 * @author <a href=mailto:gtassone@cougaarsoftware.com>gtassone</a>
 * @version $Revision$
 */
public class FieldFilterRowValidator implements RowValidator {

  private InclusionFilter ifilter;

  private String filteredFieldName;

  /**
   * @param field
   *        the field used to validate the row.
   * @param filterFile
   *        the file listing accepted values for the field.
   */
  public FieldFilterRowValidator(String field, File filterFile) {
    ifilter = new InclusionFilterImpl(filterFile, false);
    filteredFieldName = field;
  }

  @Override
  public final boolean isValid(Row row) {
    return row != null && ifilter.include(row.getValue(filteredFieldName));
  }

}
