package com.gmail.gtassone.util.attribute.query.handler;

import com.gmail.gtassone.util.attribute.constraint.AcceptedValueConstraint;
import com.gmail.gtassone.util.attribute.constraint.AttributeConstraint;
import com.gmail.gtassone.util.attribute.exception.ConstraintException;
import com.gmail.gtassone.util.attribute.exception.UnsupportedConstraintException;
import com.gmail.gtassone.util.attribute.query.handler.column.ColumnProvider;

/**
 * FIXME it uses contraint.getValue().toString() which assumes that the
 * toString() method matches the value conversion method that Hibernate is
 * using. So for instance, for a UID attribute, so long as UID.toString() is the
 * method Hibernate is using to persist the UID, then we're good. But if
 * Hibernate uses a different method, we need to match that in some way for
 * instance we could use this as the default handler and if the attribute uses a
 * different persistence method then there could be a lookup map for custom
 * attributes
 * 
 * @author GTassone
 * @param <T>
 */
public class AcceptedValueHandler extends AbstractConstraintHandler {

  public AcceptedValueHandler() {
    super();
  }

  public AcceptedValueHandler(ColumnProvider provider) {
    super(provider);
  }

  public void applyConstraint(StringBuffer query, AttributeConstraint constraint)
      throws ConstraintException {

    if (!(constraint instanceof AcceptedValueConstraint)) {
      throw new UnsupportedConstraintException();
    }
    AcceptedValueConstraint avConstraint = (AcceptedValueConstraint) constraint;

    // the goal here is to amend the query with an acceptable value

    query.append((avConstraint.isNegated() ? "NOT " : "")
        + getColumnName(avConstraint.getAttribute()) + "='"
        + avConstraint.getValue().toString() + "'");

  }

}
