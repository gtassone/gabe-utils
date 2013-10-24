package com.gmail.gtassone.util.attribute.filter.handler;

import java.util.HashMap;
import java.util.List;

import com.gmail.gtassone.util.attribute.Attribute;
import com.gmail.gtassone.util.attribute.ConstraintSet;
import com.gmail.gtassone.util.attribute.accessor.AttributeAccessor;
import com.gmail.gtassone.util.attribute.constraint.AttributeConstraint;

/**
 * Applies a {@link com.cougaarsoftware.util.attribute.ConstraintSet
 * ConstraintSet} filter to a collection of objects.
 * 
 * @author GTassone
 */
public class ConstraintFilterProcessor {

  HashMap<Attribute, AttributeAccessor> attributeAccessorMap = new HashMap<Attribute, AttributeAccessor>();

  // map constraint type to predicate
  public Iterable applyConstraints(ConstraintSet constraints,
      Iterable collection) {

    // how do we know the type of o
    // we must verify the type
    for (Object o : collection) {
      if (!constraints.getOwnerClass().isInstance(o)) {
        // reject o.
      }
      // each member of the collection
      for (Attribute attr : (List<Attribute>) constraints
          .listSupportedAttributes()) {
        for (AttributeConstraint constraint : (List<AttributeConstraint>) constraints
            .listConstraints(attr)) {

          // get constraint handler
          // test it against the member
        }
      }
    }

    return null;
  }
  // for each attribute
  // for each constraint

  // for each member of the collection
  //
  /*
   * for (OrganizationAttribute attribute : filter.listSupportedAttributes()) {
   * if (unitAttributeHandlerMap.containsKey(attribute.getAttributeName())) { if
   * (!unitAttributeHandlerMap.get(attribute.getAttributeName())
   * .testFilterConstraints(filter, unit)) { return false; } } } return true; }
   */

}
