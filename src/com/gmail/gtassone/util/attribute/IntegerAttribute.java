package com.gmail.gtassone.util.attribute;

import java.io.Serializable;

public class IntegerAttribute<C> extends AbstractAttribute<Integer, C>
    implements Serializable {

  private static final long serialVersionUID = 1;

  public IntegerAttribute(String name, Class<C> ownerClass) {
    super(name, ownerClass);
  }

  public Class<Integer> getClassType() {
    return Integer.class;
  }

}
