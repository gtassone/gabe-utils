package com.gmail.gtassone.util.data.file;

import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import com.gmail.gtassone.util.data.file.RowValidator.RowValidationResult;


/**
 * This class may be extended
 * 
 * @author gabriel
 * 
 */
public abstract class Importer {

    private RowProcessor<?> rowProcessor;

    private RowValidator rowValidator;

    private PreValidator preValidator;

    protected InvalidRowHandler invalidRowHandler;

    public void setRowProcessor(RowProcessor<?> rp) {
        this.rowProcessor = rp;
    }

    public void setRowValidator(RowValidator rv) {
        this.rowValidator = rv;
    }

    public void setPreValidator(PreValidator pv) {
        this.preValidator = pv;
    }

    public void setInvalidRowHandler(InvalidRowHandler irh) {
        this.invalidRowHandler = irh;
    }

    /**
     * Import method which accepts a data source, allowing the Importer implementing class to be a Singleton. The
     * processing components can be singleton, or they could be injected scoped per request (?) It is important that
     * they be tied somehow to the workbook, either through request scope or per-workbook (non-singleton) importer.
     * 
     * @param dataSource
     * @throws Exception
     */
    public void importData(DataSourceWrapper<?> dataSource) throws Exception {
        dataSource.openDataSource();
        if (preValidator == null || preValidator.preValidate(dataSource)) {
            Iterator<Row> rowIter = dataSource.getRowIterator();
            while (rowIter.hasNext()) {
                Row row = rowIter.next();
                try {
                    RowValidationResult result = (rowValidator == null ? null : rowValidator.validate(row));
                    if (result == null || result.isValid()) {
                        rowProcessor.processRow(row);
                    }
                    else {
                        invalidRowHandler.handleInvalidRow(row, result);
                    }
                }
                catch (Exception e) {
                    if (invalidRowHandler == null) throw e;
                    invalidRowHandler.handleInvalidRow(row, e);
                }
            } // end while
            rowProcessor.postProcess();
        } // else the prevalidation failed
        else {}
    }
    
    protected boolean isRowBlank(Row row) {
        boolean isEntireRowBlank = true;
        for (int i = 0; i < row.getColCount(); i++) {
            if (!StringUtils.isBlank(row.getValue(i))) {
                isEntireRowBlank = false;
            }
        }
        return isEntireRowBlank;
    }
}
