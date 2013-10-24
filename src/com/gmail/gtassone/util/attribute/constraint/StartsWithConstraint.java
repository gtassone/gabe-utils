package com.gmail.gtassone.util.attribute.constraint;

import java.io.Serializable;

import com.gmail.gtassone.util.attribute.Attribute;

public class StartsWithConstraint extends ValueConstraint<String> implements
    Serializable {

  private static final long serialVersionUID = 1;

  public StartsWithConstraint() {
    super();
  }

  public StartsWithConstraint(String value) {
    super(value);
  }

  public StartsWithConstraint(Attribute attr) {
    super(attr);
  }

  public StartsWithConstraint(Attribute attr, String value) {
    super(attr, value);
  }

}
