package com.gmail.gtassone.util.attribute.filter.predicate;

import com.gmail.gtassone.util.attribute.constraint.AttributeConstraint;
import com.gmail.gtassone.util.attribute.constraint.ContainsConstraint;

public class ContainsConstraintPredicate implements
    AttributeConstraintPredicate<String> {

  public boolean acceptValue(AttributeConstraint constraint, String value) {
    if (constraint instanceof ContainsConstraint) {
      return acceptValue((ContainsConstraint) constraint, value);
    }
    return false;
  }

  public boolean acceptValue(ContainsConstraint constraint, String value) {
    if (value == null) {
      return false;
    }
    String containsString = constraint.getValue();

    boolean contains = false;
    if (value != null) {
      if (value.contains(containsString)) {
        contains = true;
      }
    }

    return contains ^ constraint.isNegated();
  }

}
