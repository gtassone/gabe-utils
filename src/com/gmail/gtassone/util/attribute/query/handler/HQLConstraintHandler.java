package com.gmail.gtassone.util.attribute.query.handler;

import com.gmail.gtassone.util.attribute.constraint.AttributeConstraint;
import com.gmail.gtassone.util.attribute.exception.ConstraintException;

public interface HQLConstraintHandler {

  public void applyConstraint(StringBuffer query, AttributeConstraint constraint)
      throws ConstraintException;

}
