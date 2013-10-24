package com.gmail.gtassone.util.attribute.filter.predicate;

import com.gmail.gtassone.util.attribute.constraint.AcceptedValueConstraint;
import com.gmail.gtassone.util.attribute.constraint.AttributeConstraint;

public class AcceptedValueConstraintPredicate<T> implements
    AttributeConstraintPredicate<T> {

  public boolean acceptValue(AcceptedValueConstraint constraint, T value) {
    if (value == null) {
      return false;
    }
    Object acceptedValue = constraint.getValue();
    boolean isEqual = false;
    if (acceptedValue.equals(value)) {
      isEqual = true;
    }

    /*
     * System.err.println("AcceptedValueConstraintPredicate: " +
     * "\naccepted value is : " + acceptedValue + "\nactual value is : " + value
     * + "\nequality is : " + acceptedValue.equals(value));
     */
    return isEqual ^ constraint.isNegated();
  }

  public boolean acceptValue(AttributeConstraint constraint, T value) {
    if (constraint instanceof AcceptedValueConstraint) {
      return acceptValue((AcceptedValueConstraint) constraint, value);
    }
    return false;
  }
}
