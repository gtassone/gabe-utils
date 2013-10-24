package com.gmail.gtassone.util.attribute.constraint;

import java.io.Serializable;
import java.util.List;

import com.gmail.gtassone.util.attribute.Attribute;

public class RejectedListConstraint<T extends Serializable> extends
    ListConstraint<T> implements Serializable {

  private static final long serialVersionUID = 1;

  public RejectedListConstraint() {

  }

  public RejectedListConstraint(List<T> values) {
    super(values);
  }

  public RejectedListConstraint(Attribute attr) {
    super(attr);
  }

  public RejectedListConstraint(Attribute attr, List<T> values) {
    super(attr, values);
  }

}
