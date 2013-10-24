package com.gmail.gtassone.util.attribute.config;


public interface ValueParser<T> {

  public T parseValue(String stringValue);
}
