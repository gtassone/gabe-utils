package com.gmail.gtassone.util.attribute.query.handler;

import com.gmail.gtassone.util.attribute.constraint.AttributeConstraint;
import com.gmail.gtassone.util.attribute.constraint.StartsWithConstraint;
import com.gmail.gtassone.util.attribute.constraint.ValueConstraint;
import com.gmail.gtassone.util.attribute.exception.ConstraintException;
import com.gmail.gtassone.util.attribute.exception.UnititializedConstraintException;
import com.gmail.gtassone.util.attribute.exception.UnsupportedConstraintException;
import com.gmail.gtassone.util.attribute.query.handler.column.ColumnProvider;

public class StartsWithHandler extends AbstractConstraintHandler {

  public StartsWithHandler() {
    super();
  }

  public StartsWithHandler(ColumnProvider provider) {
    super(provider);
  }

  public void applyConstraint(StringBuffer query, AttributeConstraint constraint)
      throws ConstraintException {

    if (!(constraint instanceof StartsWithConstraint)) {
      throw new UnsupportedConstraintException();
    }

    if (constraint.getAttribute() == null) {
      throw new UnititializedConstraintException();
    }

    query.append((constraint.isNegated() ? "NOT " : "")
        + getColumnName(constraint.getAttribute()) + " like '"
        + ((ValueConstraint<String>) constraint).getValue() + "%'");

  }

}
