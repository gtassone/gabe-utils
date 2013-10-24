package com.gmail.gtassone.util.attribute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import com.gmail.gtassone.util.attribute.constraint.AcceptedListConstraint;
import com.gmail.gtassone.util.attribute.constraint.AcceptedValueConstraint;
import com.gmail.gtassone.util.attribute.constraint.AttributeConstraint;
import com.gmail.gtassone.util.attribute.constraint.ContainsConstraint;
import com.gmail.gtassone.util.attribute.constraint.EndsWithConstraint;
import com.gmail.gtassone.util.attribute.constraint.MaxConstraint;
import com.gmail.gtassone.util.attribute.constraint.RangeConstraint;
import com.gmail.gtassone.util.attribute.constraint.RangeConstraint.ComparisonOperator;
import com.gmail.gtassone.util.attribute.constraint.RegexConstraint;
import com.gmail.gtassone.util.attribute.constraint.RejectedListConstraint;
import com.gmail.gtassone.util.attribute.constraint.RejectedValueConstraint;
import com.gmail.gtassone.util.attribute.constraint.StartsWithConstraint;
/*
 * import com.cougaarsoftware.util.attribute.constraint.AcceptedListConstraint;
 * import com.cougaarsoftware.util.attribute.constraint.AcceptedValueConstraint;
 * import com.cougaarsoftware.util.attribute.constraint.AttributeConstraint;
 * import com.cougaarsoftware.util.attribute.constraint.ContainsConstraint;
 * import com.cougaarsoftware.util.attribute.constraint.EndsWithConstraint;
 * import com.cougaarsoftware.util.attribute.constraint.RangeConstraint; import
 * com.cougaarsoftware.util.attribute.constraint.RegexConstraint; import
 * com.cougaarsoftware.util.attribute.constraint.RejectedListConstraint; import
 * com.cougaarsoftware.util.attribute.constraint.RejectedValueConstraint; import
 * com.cougaarsoftware.util.attribute.constraint.StartsWithConstraint;
 */

/**
 * A set of
 * {@link com.cougaarsoftware.util.attribute.constraint.AttributeConstraint
 * AttributeConstraints}, generally used as a query or filter. The
 * AttributeConstraints, as well as the
 * {@link com.cougaarsoftware.util.attribute.Attribute Attributes}, correspond
 * to the Class specified by the Type parameter C, which is also the
 * {@link #ownerClass ownerClass} of the ConstraintSet. Therefore, the
 * ownerClass is both the type being constrained by the ConstraintSet, as well
 * as the owner of the constrained Attributes included in the ConstraintSet.
 * Internally, the ConstraintSet is organized by mappings from the Attribute
 * names to a List of AttributeConstraints and to the corresponding Attributes.
 * Use of Attribute names is intended to allow some external entity to maintain
 * a registry of known Attributes. TODO FIXME If there is an intention to
 * support a more arbitrary set of Constraint types, the addXConstraint methods
 * should be removed; there was an assumption that the number of different
 * Constraint types is limited. The critical method to retain is simply
 * {@link #addConstraint(Attribute, AttributeConstraint) addConstraint}. TODO to
 * support compound logic, this class (or a parallel class) must evolve to
 * represent a binary logic tree of constraints, rather than just a set of
 * constraints.
 * 
 * @author <a href=mailto:GTassone@cougaarsoftware.com>GTassone</a>
 * @version $Revision: 4272 $
 */
public class ConstraintSet<C> implements Serializable {

  private static final long serialVersionUID = 1;

  Map<String, List<AttributeConstraint>> attributeConstraintMap;

  Class<C> ownerClass;

  private HashMap<String, Attribute> attributeNameMap;

  public ConstraintSet(Class<C> ownerClass) {
    attributeConstraintMap = new HashMap<String, List<AttributeConstraint>>();
    this.ownerClass = ownerClass;
    // here should i fill in the supported attributes?
    // or should that be up for grabs?
    // currently we are going to support arbitrary attributes.
    attributeNameMap = new HashMap<String, Attribute>();
  }

  /**
   * @return the Class being constrained, i.e. the class which owns the
   *         Attributes being constrained.
   */
  public Class<C> getOwnerClass() {
    return ownerClass;
  }

  // **************************************************************************
  // add Methods
  // **************************************************************************

