package com.gmail.gtassone.util.attribute.query;

import java.util.List;

import com.gmail.gtassone.util.attribute.Attribute;
import com.gmail.gtassone.util.attribute.ConstraintSet;
import com.gmail.gtassone.util.attribute.constraint.AttributeConstraint;
import com.gmail.gtassone.util.attribute.exception.ConstraintException;
import com.gmail.gtassone.util.attribute.exception.UnsupportedConstraintException;
import com.gmail.gtassone.util.attribute.query.handler.HQLConstraintHandler;

/**
 * Converts a {@link com.cougaarsoftware.util.attribute.ConstraintSet
 * ConstraintSet} into an HQL query
 * 
 * @author GTassone
 */
public abstract class HQLQueryBuilder {

  /**
   * Implementation depends on the desired method of accessing the Object Table
   * Name that will be queried - be it a default method of using the Classname
   * from the ConstraintSet, a hard-coded mapping, a descriptor object of some
   * kind, etc.
   * 
   * @param constraints
   *        the ConstraintSet defining the desired query constraints
   * @return the initial query string, probably a select statement defining the
   *         object table to select from
   * @throws ConstraintException
   */
  public abstract String getInitialQuery(ConstraintSet constraints)
      throws ConstraintException;

  /**
   * Implementation depends on how ConstraintHandlers will be registered and
   * their responsibilities allocated - for instance, one implementation might
   * use a single ConstraintHandler for all AttributeConstraints, another might
   * have one Handler per Attribute; or one per Attribute type; or one per
   * Constraint type.
   * 
   * @param constraint
   *        the AttributeContsraint currently being processed into an HQL
   *        snippet
   * @return the ConstraintHandler associated with the supplied
   *         AttributeConstraint
   */
  public abstract HQLConstraintHandler getConstraintHandler(
      AttributeConstraint constraint);

  /**
   * Builds the HQL query for a given
   * {@link com.cougaarsoftware.util.attribute.ConstraintSet ConstraintSet}.
   * Behavior is customizable based on how the initial query is formed and how
   * the ConstraintHandlers are managed and perform. So basically this method is
   * just set up to append something to a StringBuffer for each Constraint.
   * 
   * @param constraints
   * @return
   */
  public String getQuery(ConstraintSet constraints) throws ConstraintException {

    // start with the default query

    // from <ObjectTableName> <instanceName> [where
    // <instanceName>.<primaryKey> is not null]

    // how does the QueryBuilder know what the ObjectTableName is
    // it could be A) the name of the class constrained by the constraintSet
    // or B) provided by some descriptor
    // or C) provided by some abstract method

    StringBuffer query = new StringBuffer(getInitialQuery(constraints));

    List<Attribute> constrainedAttributes = constraints
        .listSupportedAttributes();

    if (constrainedAttributes.size() == 0) {
      return query.toString();
    } else {
      // Modification
      // original
      // query.append(" where true");
      // modified
      query.append(" where ");
    }
    boolean isFirst = true;
    for (Attribute attribute : constrainedAttributes) {

      for (AttributeConstraint constraint : (List<AttributeConstraint>) constraints
          .listConstraints(attribute)) {

        HQLConstraintHandler handler = getConstraintHandler(constraint);

        if (handler == null) {
          throw new UnsupportedConstraintException();
        }
        if (isFirst == true) {

          handler.applyConstraint(query, constraint);
          isFirst = false;
        } else {
          query.append(" and ");
          handler.applyConstraint(query, constraint);
        }

      }
    }

    return query.toString();

  }

}
