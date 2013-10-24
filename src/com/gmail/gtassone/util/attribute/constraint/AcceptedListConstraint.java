package com.gmail.gtassone.util.attribute.constraint;

import java.io.Serializable;
import java.util.List;

import com.gmail.gtassone.util.attribute.Attribute;

public class AcceptedListConstraint<T extends Serializable> extends
    ListConstraint<T> implements Serializable {

  private static final long serialVersionUID = 1;

  public AcceptedListConstraint() {
    super();
  }

  public AcceptedListConstraint(List<T> values) {
    super(values);
  }

  public AcceptedListConstraint(Attribute attr) {
    super(attr);
  }

  public AcceptedListConstraint(Attribute attr, List<T> values) {
    super(attr, values);
  }
}
