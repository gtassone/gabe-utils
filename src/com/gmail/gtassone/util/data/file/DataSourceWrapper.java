package com.gmail.gtassone.util.data.file;

import java.util.Iterator;


/**
 * interface for DataSource wrappers. 
 * @author gtassone
 *
 * @param <D>
 */
public interface DataSourceWrapper<D> {

    public void openDataSource() throws Exception;
    
    public void preValidate() throws Exception;
    
    public void closeDataSource() throws Exception;
    
    public void setDataSource(D dataSource);
    
    public D getDataSource();
    
    public Iterator<Row> getRowIterator();
    
}