  public <T extends Serializable> void addAcceptedListConstraint(
      Attribute<T, C> attribute, List<T> values) {

    if (!attributeNameMap.containsKey(attribute.getAttributeName())) {
      attributeNameMap.put(attribute.getAttributeName(), attribute);
    }
    if (!attributeConstraintMap.containsKey(attribute.getAttributeName())) {
      attributeConstraintMap.put(attribute.getAttributeName(),
          new Vector<AttributeConstraint>());
    }
    AcceptedListConstraint<T> constraint = new AcceptedListConstraint<T>(
        attribute);
    for (T value : values) {
      constraint.addValue(value);
    }

    attributeConstraintMap.get(attribute.getAttributeName()).add(constraint);

  }

  public <T extends Serializable> void addAcceptedValueConstraint(
      Attribute<T, C> attribute, T value) {

    if (!attributeNameMap.containsKey(attribute.getAttributeName())) {
      attributeNameMap.put(attribute.getAttributeName(), attribute);
    }
    if (!attributeConstraintMap.containsKey(attribute.getAttributeName())) {
      attributeConstraintMap.put(attribute.getAttributeName(),
          new Vector<AttributeConstraint>());
    }

    AcceptedValueConstraint<T> constraint = new AcceptedValueConstraint<T>(
        attribute);

    constraint.setValue(value);

    attributeConstraintMap.get(attribute.getAttributeName()).add(constraint);

  }

  /**
   * FIXME Why doesn't this populate the mappings if the attribute isn't
   * present? All the other add methods do. I believe this was to prevent
   * arbitrary Attributes, which we in fact want to support.
   * 
   * @param <T>
   *        included to allow type parameter declaration on the Attribute
   * @param attribute
   *        the Attribute being constrained
   * @param constraint
   *        the AttributeConstraint being applied to the attribute
   * @return
   */
  public <T extends Serializable> boolean addConstraint(
      Attribute<T, C> attribute, AttributeConstraint constraint) {
    if (!attributeNameMap.containsKey(attribute.getAttributeName())) {
      attributeNameMap.put(attribute.getAttributeName(), attribute);
    }
    if (!attributeConstraintMap.containsKey(attribute.getAttributeName())) {
      attributeConstraintMap.put(attribute.getAttributeName(),
          new Vector<AttributeConstraint>());
    }

    attributeConstraintMap.get(attribute.getAttributeName()).add(constraint);
    return true;

  }

  public <T extends Serializable & Comparable> void addRangeConstraint(
      Attribute<T, C> attribute, T lowerBound, T upperBound) {
    if (!attributeNameMap.containsKey(attribute.getAttributeName())) {
      attributeNameMap.put(attribute.getAttributeName(), attribute);
    }
    if (!attributeConstraintMap.containsKey(attribute.getAttributeName())) {
      attributeConstraintMap.put(attribute.getAttributeName(),
          new Vector<AttributeConstraint>());
    }

    RangeConstraint<T> constraint = new RangeConstraint<T>(attribute);

    constraint.setLowerBound(lowerBound);
    constraint.setUpperBound(upperBound);

    attributeConstraintMap.get(attribute.getAttributeName()).add(constraint);

  }

  public <T extends Serializable & Comparable> void addRangeConstraint(
      Attribute<T, C> attribute, T lowerBound, T upperBound,
      ComparisonOperator lowerOp, ComparisonOperator upperOp) {

    if (!attributeNameMap.containsKey(attribute.getAttributeName())) {
      attributeNameMap.put(attribute.getAttributeName(), attribute);
    }
    if (!attributeConstraintMap.containsKey(attribute.getAttributeName())) {
      attributeConstraintMap.put(attribute.getAttributeName(),
          new Vector<AttributeConstraint>());
    }

    RangeConstraint<T> constraint = new RangeConstraint<T>(attribute);

    constraint.setLowerBound(lowerBound);
    constraint.setUpperBound(upperBound);
    constraint.setLowerOperator(lowerOp);
    constraint.setUpperOperator(upperOp);

    attributeConstraintMap.get(attribute.getAttributeName()).add(constraint);
  }

