package com.gmail.gtassone.util.attribute;

import java.io.Serializable;

public class UserTypedAttribute<T, C> extends AbstractAttribute<T, C> implements
    Serializable {

  private static final long serialVersionUID = 1;

  private Class<T> attributeType;

  public UserTypedAttribute(String name, Class<C> ownerClass) {
    super(name, ownerClass);
  }

  public UserTypedAttribute(String name, Class<C> ownerClass,
      Class<T> attributeType) {
    super(name, ownerClass);
    setClassType(attributeType);
  }

  public Class<T> getClassType() {
    return attributeType;
  }

  public void setClassType(Class<T> clazz) {
    attributeType = clazz;
  }

}
