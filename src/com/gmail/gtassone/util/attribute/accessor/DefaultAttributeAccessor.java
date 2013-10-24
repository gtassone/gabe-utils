package com.gmail.gtassone.util.attribute.accessor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.gmail.gtassone.util.attribute.Attribute;

public class DefaultAttributeAccessor<T, C> implements AttributeAccessor<T, C> {

  private Attribute<T, C> attribute;

  public DefaultAttributeAccessor(Attribute<T, C> attribute) {
    this.attribute = attribute;
  }

  public Attribute<T, C> getAttribute() {
    return attribute;
  }

  public T getAttributeValue(Object o) {
    if (attribute.getOwnerClass().isAssignableFrom(o.getClass())) {

      try {
        Field field = o.getClass().getDeclaredField(
            attribute.getAttributeName());
        T value = (T) field.get(o);
        return value;
      } catch (Exception fieldException) {
        System.err.println("couldn't find field: "
            + attribute.getAttributeName() + ", trying method : get"
            + capitalize(attribute.getAttributeName()));
        fieldException.printStackTrace(System.err);
        try {

          Method method = o.getClass().getDeclaredMethod(
              "get" + capitalize(attribute.getAttributeName()));
          T value = (T) method.invoke(o);
          return value;
        } catch (NoSuchMethodException methodException) {

          methodException.printStackTrace(System.err);

        } catch (InvocationTargetException invocationException) {

          invocationException.printStackTrace(System.err);

        } catch (IllegalAccessException accessException) {

          accessException.printStackTrace(System.err);

        }
      }
    }
    return null;
  }

  public Class<C> getOwnerClass() {
    return attribute.getOwnerClass();
  }

  private String capitalize(String s) {
    return s.substring(0, 1).toUpperCase() + s.substring(1);
  }
}
