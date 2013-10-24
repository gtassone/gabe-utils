package com.gmail.gtassone.util.attribute.constraint;

import java.io.Serializable;

import com.gmail.gtassone.util.attribute.Attribute;

public class AcceptedValueConstraint<T extends Serializable> extends
    ValueConstraint<T> implements Serializable {

  private static final long serialVersionUID = 1;

  public AcceptedValueConstraint() {
    super();
  }

  public AcceptedValueConstraint(T value) {
    super(value);
  }

  public AcceptedValueConstraint(Attribute attr) {
    super(attr);
  }

  public AcceptedValueConstraint(Attribute attr, T value) {
    super(attr, value);
  }

}