  public <T extends Serializable> void addRejectedListConstraint(
      Attribute<T, C> attribute, List<T> values) {

    if (!attributeNameMap.containsKey(attribute.getAttributeName())) {
      attributeNameMap.put(attribute.getAttributeName(), attribute);
    }
    if (!attributeConstraintMap.containsKey(attribute.getAttributeName())) {
      attributeConstraintMap.put(attribute.getAttributeName(),
          new Vector<AttributeConstraint>());
    }

    RejectedListConstraint<T> constraint = new RejectedListConstraint<T>(
        attribute);
    for (T value : values) {
      constraint.addValue(value);
    }

    attributeConstraintMap.get(attribute.getAttributeName()).add(constraint);

  }

  public <T extends Serializable> void addRejectedValueConstraint(
      Attribute<T, C> attribute, T value) {
    if (!attributeNameMap.containsKey(attribute.getAttributeName())) {
      attributeNameMap.put(attribute.getAttributeName(), attribute);
    }
    if (!attributeConstraintMap.containsKey(attribute.getAttributeName())) {
      attributeConstraintMap.put(attribute.getAttributeName(),
          new Vector<AttributeConstraint>());
    }

    RejectedValueConstraint<T> constraint = new RejectedValueConstraint<T>(
        attribute);

    constraint.setValue(value);

    attributeConstraintMap.get(attribute.getAttributeName()).add(constraint);

  }

  public void addContainsConstraint(Attribute<String, C> attribute, String value) {
    if (!attributeNameMap.containsKey(attribute.getAttributeName())) {
      attributeNameMap.put(attribute.getAttributeName(), attribute);
    }
    if (!attributeConstraintMap.containsKey(attribute.getAttributeName())) {
      attributeConstraintMap.put(attribute.getAttributeName(),
          new Vector<AttributeConstraint>());
    }

    ContainsConstraint constraint = new ContainsConstraint(attribute);

    constraint.setValue(value);

    attributeConstraintMap.get(attribute.getAttributeName()).add(constraint);

  }

  public void addEndsWithConstraint(Attribute<String, C> attribute, String value) {
    if (!attributeNameMap.containsKey(attribute.getAttributeName())) {
      attributeNameMap.put(attribute.getAttributeName(), attribute);
    }
    if (!attributeConstraintMap.containsKey(attribute.getAttributeName())) {
      attributeConstraintMap.put(attribute.getAttributeName(),
          new Vector<AttributeConstraint>());
    }

    EndsWithConstraint constraint = new EndsWithConstraint(attribute);

    constraint.setValue(value);

    attributeConstraintMap.get(attribute.getAttributeName()).add(constraint);

  }

  public void addStartsWithConstraint(Attribute<String, C> attribute,
      String value) {
    if (!attributeNameMap.containsKey(attribute.getAttributeName())) {
      attributeNameMap.put(attribute.getAttributeName(), attribute);
    }
    if (!attributeConstraintMap.containsKey(attribute.getAttributeName())) {
      attributeConstraintMap.put(attribute.getAttributeName(),
          new Vector<AttributeConstraint>());
    }

    StartsWithConstraint constraint = new StartsWithConstraint(attribute);

    constraint.setValue(value);

    attributeConstraintMap.get(attribute.getAttributeName()).add(constraint);

  }

  public void addRegexConstraint(Attribute<String, C> attribute, Pattern value) {
    if (!attributeNameMap.containsKey(attribute.getAttributeName())) {
      attributeNameMap.put(attribute.getAttributeName(), attribute);
    }
    if (!attributeConstraintMap.containsKey(attribute.getAttributeName())) {
      attributeConstraintMap.put(attribute.getAttributeName(),
          new Vector<AttributeConstraint>());
    }

    RegexConstraint constraint = new RegexConstraint(attribute);

    constraint.setValue(value);

    attributeConstraintMap.get(attribute.getAttributeName()).add(constraint);

  }

  public <T extends Serializable & Comparable> void addMaxValueConstraint(
      Attribute<T, C> attribute) {
    if (!attributeNameMap.containsKey(attribute.getAttributeName())) {
      attributeNameMap.put(attribute.getAttributeName(), attribute);
    }
    if (!attributeConstraintMap.containsKey(attribute.getAttributeName())) {
      attributeConstraintMap.put(attribute.getAttributeName(),
          new Vector<AttributeConstraint>());
    }

    MaxConstraint constraint = new MaxConstraint(attribute);
    constraint
        .setConstraintOwnerClassName(this.getOwnerClass().getSimpleName());
    attributeConstraintMap.get(attribute.getAttributeName()).add(constraint);

  }

