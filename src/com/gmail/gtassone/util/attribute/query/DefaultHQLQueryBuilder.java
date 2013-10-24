package com.gmail.gtassone.util.attribute.query;

import java.util.HashMap;

import com.gmail.gtassone.util.attribute.Attribute;
import com.gmail.gtassone.util.attribute.ConstraintSet;
import com.gmail.gtassone.util.attribute.constraint.AcceptedListConstraint;
import com.gmail.gtassone.util.attribute.constraint.AcceptedValueConstraint;
import com.gmail.gtassone.util.attribute.constraint.AttributeConstraint;
import com.gmail.gtassone.util.attribute.constraint.ContainsConstraint;
import com.gmail.gtassone.util.attribute.constraint.EndsWithConstraint;
import com.gmail.gtassone.util.attribute.constraint.RangeConstraint;
import com.gmail.gtassone.util.attribute.constraint.RejectedListConstraint;
import com.gmail.gtassone.util.attribute.constraint.RejectedValueConstraint;
import com.gmail.gtassone.util.attribute.constraint.StartsWithConstraint;
import com.gmail.gtassone.util.attribute.exception.ConstraintException;
import com.gmail.gtassone.util.attribute.exception.NullConstraintSetException;
import com.gmail.gtassone.util.attribute.exception.UninitializedConstraintSetException;
import com.gmail.gtassone.util.attribute.query.handler.AcceptedListHandler;
import com.gmail.gtassone.util.attribute.query.handler.AcceptedValueHandler;
import com.gmail.gtassone.util.attribute.query.handler.AttributeHandler;
import com.gmail.gtassone.util.attribute.query.handler.ContainsHandler;
import com.gmail.gtassone.util.attribute.query.handler.EndsWithHandler;
import com.gmail.gtassone.util.attribute.query.handler.HQLConstraintHandler;
import com.gmail.gtassone.util.attribute.query.handler.RangeHandler;
import com.gmail.gtassone.util.attribute.query.handler.RejectedListHandler;
import com.gmail.gtassone.util.attribute.query.handler.RejectedValueHandler;
import com.gmail.gtassone.util.attribute.query.handler.StartsWithHandler;

/**
 * Default implementation of HQLQueryBuilder, that generates a simple initial
 * query based on the classname. This builder also allows custom
 * {@link com.cougaarsoftware.util.attribute.query.handler.AttributeHandler
 * AttributeHandlers}, which act as factories for HQLConstraintHandlers for a
 * given Attribute. The default method to convert a Constraint value to an HQL
 * fragment is using the toString() method of the supplied value. If that method
 * is not correct, custom HQLConstraintHandlers need to be provided to handle
 * that Attribute, via an AttributeHandler. TODO custom Disassemblers - see
 * HibernateType this will allow another method besides toString() to be used to
 * access the Constraint Values when constructing a query good example is Date
 * attributes - these need to be converted to the String format that Hibernate
 * uses when storing the date, in order to be included in the HQL TODO
 * post-processing for unsupported Constraint types : i.e. RegexConstraint
 * 
 * @author GTassone
 */
public class DefaultHQLQueryBuilder extends HQLQueryBuilder {

  /**
   * if a class requires custom Handlers they can be registered here
   */
  // MODIFICATION
  // Trying to implement alias capability
  /*
   * String alias; Random random = new Random();
   */
  // END MODIFICATION

  HashMap<Attribute, AttributeHandler> customHandlerMap;

  HashMap<Class<? extends AttributeConstraint>, HQLConstraintHandler> handlerMap;

  public DefaultHQLQueryBuilder() {

    customHandlerMap = new HashMap<Attribute, AttributeHandler>();

    handlerMap = new HashMap<Class<? extends AttributeConstraint>, HQLConstraintHandler>();

    handlerMap.put(AcceptedValueConstraint.class, new AcceptedValueHandler());

    handlerMap.put(RejectedValueConstraint.class, new RejectedValueHandler());

    handlerMap.put(AcceptedListConstraint.class, new AcceptedListHandler());

    handlerMap.put(RejectedListConstraint.class, new RejectedListHandler());

    handlerMap.put(ContainsConstraint.class, new ContainsHandler());

    handlerMap.put(StartsWithConstraint.class, new StartsWithHandler());

    handlerMap.put(EndsWithConstraint.class, new EndsWithHandler());

    handlerMap.put(RangeConstraint.class, new RangeHandler());

    // Modification
    // handlerMap.put(MaxConstraint.class, new MaxValueHandler());
  }

  /**
   * let's see if we can provide default behavior of creating a Handler on the
   * fly for a given constraint
   */
  @Override
  public HQLConstraintHandler getConstraintHandler(
      AttributeConstraint constraint) {

    Attribute attr = constraint.getAttribute();
    if (customHandlerMap.containsKey(attr)) {
      return customHandlerMap.get(attr).getConstraintHandler(constraint);
    }

    return handlerMap.get(constraint.getClass());

  }

  /**
   * Provides the default behavior of using the Class name as the Table or
   * mapped object name
   */
  @Override
  public String getInitialQuery(ConstraintSet constraints)
      throws ConstraintException {

    if (constraints == null) {
      throw new NullConstraintSetException();
    }
    if (constraints.getOwnerClass() == null) {
      throw new UninitializedConstraintSetException();
    }
    String initialQuery = "from " + constraints.getOwnerClass().getSimpleName();

    return initialQuery;
  }

  /**
   * for an attribute that doesn't behave 'normally', i.e. attribute name ->
   * column name and attribute value .toString() to convert value to HQL, then
   * it needs custom handlers - the AttributeHandler acts as a Factory for the
   * individual HQLConstraintHandlers for this attribute
   * 
   * @param attr
   * @param handler
   */
  public void addCustomAttributeHandler(Attribute attr, AttributeHandler handler) {
    this.customHandlerMap.put(attr, handler);

  }

}
