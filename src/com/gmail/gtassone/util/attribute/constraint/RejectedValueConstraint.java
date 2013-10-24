package com.gmail.gtassone.util.attribute.constraint;

import java.io.Serializable;

import com.gmail.gtassone.util.attribute.Attribute;

public class RejectedValueConstraint<T extends Serializable> extends
    ValueConstraint<T> implements Serializable {

  private static final long serialVersionUID = 1;

  public RejectedValueConstraint() {
    super();
    super.value = null;
  }

  public RejectedValueConstraint(T value) {
    super(value);
    // this.value = value;
  }

  public RejectedValueConstraint(Attribute attr) {
    super(attr);
  }

  public RejectedValueConstraint(Attribute attr, T value) {
    super(attr, value);
  }

}
