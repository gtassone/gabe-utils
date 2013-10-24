package com.gmail.gtassone.util.attribute.filter.predicate;

import com.gmail.gtassone.util.attribute.constraint.AttributeConstraint;
import com.gmail.gtassone.util.attribute.constraint.ValueExistsConstraint;

public class ValueExistsPredicate implements AttributeConstraintPredicate {

  public boolean acceptValue(AttributeConstraint constraint, Object value) {
    if (constraint instanceof ValueExistsConstraint) {
      return acceptValue((ValueExistsConstraint) constraint, value);
    }
    return false;
  }

  public boolean acceptValue(ValueExistsConstraint constraint, Object value) {

    return (value != null) ^ constraint.isNegated();
  }

}
