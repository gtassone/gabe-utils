package com.gmail.gtassone.util.attribute.filter.predicate;

import com.gmail.gtassone.util.attribute.constraint.AttributeConstraint;
import com.gmail.gtassone.util.attribute.constraint.RejectedValueConstraint;

public class RejectedValueConstraintPredicate<T> implements
    AttributeConstraintPredicate<T> {

  public boolean acceptValue(AttributeConstraint constraint, T value) {
    if (constraint instanceof RejectedValueConstraint) {
      return acceptValue((RejectedValueConstraint) constraint, value);
    }
    return false;
  }

  public boolean acceptValue(RejectedValueConstraint constraint, T value) {

    if (value == null) {
      return false;
    }

    Object acceptedValue = constraint.getValue();
    boolean isEqual = false;
    if (acceptedValue.equals(value)) {
      isEqual = true;
    }
    return (!isEqual) ^ constraint.isNegated();
  }

}
