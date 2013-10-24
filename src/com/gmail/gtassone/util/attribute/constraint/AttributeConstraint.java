package com.gmail.gtassone.util.attribute.constraint;

import java.io.Serializable;

import com.gmail.gtassone.util.attribute.Attribute;

public abstract class AttributeConstraint<T extends Serializable> implements
    Serializable {

  private static final long serialVersionUID = 1;

  boolean negate = false;

  // MODIFICATION
  // NOTE - Read how to use for proper use of Child classes of
  // AttributeConstraint

  // Adding a boolean member to identify if this constraint is the first
  // constraint being
  // applied or if it is a subsequent constraint( second, third, ...)
  // this is being done because if the constraint is the first constraint then
  // the hibernate query should be something like -
  // from <ClassName> where <AttributeName> = <AttributeValue>
  // without any 'and' before the <AttributeName>
  // where as for any subsequent Constraint added, the <AttributeName>
  // needs to be followed by and, example -
  // from <ClassName> where <AttributeName> = <AttributeValue> and
  // <AttributeName> = <AttributeValue>
  // Declaring the variable as static because this way it is easier to have just
  // one variable shared across all the attributeconstraints and thus monitored
  // they are
  // the first or any subsequent constraint.

  // HOW TO USE - Every time a new type of ConstraintHandler is created for
  // example -
  // AcceptedListConstraintHandler, EndsWithHandler etc. make sure that the
  // handler
  // checks if the value of firstConstraint is true using the function -
  // boolean ifFirstConstraint()
  // IF TRUE - do not append the query string with "and" before the actual query
  // and after the query is created and appended to the query string, call the
  // function
  // void setNotFirstConstraint() so that the subsequent calls to the function
  // boolean ifFirstConstraint() will return the correct value(false)
  /* static boolean firstConstraint = true; */

  Attribute attribute;

  AttributeConstraint() {
    attribute = null;
  }

  AttributeConstraint(Attribute attr) {
    attribute = attr;
  }

  public void negate() {
    negate = true;
  }

  public void unnegate() {
    negate = false;
  }

  public boolean isNegated() {
    return negate;
  }

  public Attribute getAttribute() {
    return attribute;
  }

  public void setAttribute(Attribute a) {
    attribute = a;
  }

  /*
   * Function to identify if this constraint is the
   */
  /*
   * public boolean ifFirstConstraint() { return this.firstConstraint; } public
   * void setNotFirstConstraint() { this.firstConstraint = false; }
   */
}
