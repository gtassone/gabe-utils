package com.gmail.gtassone.util.attribute;

import java.io.Serializable;

public class StringAttribute<C> extends AbstractAttribute<String, C> implements
    Serializable {

  private static final long serialVersionUID = 1;

  public StringAttribute(String name, Class<C> ownerClass) {
    super(name, ownerClass);
  }

  public Class<String> getClassType() {
    return String.class;
  }

}
