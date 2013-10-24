package com.gmail.gtassone.util.attribute.query.handler;

import java.util.List;

import com.gmail.gtassone.util.attribute.constraint.AttributeConstraint;
import com.gmail.gtassone.util.attribute.constraint.ListConstraint;
import com.gmail.gtassone.util.attribute.constraint.RejectedListConstraint;
import com.gmail.gtassone.util.attribute.exception.ConstraintException;
import com.gmail.gtassone.util.attribute.exception.UnsupportedConstraintException;
import com.gmail.gtassone.util.attribute.query.handler.column.ColumnProvider;

public class RejectedListHandler extends AbstractConstraintHandler {

  public RejectedListHandler() {
    super();
  }

  public RejectedListHandler(ColumnProvider provider) {
    super(provider);
  }

  public void applyConstraint(StringBuffer query, AttributeConstraint constraint)
      throws ConstraintException {

    if (!(constraint instanceof RejectedListConstraint)) {
      throw new UnsupportedConstraintException();
    }

    StringBuffer inSet = new StringBuffer();
    List<String> acceptedValues = ((ListConstraint<String>) constraint)
        .getValues();

    inSet.append("(");
    for (int i = 0; i < acceptedValues.size(); i++) {
      inSet.append("'" + acceptedValues.get(i).toString() + "'"
          + (i < acceptedValues.size() - 1 ? ", " : ")"));
    }

    query.append((!constraint.isNegated() ? "NOT " : "")
        + getColumnName(constraint.getAttribute()) + " in " + inSet.toString());

  }

}
