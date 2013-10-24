package com.gmail.gtassone.util.attribute.constraint;

import java.io.Serializable;

import com.gmail.gtassone.util.attribute.Attribute;

public class MaxConstraint<T extends Serializable> extends
    AttributeConstraint<T> implements Serializable {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1;

  private String constraintOwnerClassName;

  public MaxConstraint() {
    super();
    this.constraintOwnerClassName = new String();
  }

  /*
   * public MaxConstraint(T value) { super(value); }
   */

  public MaxConstraint(Attribute attr) {
    super(attr);
    this.constraintOwnerClassName = new String();
  }

  /*
   * public MaxConstraint(Attribute attr, T value) { super(attr, value); }
   */

  public void setConstraintOwnerClassName(String owner) {
    this.constraintOwnerClassName = new String(owner);
  }

  public String getConstraintOwnerClassName() {
    return this.constraintOwnerClassName;
  }
}
