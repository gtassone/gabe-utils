package com.gmail.gtassone.util.attribute.constraint;

import java.io.Serializable;

import com.gmail.gtassone.util.attribute.Attribute;

public class ValueIsNullConstraint extends AttributeConstraint implements
    Serializable {

  private static final long serialVersionUID = 1;

  public ValueIsNullConstraint() {
    super();
  }

  public ValueIsNullConstraint(Attribute attr) {
    super(attr);
  }

}
