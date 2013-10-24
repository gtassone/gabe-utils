package com.gmail.gtassone.util.attribute.query.handler;

import com.gmail.gtassone.util.attribute.constraint.AttributeConstraint;
import com.gmail.gtassone.util.attribute.constraint.MaxConstraint;
import com.gmail.gtassone.util.attribute.exception.ConstraintException;
import com.gmail.gtassone.util.attribute.exception.UnsupportedConstraintException;
import com.gmail.gtassone.util.attribute.query.handler.column.ColumnProvider;

public class MaxValueHandler extends AbstractConstraintHandler implements
    AttributeHandler {

  public MaxValueHandler() {
    super();
  }

  public MaxValueHandler(ColumnProvider provider) {
    super(provider);
  }

  public void applyConstraint(StringBuffer query, AttributeConstraint constraint)
      throws ConstraintException {
    if (!(constraint instanceof MaxConstraint)) {
      throw new UnsupportedConstraintException();
    }

    query.append((getColumnName(constraint.getAttribute()))
        + (constraint.isNegated() ? " NOT " : " ") + "IN ( SELECT MAX("
        + (getColumnName(constraint.getAttribute())) + ") " + " from "
        + ((MaxConstraint) constraint).getConstraintOwnerClassName() + " ) ");

  }

  @Override
  public HQLConstraintHandler getConstraintHandler(
      AttributeConstraint constraint) {
    /*
     * if ( !(constraint instanceof MaxConstraint ) ) { throw new
     * UnsupportedConstraintException(); }
     */
    return this;
  }

}
