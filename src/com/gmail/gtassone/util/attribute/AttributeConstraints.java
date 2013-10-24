package com.gmail.gtassone.util.attribute;

import java.io.Serializable;

/**
 * @deprecated does nothing, use {@link ConstraintSet} instead. Mapping of named
 *             Attributes to an associated collection of AttributeConstraints.
 *             The constraints can be applied in a Filter or query. Attributes
 *             refer to a typed value held by an object; the value may be a
 *             member property of the object, or a calculated value. Part of the
 *             infrastructure will be an Accessor which provides the code to
 *             extract the value of the attribute from the object itself. This
 *             could be supported by default Accessors, which much like
 *             Hibernate would use reflection to extract the value directly from
 *             a the named field or getter method. The other part of the
 *             infrastructure would be specific to the situation the constraints
 *             are being applied in; this would be the Constraints Applicator
 *             (or Handler or Processor). This would be situation-dependent : if
 *             the Constraints are
 * @author <a href=mailto:GTassone@gmail.com>GTassone</a>
 * @version $Revision: 4272 $
 * @param <T>
 */
public interface AttributeConstraints extends Serializable {

  /*
   * public List<Attribute> listSupportedAttributes(); public Attribute
   * getAttribute(String attributeName); public <T extends Serializable>
   * List<AttributeConstraint<T>>listConstraints( Attribute attribute); public
   * <T extends Serializable, C extends AttributeConstraint<T>>
   * List<C>listConstraints( Attribute attribute, Class<C> constraintType);
   * public <T extends Serializable> boolean addConstraint(Attribute attribute,
   * AttributeConstraint constraint); public <T extends Serializable>
   * AcceptedListConstraint<T> getAcceptedListConstraint( Attribute attribute);
   * public <T extends Serializable> RejectedListConstraint<T>
   * getRejectedListConstraint( Attribute attribute); public <T extends
   * Serializable> AcceptedValueConstraint<T> getAcceptedValueConstraint(
   * Attribute attribute); public <T extends Serializable>
   * RejectedValueConstraint<T> getRejectedValueConstraint( Attribute
   * attribute); public <T extends Serializable, Comparable> RangeConstraint
   * getRangeConstraint( Attribute attribute); public StartsWithConstraint
   * getStartsWithConstraint( StringAttribute stringAttrib); public
   * EndsWithConstraint getEndsWithConstraint(StringAttribute stringAttrib);
   * public ContainsConstraint getContainsConstraint(StringAttribute
   * stringAttrib); public <T extends Serializable> void
   * addAcceptedValueConstraint( Attribute attribute, T value); public <T
   * extends Serializable> void addRejectedValueConstraint( Attribute attribute,
   * T value); public <T extends Serializable> void
   * addAcceptedListConstraint(Attribute attribute, List<T> values); public <T
   * extends Serializable> void addRejectedListConstraint(Attribute attribute,
   * List<T> values); public <T extends Serializable> void
   * addRangeConstraint(Attribute attribute, T lowerBound, T upperBound); public
   * void addStartsWithConstraint(StringAttribute attribute, String value);
   * public void addEndsWithConstraint(StringAttribute attribute, String value);
   * public void addContainsConstraint(StringAttribute attribute, String value);
   */

}