  /*
   * public void addMaxValueConstraint(Attribute<T, C>) { }
   */

  // **************************************************************************
  // Create methods - factory methods
  // get a new constraint for an attribute
  // **************************************************************************

  public <T extends Serializable> AcceptedListConstraint<T> getAcceptedListConstraint(
      Attribute<T, C> attribute) {

    return new AcceptedListConstraint<T>(attribute);
  }

  public <T extends Serializable> AcceptedValueConstraint<T> getAcceptedValueConstraint(
      Attribute<T, C> attribute) {

    return new AcceptedValueConstraint<T>(attribute);
  }

  public Attribute getAttribute(String attributeName) {
    if (attributeNameMap.containsKey(attributeName)) {
      return attributeNameMap.get(attributeName);
    } else
      return null;
  }

  public ContainsConstraint getContainsConstraint(
      Attribute<String, C> stringAttrib) {

    return new ContainsConstraint(stringAttrib);
  }

  public EndsWithConstraint getEndsWithConstraint(
      Attribute<String, C> stringAttrib) {

    return new EndsWithConstraint(stringAttrib);
  }

  public <T extends Serializable & Comparable> RangeConstraint<T> getRangeConstraint(
      Attribute<T, C> attribute) {

    return new RangeConstraint<T>(attribute);
  }

  public <T extends Serializable> RejectedListConstraint<T> getRejectedListConstraint(
      Attribute<T, C> attribute) {

    return new RejectedListConstraint<T>(attribute);
  }

  public <T extends Serializable> RejectedValueConstraint<T> getRejectedValueConstraint(
      Attribute<T, C> attribute) {

    return new RejectedValueConstraint<T>(attribute);
  }

  public StartsWithConstraint getStartsWithConstraint(
      Attribute<String, C> stringAttrib) {

    return new StartsWithConstraint(stringAttrib);
  }

  public RegexConstraint getRegexConstraint(Attribute<String, C> stringAttrib) {
    return new RegexConstraint();
  }

  // **************************************************************************
  // list methods
  // **************************************************************************

  /**
   * Lists the registered AttributeConstraints for the given Attribute.
   * 
   * @param <T>
   *        type parameter for the Attribute type
   * @param attribute
   *        the Attribute for which to retrieve the associated
   *        AttributeConstraints
   * @return the List of AttributeConstraints corresponding to the provided
   *         Attribute
   */
  public <T extends Serializable> List<AttributeConstraint<T>> listConstraints(
      Attribute<T, C> attribute) {

    List<AttributeConstraint<T>> constraints = new LinkedList<AttributeConstraint<T>>();

    List<AttributeConstraint> internalConstraints = attributeConstraintMap
        .get(attribute.getAttributeName());
    if (internalConstraints == null) {
      return null;
    }
    for (AttributeConstraint constraint : attributeConstraintMap.get(attribute
        .getAttributeName())) {
      constraints.add(constraint);
    }

    return constraints;
  }

  /**
   * Lists the registered AttributeConstraints of a particular type for the
   * given Attribute.
   * 
   * @param <T>
   *        Type parameter for the Attribute type
   * @param <C>
   *        Type parameter for the Constraint type
   * @param attribute
   *        the Attribute for which to retrieve the associated
   *        AttributeConstraints
   * @param constraintType
   *        the Class of AttributeConstraint being requested
   * @return the List of AttributeConstraints corresponding to the provided
   *         Attribute, and of the requested AttributeConstraint type
   */
  public <T extends Serializable, C extends AttributeConstraint<T>> List<C> listConstraints(
      Attribute<T, C> attribute, Class<C> constraintType) {

    List<C> constraints = new LinkedList<C>();

    List<AttributeConstraint> registeredConstraints = attributeConstraintMap
        .get(attribute.getAttributeName());

    if (registeredConstraints != null) {
      for (AttributeConstraint constraint : registeredConstraints) {
        if (constraintType.isAssignableFrom(constraint.getClass())) {
          constraints.add((C) constraint);
        }
      }
    }

    return constraints;
  }

  /**
   * @return the List of Attributes currently registered within this
   *         ConstraintSet.
   */
  public List<Attribute> listSupportedAttributes() {
    return new ArrayList<Attribute>(attributeNameMap.values());
  }

}
