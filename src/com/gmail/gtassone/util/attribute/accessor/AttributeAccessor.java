package com.gmail.gtassone.util.attribute.accessor;


public interface AttributeAccessor<T, C> {

  public Class<C> getOwnerClass();

  public T getAttributeValue(Object o);

}
