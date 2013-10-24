package com.gmail.gtassone.util.attribute.filter.predicate;

import com.gmail.gtassone.util.attribute.constraint.AttributeConstraint;
import com.gmail.gtassone.util.attribute.constraint.StartsWithConstraint;

public class StartsWithConstraintPredicate implements
    AttributeConstraintPredicate<String> {

  public boolean acceptValue(AttributeConstraint constraint, String value) {
    if (constraint instanceof StartsWithConstraint) {
      return acceptValue((StartsWithConstraint) constraint, value);
    }
    return false;
  }

  public boolean acceptValue(StartsWithConstraint constraint, String value) {

    if (value == null) {
      return false;
    }

    String startString = constraint.getValue();

    boolean startsWith = false;
    if (value != null) {
      if (value.startsWith(startString)) {
        startsWith = true;
      }
    }

    return startsWith ^ constraint.isNegated();
  }

}
