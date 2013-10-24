package com.gmail.gtassone.util.attribute.query.handler;

import java.util.List;

import com.gmail.gtassone.util.attribute.constraint.AcceptedListConstraint;
import com.gmail.gtassone.util.attribute.constraint.AttributeConstraint;
import com.gmail.gtassone.util.attribute.constraint.ListConstraint;
import com.gmail.gtassone.util.attribute.exception.ConstraintException;
import com.gmail.gtassone.util.attribute.exception.UnsupportedConstraintException;
import com.gmail.gtassone.util.attribute.query.handler.column.ColumnProvider;

public class AcceptedListHandler extends AbstractConstraintHandler {

  public AcceptedListHandler() {
    super();
  }

  public AcceptedListHandler(ColumnProvider provider) {
    super(provider);
  }

  public void applyConstraint(StringBuffer query, AttributeConstraint constraint)
      throws ConstraintException {

    if (!(constraint instanceof AcceptedListConstraint)) {
      throw new UnsupportedConstraintException();
    }

    AcceptedListConstraint alConstraint = (AcceptedListConstraint) constraint;
    StringBuffer inSet = new StringBuffer();
    List acceptedList = ((ListConstraint) constraint).getValues();

    inSet.append("(");
    for (int i = 0; i < acceptedList.size(); i++) {
      inSet.append("'" + acceptedList.get(i).toString() + "'"
          + (i < acceptedList.size() - 1 ? ", " : ")"));
    }

    query.append((alConstraint.isNegated() ? "NOT " : "")
        + getColumnName(alConstraint.getAttribute()) + " in "
        + inSet.toString());

  }

}
