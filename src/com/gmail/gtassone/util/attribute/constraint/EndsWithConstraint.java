package com.gmail.gtassone.util.attribute.constraint;

import java.io.Serializable;

import com.gmail.gtassone.util.attribute.Attribute;

public class EndsWithConstraint extends ValueConstraint<String> implements
    Serializable {

  private static final long serialVersionUID = 1;

  public EndsWithConstraint() {
    super();
  }

  public EndsWithConstraint(String value) {
    super(value);
  }

  public EndsWithConstraint(Attribute attr) {
    super(attr);
  }

  public EndsWithConstraint(Attribute attr, String value) {
    super(attr, value);
  }

}
