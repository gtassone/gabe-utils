package com.gmail.gtassone.util.attribute.query.handler.column;

import com.gmail.gtassone.util.attribute.Attribute;

public class DefaultColumnProvider implements ColumnProvider {

  @Override
  public String getColumnName(Attribute attr) {
    return attr.getAttributeName();
  }

}