package com.gmail.gtassone.util.attribute.filter.predicate;

import java.util.List;

import com.gmail.gtassone.util.attribute.constraint.AcceptedListConstraint;
import com.gmail.gtassone.util.attribute.constraint.AttributeConstraint;

public class AcceptedListConstraintPredicate<T> implements
    AttributeConstraintPredicate<T> {

  public boolean acceptValue(AcceptedListConstraint constraint, T value) {
    if (value == null) {
      return false;
    }
    List acceptedValues = constraint.getValues();
    boolean found = false;

    for (Object acceptedValue : acceptedValues) {

      if (value.equals(acceptedValue)) {
        found = true;
        break;
      }
    }
    return constraint.isNegated() ^ found;
  }

  public boolean acceptValue(AttributeConstraint constraint, T value) {
    if (constraint instanceof AcceptedListConstraint) {
      return acceptValue((AcceptedListConstraint) constraint, value);
    }
    return false;
  }

}
