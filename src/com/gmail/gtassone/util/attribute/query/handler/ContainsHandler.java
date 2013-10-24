package com.gmail.gtassone.util.attribute.query.handler;

import com.gmail.gtassone.util.attribute.constraint.AttributeConstraint;
import com.gmail.gtassone.util.attribute.constraint.ContainsConstraint;
import com.gmail.gtassone.util.attribute.constraint.ValueConstraint;
import com.gmail.gtassone.util.attribute.exception.ConstraintException;
import com.gmail.gtassone.util.attribute.exception.UnsupportedConstraintException;
import com.gmail.gtassone.util.attribute.query.handler.column.ColumnProvider;

public class ContainsHandler extends AbstractConstraintHandler {

  public ContainsHandler() {
    super();
  }

  public ContainsHandler(ColumnProvider provider) {
    super(provider);
  }

  public void applyConstraint(StringBuffer query, AttributeConstraint constraint)
      throws ConstraintException {

    if (!(constraint instanceof ContainsConstraint)) {
      throw new UnsupportedConstraintException();
    }

    query.append((constraint.isNegated() ? "NOT " : "")
        + getColumnName(constraint.getAttribute()) + " like '%"
        + ((ValueConstraint<String>) constraint).getValue() + "%'");

  }

}
