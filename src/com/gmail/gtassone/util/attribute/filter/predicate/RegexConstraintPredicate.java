package com.gmail.gtassone.util.attribute.filter.predicate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gmail.gtassone.util.attribute.constraint.AttributeConstraint;
import com.gmail.gtassone.util.attribute.constraint.RegexConstraint;

public class RegexConstraintPredicate<T> implements
    AttributeConstraintPredicate<T> {

  public boolean acceptValue(AttributeConstraint constraint, T value) {
    if (constraint instanceof RegexConstraint) {
      return acceptValue((RegexConstraint) constraint, value);
    }
    return false;
  }

  public boolean acceptValue(RegexConstraint constraint, T value) {

    if (value == null) {
      return false;
    }

    Pattern p = constraint.getValue();

    Matcher matcher = p.matcher(value.toString());

    boolean matches = matcher.matches();

    return matches ^ constraint.isNegated();

  }

}
