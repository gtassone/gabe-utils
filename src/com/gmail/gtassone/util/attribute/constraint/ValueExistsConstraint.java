package com.gmail.gtassone.util.attribute.constraint;

import java.io.Serializable;

import com.gmail.gtassone.util.attribute.Attribute;

public class ValueExistsConstraint extends AttributeConstraint implements
    Serializable {

  private static final long serialVersionUID = 1;

  public ValueExistsConstraint() {
    super();
  }

  public ValueExistsConstraint(Attribute attr) {
    super(attr);
  }

}
