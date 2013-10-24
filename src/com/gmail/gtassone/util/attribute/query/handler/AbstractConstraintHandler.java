package com.gmail.gtassone.util.attribute.query.handler;

import com.gmail.gtassone.util.attribute.Attribute;
import com.gmail.gtassone.util.attribute.constraint.AttributeConstraint;
import com.gmail.gtassone.util.attribute.exception.ConstraintException;
import com.gmail.gtassone.util.attribute.query.handler.column.ColumnProvider;
import com.gmail.gtassone.util.attribute.query.handler.column.DefaultColumnProvider;

// column name can be set when the Handler is created ; in this case it is
// specific to a particular Attribute

/**
 * 
 */
public abstract class AbstractConstraintHandler implements HQLConstraintHandler {

  ColumnProvider provider;

  public AbstractConstraintHandler() {
    provider = new DefaultColumnProvider();
  }

  public AbstractConstraintHandler(ColumnProvider provider) {
    this.provider = provider;
  }

  public void setColumnProvider(ColumnProvider provider) {
    this.provider = provider;
  }

  public String getColumnName(Attribute attribute) {
    return provider.getColumnName(attribute);
  }

  @Override
  public void applyConstraint(StringBuffer query, AttributeConstraint constraint)
      throws ConstraintException {

    throw new ConstraintException();
  }

}
