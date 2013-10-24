package com.gmail.gtassone.util.attribute.constraint;

import java.io.Serializable;

import com.gmail.gtassone.util.attribute.Attribute;

public class ContainsConstraint extends ValueConstraint<String> implements
    Serializable {

  private static final long serialVersionUID = 1;

  public ContainsConstraint() {
    super();
  }

  public ContainsConstraint(String value) {
    super(value);
  }

  public ContainsConstraint(Attribute attr) {
    super(attr);
  }

  public ContainsConstraint(Attribute attr, String value) {
    super(attr, value);
  }

}
