package com.gmail.gtassone.util.attribute.constraint;

import java.io.Serializable;
import java.util.regex.Pattern;

import com.gmail.gtassone.util.attribute.Attribute;

/**
 * Just a placeholder right now, should be supported for (at least) any
 * StringAttribute.
 * 
 * @author <a href=mailto:GTassone@gmail.com>GTassone</a>
 * @version $Revision: 4272 $
 */
public class RegexConstraint extends ValueConstraint<Pattern> implements
    Serializable {

  private static final long serialVersionUID = 1;

  public RegexConstraint() {
    super();
  }

  public RegexConstraint(Pattern value) {
    super(value);
  }

  public RegexConstraint(Attribute attr) {
    super(attr);
  }

  public RegexConstraint(Attribute attr, Pattern value) {
    super(attr, value);
  }
}
