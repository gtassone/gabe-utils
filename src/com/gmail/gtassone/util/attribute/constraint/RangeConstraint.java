package com.gmail.gtassone.util.attribute.constraint;

import java.io.Serializable;

import com.gmail.gtassone.util.attribute.Attribute;

public class RangeConstraint<T extends Serializable & Comparable> extends
    AttributeConstraint<T> implements Serializable {

  private static final long serialVersionUID = 1;

  public static enum ComparisonOperator {

    GREATER_THAN(">"),
    LESS_THAN("<"),
    GREATER_OR_EQUAL(">="),
    LESS_OR_EQUAL("<=");

    private String operatorString;

    private ComparisonOperator(String opString) {
      operatorString = opString;
    }

    public String getOperatorString() {
      return operatorString;
    }
  }

  T upperbound;

  T lowerbound;

  ComparisonOperator lowerOp;

  ComparisonOperator upperOp;

  public RangeConstraint() {
    super();
  }

  public RangeConstraint(T lower, T upper) {
    super();
    lowerbound = lower;
    upperbound = upper;
    lowerOp = ComparisonOperator.GREATER_OR_EQUAL;
    upperOp = ComparisonOperator.LESS_OR_EQUAL;
  }

  public RangeConstraint(T lower, T upper, ComparisonOperator lowerOp,
      ComparisonOperator upperOp) {
    super();
    lowerbound = lower;
    upperbound = upper;
    this.lowerOp = lowerOp;
    this.upperOp = upperOp;
  }

  public RangeConstraint(Attribute attr) {
    super(attr);
    lowerbound = null;
    upperbound = null;
    lowerOp = ComparisonOperator.GREATER_OR_EQUAL;
    upperOp = ComparisonOperator.LESS_OR_EQUAL;
  }

  public RangeConstraint(Attribute attr, T lower, T upper) {
    super(attr);
    lowerbound = lower;
    upperbound = upper;
    lowerOp = ComparisonOperator.GREATER_OR_EQUAL;
    upperOp = ComparisonOperator.LESS_OR_EQUAL;
  }

  public RangeConstraint(Attribute attr, T lower, T upper,
      ComparisonOperator lowerOp, ComparisonOperator upperOp) {
    super(attr);
    lowerbound = lower;
    upperbound = upper;
    this.lowerOp = lowerOp;
    this.upperOp = upperOp;
  }

  public void setUpperBound(T upperbound) {
    this.upperbound = upperbound;
  }

  public void setLowerBound(T lowerbound) {
    this.lowerbound = lowerbound;
  }

  public T getUpperBound() {
    return upperbound;
  }

  public T getLowerBound() {
    return lowerbound;
  }

  public void setLowerOperator(ComparisonOperator lowerOp) {
    this.lowerOp = lowerOp;
  }

  public void setUpperOperator(ComparisonOperator upperOp) {
    this.upperOp = upperOp;
  }

  public ComparisonOperator getLowerOperator() {
    return lowerOp;
  }

  public ComparisonOperator getUpperOperator() {
    return upperOp;
  }
}
