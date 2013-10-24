package com.gmail.gtassone.util.attribute.query.handler;

import com.gmail.gtassone.util.attribute.constraint.AttributeConstraint;
import com.gmail.gtassone.util.attribute.constraint.RejectedValueConstraint;
import com.gmail.gtassone.util.attribute.constraint.ValueConstraint;
import com.gmail.gtassone.util.attribute.exception.ConstraintException;
import com.gmail.gtassone.util.attribute.exception.UnsupportedConstraintException;
import com.gmail.gtassone.util.attribute.query.handler.column.ColumnProvider;

public class RejectedValueHandler extends AbstractConstraintHandler {

  public RejectedValueHandler() {
    super();
  }

  public RejectedValueHandler(ColumnProvider provider) {
    super(provider);
  }

  public void applyConstraint(StringBuffer query, AttributeConstraint constraint)
      throws ConstraintException {

    if (!(constraint instanceof RejectedValueConstraint)) {
      throw new UnsupportedConstraintException();
    }
    String value = ((ValueConstraint<String>) constraint).getValue();

    if (value == null) {

      query.append(getColumnName(constraint.getAttribute()) + " is "
          + (!constraint.isNegated() ? "NOT " : "") + " null");

    } else {

      query.append((!constraint.isNegated() ? "NOT " : "")
          + getColumnName(constraint.getAttribute()) + "='" + value + "'");

    }
  }

}
