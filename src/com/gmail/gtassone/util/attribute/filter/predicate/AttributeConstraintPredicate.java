package com.gmail.gtassone.util.attribute.filter.predicate;

import com.gmail.gtassone.util.attribute.constraint.AttributeConstraint;

public interface AttributeConstraintPredicate<T> {

  public boolean acceptValue(AttributeConstraint constraint, T value);
}
