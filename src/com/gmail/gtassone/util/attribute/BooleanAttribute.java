package com.gmail.gtassone.util.attribute;

import java.io.Serializable;

/**
 * Represents a Boolean type attribute.
 * 
 * @author gtassone
 * @param <C>
 *        The owner class of the attribute.
 */
public class BooleanAttribute<C> extends AbstractAttribute<Boolean, C>
    implements Serializable {

  private static final long serialVersionUID = 1;

  public BooleanAttribute(String name, Class<C> ownerClass) {
    super(name, ownerClass);
  }

  public Class<Boolean> getClassType() {
    return Boolean.class;
  }

}
