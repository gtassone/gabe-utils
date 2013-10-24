package com.gmail.gtassone.util.attribute.filter.predicate;

import java.util.List;

import com.gmail.gtassone.util.attribute.constraint.AttributeConstraint;
import com.gmail.gtassone.util.attribute.constraint.RejectedListConstraint;

public class RejectedListConstraintPredicate<T> implements
    AttributeConstraintPredicate<T> {

  public boolean acceptValue(AttributeConstraint constraint, T value) {
    if (constraint instanceof RejectedListConstraint) {
      return acceptValue((RejectedListConstraint) constraint, value);
    }
    return false;
  }

  public boolean acceptValue(RejectedListConstraint constraint, T value) {

    if (value == null) {
      return false;
    }

    List rejectedValues = constraint.getValues();
    boolean rejected = false;
    for (Object rejectedValue : rejectedValues) {
      if (value.equals(rejected)) {
        rejected = true;
        break;
      }
    }
    return (!rejected) ^ constraint.isNegated();
  }

}
