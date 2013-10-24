package com.gmail.gtassone.util.attribute.filter.handler;

import com.gmail.gtassone.util.attribute.ConstraintSet;

public interface FilterAttributeHandler {

  public Object getAttributeValue(Object obj);

  public boolean testFilterConstraints(ConstraintSet filter, Object obj);

}
