package com.gmail.gtassone.util.attribute;

import java.io.Serializable;

public abstract class AbstractAttribute<T, C> implements Attribute<T, C>,
    Serializable {

  private static final long serialVersionUID = 1;

  private String attributeName;

  private Class<C> ownerClass;

  public AbstractAttribute(String name, Class<C> ownerClass) {
    attributeName = name;
    this.ownerClass = ownerClass;
  }

  public String getAttributeName() {
    return attributeName;
  }

  public Class<C> getOwnerClass() {
    return ownerClass;
  }

  public boolean equals(Object o) {
    if (AbstractAttribute.class.isAssignableFrom(o.getClass())) {
      AbstractAttribute attr = (AbstractAttribute) o;
      if (attr.getOwnerClass().equals(this.ownerClass)) {
        if (attr.getAttributeName().equals(this.attributeName)) {
          if (attr.getClassType().equals(this.getClassType())) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public int hashCode() {
    return (attributeName + ownerClass.getName()).hashCode();
  }

}
