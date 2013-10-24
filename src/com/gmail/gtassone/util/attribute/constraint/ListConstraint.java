package com.gmail.gtassone.util.attribute.constraint;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import com.gmail.gtassone.util.attribute.Attribute;

public abstract class ListConstraint<T extends Serializable> extends
    AttributeConstraint<T> implements Serializable {

  private static final long serialVersionUID = 1;

  public Vector<T> values;

  public ListConstraint() {
    super();
    this.values = new Vector<T>();
  }

  public ListConstraint(List<T> values) {
    this.values = new Vector<T>(values);
  }

  public ListConstraint(Attribute attr) {
    super(attr);
    this.values = new Vector<T>();
  }

  public ListConstraint(Attribute attr, List<T> values) {
    super(attr);
    this.values = new Vector<T>(values);
  }

  public void addValue(T value) {
    values.add(value);
  }

  public void removeValue(T value) {
    values.remove(value);
  }

  public List<T> getValues() {
    return values;
  }

}
