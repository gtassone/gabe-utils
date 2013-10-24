package com.gmail.gtassone.util.attribute.filter.predicate;

import java.io.Serializable;

import com.gmail.gtassone.util.attribute.constraint.AttributeConstraint;
import com.gmail.gtassone.util.attribute.constraint.RangeConstraint;

public class RangeConstraintPredicate<T extends Serializable & Comparable>
    implements AttributeConstraintPredicate<T> {

  public boolean acceptValue(AttributeConstraint constraint, T value) {
    if (constraint instanceof RangeConstraint) {
      return acceptValue((RangeConstraint) constraint, value);
    }
    return false;
  }

  public boolean acceptValue(RangeConstraint constraint, T value) {

    if (value == null) {
      return false;
    }

    Comparable upper = (Comparable) constraint.getUpperBound();
    Comparable lower = (Comparable) constraint.getLowerBound();

    boolean inRange = false;

    if (upper != null) {
      if (upper.compareTo(value) == 0 || upper.compareTo(value) > 0) {
        if (lower == null) {
          inRange = true;
        } else {
          if (lower.compareTo(value) == 0 || lower.compareTo(value) < 0) {
            inRange = true;
          }
        }
      }
    } else if (lower != null) {
      if (lower.compareTo(value) == 0 || lower.compareTo(value) < 0) {
        inRange = true;
      }
    }

    return inRange ^ constraint.isNegated();
  }

}
