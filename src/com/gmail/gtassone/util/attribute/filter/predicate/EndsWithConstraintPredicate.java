package com.gmail.gtassone.util.attribute.filter.predicate;

import com.gmail.gtassone.util.attribute.constraint.AttributeConstraint;
import com.gmail.gtassone.util.attribute.constraint.EndsWithConstraint;

public class EndsWithConstraintPredicate implements
    AttributeConstraintPredicate<String> {

  public boolean acceptValue(AttributeConstraint constraint, String value) {
    if (constraint instanceof EndsWithConstraint) {
      return acceptValue((EndsWithConstraint) constraint, value);
    }
    return false;
  }

  public boolean acceptValue(EndsWithConstraint constraint, String value) {
    if (value == null) {
      return false;
    }

    String endString = constraint.getValue();

    boolean endsWith = false;
    if (value != null) {
      if (value.endsWith(endString)) {
        endsWith = true;
      }
    }

    return endsWith ^ constraint.isNegated();
  }

}