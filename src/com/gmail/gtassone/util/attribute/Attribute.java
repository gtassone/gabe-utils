package com.gmail.gtassone.util.attribute;

import java.io.Serializable;

/**
 * Type parameter T is the type of the attribute described by this object. Type
 * parameter C is the Class that owns the attribute described by this object.
 * 
 * @author <a href=mailto:GTassone@gmail.com>GTassone</a>
 * @version $Revision: 4272 $
 */
public interface Attribute<T, C> extends Serializable {

  /**
   * The Class type of the attribute described by this object.
   * 
   * @return
   */
  public Class<T> getClassType();

  /**
   * The Class containing the attribute described by this object.
   * 
   * @return
   */
  public Class<C> getOwnerClass();

  /**
   * The name of this attribute. If the default accessor is used, this name will
   * inform introspective access to the attribute value.
   * 
   * @return
   */
  public String getAttributeName();

}
