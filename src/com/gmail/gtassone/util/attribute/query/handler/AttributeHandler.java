package com.gmail.gtassone.util.attribute.query.handler;

import com.gmail.gtassone.util.attribute.constraint.AttributeConstraint;

public interface AttributeHandler {

  public HQLConstraintHandler getConstraintHandler(
      AttributeConstraint constraint);
}
