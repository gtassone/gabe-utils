package com.gmail.gtassone.util.attribute;

import java.io.Serializable;

public class DoubleAttribute<C> extends AbstractAttribute<Double, C> implements
    Serializable {

  private static final long serialVersionUID = 1;

  public DoubleAttribute(String name, Class<C> ownerClass) {
    super(name, ownerClass);
  }

  public Class<Double> getClassType() {
    return Double.class;
  }

}
