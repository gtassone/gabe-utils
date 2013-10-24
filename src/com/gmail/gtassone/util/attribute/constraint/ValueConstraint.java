package com.gmail.gtassone.util.attribute.constraint;

import java.io.Serializable;

import com.gmail.gtassone.util.attribute.Attribute;

public abstract class ValueConstraint<T extends Serializable> extends
    AttributeConstraint<T> implements Serializable {

  private static final long serialVersionUID = 1;

  T value;

  public ValueConstraint() {
    super();
  }

  public ValueConstraint(T value) {
    super();
    this.value = value;
  }

  public ValueConstraint(Attribute attr) {
    super(attr);
  }

  public ValueConstraint(Attribute attr, T value) {
    super(attr);
    this.value = value;
  }

  public void setValue(T value) {
    this.value = value;
  }

  public T getValue() {
    return value;
  }

}
