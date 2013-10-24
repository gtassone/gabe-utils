package com.gmail.gtassone.util.attribute.filter.predicate;

import com.gmail.gtassone.util.attribute.constraint.AttributeConstraint;
import com.gmail.gtassone.util.attribute.constraint.ValueIsNullConstraint;

public class ValueIsNullPredicate implements AttributeConstraintPredicate {

  public boolean acceptValue(AttributeConstraint constraint, Object value) {
    if (constraint instanceof ValueIsNullConstraint) {
      return acceptValue((ValueIsNullConstraint) constraint, value);
    }
    return false;
  }

  public boolean acceptValue(ValueIsNullConstraint constraint, Object value) {

    try {
      System.in.read();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return (value == null) ^ constraint.isNegated();
  }
}
