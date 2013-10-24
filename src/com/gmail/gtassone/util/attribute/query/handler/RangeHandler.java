package com.gmail.gtassone.util.attribute.query.handler;

import com.gmail.gtassone.util.attribute.constraint.AttributeConstraint;
import com.gmail.gtassone.util.attribute.constraint.RangeConstraint;
import com.gmail.gtassone.util.attribute.exception.ConstraintException;
import com.gmail.gtassone.util.attribute.exception.UnititializedConstraintException;
import com.gmail.gtassone.util.attribute.exception.UnsupportedConstraintException;
import com.gmail.gtassone.util.attribute.query.handler.column.ColumnProvider;

public class RangeHandler extends AbstractConstraintHandler {

  public RangeHandler() {
    super();
  }

  public RangeHandler(ColumnProvider provider) {
    super(provider);
  }

  public void applyConstraint(StringBuffer query, AttributeConstraint constraint)
      throws ConstraintException {

    if (!(constraint instanceof RangeConstraint)) {
      throw new UnsupportedConstraintException();
    }

    RangeConstraint rConstraint = (RangeConstraint) constraint;

    boolean useUpper = true;
    boolean useLower = true;
    if (rConstraint.getLowerBound() == null) {
      if (rConstraint.getUpperBound() == null
          || rConstraint.getUpperOperator() == null) {
        throw new UnititializedConstraintException();
      }
      useLower = false;
    }

    if (rConstraint.getUpperBound() == null) {
      if (rConstraint.getLowerBound() == null
          || rConstraint.getLowerOperator() == null) {
        throw new UnititializedConstraintException();
      }
      useUpper = false;
    }

    // case 1 : lower is set >, upper is set < //ok
    // case 2 : lower is set >, upper is not set //questionable?
    // case 3 : lower is set >, upper is set > //bad form
    // case 4 : lower is set <, upper is set < //bad form
    // case 5 : lower is set <, upper is not set //ok
    // case 6 : lower is set <, upper is set > // ok
    // case 7 : lower is not set, upper is set > //ok
    // case 8 : lower is not set, upper is set < //questionable?

    if (useUpper && useLower) {
      //

      if ((rConstraint.getLowerOperator().equals(
          RangeConstraint.ComparisonOperator.GREATER_OR_EQUAL) || rConstraint
          .getLowerOperator().equals(
              RangeConstraint.ComparisonOperator.GREATER_THAN))) {
        //
        if ((rConstraint.getUpperOperator()
            .equals(RangeConstraint.ComparisonOperator.LESS_OR_EQUAL))
            || (rConstraint.getUpperOperator()
                .equals(RangeConstraint.ComparisonOperator.LESS_THAN))) {

          // here we're using between keyword

          query.append((rConstraint.isNegated() ? "NOT " : "") + "("
              + getColumnName(rConstraint.getAttribute()) + " "
              + rConstraint.getLowerOperator().getOperatorString() + " '"
              + rConstraint.getLowerBound()

              + "' and " + getColumnName(rConstraint.getAttribute()) + " "
              + rConstraint.getUpperOperator().getOperatorString() + " '"
              + rConstraint.getUpperBound() + "')");

        } else {
          throw new UnititializedConstraintException();
        }

      } else {
        if ((rConstraint.getUpperOperator()
            .equals(RangeConstraint.ComparisonOperator.LESS_OR_EQUAL))
            || (rConstraint.getUpperOperator()
                .equals(RangeConstraint.ComparisonOperator.LESS_THAN))) {

          throw new UnititializedConstraintException();
        } else {
          // here we're using OR keyword // split range

          query.append((rConstraint.isNegated() ? "NOT " : "") + "("
              + getColumnName(rConstraint.getAttribute()) + " "
              + rConstraint.getLowerOperator().getOperatorString() + " '"
              + rConstraint.getLowerBound()

              + "' or " + getColumnName(rConstraint.getAttribute()) + " "
              + rConstraint.getUpperOperator().getOperatorString() + " '"
              + rConstraint.getUpperBound() + "')");

        }
      }
    } else {

      if (useLower) {

        query.append((rConstraint.isNegated() ? "NOT " : "")
            + getColumnName(rConstraint.getAttribute()) + " "
            + rConstraint.getLowerOperator().getOperatorString() + " '"
            + rConstraint.getLowerBound()

            + "'");

      }

      if (useUpper) {

        query.append((rConstraint.isNegated() ? "NOT " : "")
            + getColumnName(rConstraint.getAttribute()) + " "
            + rConstraint.getUpperOperator().getOperatorString() + " '"
            + rConstraint.getUpperBound()

            + "'");

      }
    }
  }
}
