package com.gmail.gtassone.util.attribute.config;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.gmail.gtassone.util.xml.SAXHandler;
import com.gmail.gtassone.util.attribute.Attribute;
import com.gmail.gtassone.util.attribute.BooleanAttribute;
import com.gmail.gtassone.util.attribute.DoubleAttribute;
import com.gmail.gtassone.util.attribute.IntegerAttribute;
import com.gmail.gtassone.util.attribute.StringAttribute;
import com.gmail.gtassone.util.attribute.UserTypedAttribute;
import com.gmail.gtassone.util.attribute.accessor.AttributeAccessor;
import com.gmail.gtassone.util.attribute.accessor.DefaultAttributeAccessor;

public class ClassAttributeConfigHandler extends SAXHandler {

  public interface ClassAttributeSupport {

    public boolean isRegistered(Class clazz);

    public void registerClass(Class clazz);

    public void registerAttribute(Class clazz, Attribute attr,
        AttributeAccessor accessor);
  }

  ClassAttributeSupport support;

  String currentClassName;

  Class currentClass;

  String currentAttributeName;

  String currentAttributeTypeName;

  Class currentAttributeClass;

  String currentAccessorName;

  Class currentAccessorClass;

  String currentParserClassName;

  Class currentParserClass;

  String currentParsedTypeName;

  Class currentParsedType;

  Logger log;

  static Map<Class, Class> attributeTypeToClassMap;

  static Map<Class, ValueParser> getValueParserMap() {
    return valueParserMap;
  }

  static Map<Class, ValueParser> valueParserMap;

  static {

    attributeTypeToClassMap = new HashMap<Class, Class>();

    attributeTypeToClassMap.put(String.class, StringAttribute.class);
    attributeTypeToClassMap.put(Integer.class, IntegerAttribute.class);
    attributeTypeToClassMap.put(Boolean.class, BooleanAttribute.class);
    attributeTypeToClassMap.put(Double.class, DoubleAttribute.class);

    valueParserMap = new HashMap<Class, ValueParser>();

    valueParserMap.put(Integer.class, new IntegerParser());
    valueParserMap.put(String.class, new StringParser());
    valueParserMap.put(Double.class, new DoubleParser());
    valueParserMap.put(Boolean.class, new BooleanParser());
    valueParserMap.put(Pattern.class, new RegexParser());

  }

  static synchronized void addValueParser(Class parsedClass, Class parserClass) {

    if (parsedClass != null && parserClass != null
        && !valueParserMap.containsKey(parsedClass)) {
      try {
        ValueParser parser = (ValueParser) parserClass.newInstance();
        valueParserMap.put(parsedClass, parser);

      } catch (Exception e) {

        System.err.println("couldn't create ValueParser : "
            + parserClass.getName());

        e.printStackTrace();

      }
    }

  }

  // Constructor.
  public ClassAttributeConfigHandler(ClassAttributeSupport support) {

    this.support = support;
    log = Logger.getLogger(this.getClass());

  }

  @Override
  public void applyElementText(String value) throws Exception {

    if (value == null || value.equals("")) {
      return;
    }

    // weird hack to fix parsing attr.cfg.xml error
    if (value.equalsIgnoreCase("java.lang.Str")
        || (value.equalsIgnoreCase("ing")))
      value = "java.lang.String";

    if (currentClass == null) {
      // return;
    }

    if (getCurrentElement().equals("class")) {
      // should not contain characters
    }

    if (getCurrentElement().equals("attribute")) {
      // should not contain characters
    }

    if (getCurrentElement().equals("name")) {
      currentAttributeName = value;

      info("attribute name processed : " + value);
    }

    if (getCurrentElement().equals("type")) {
      currentAttributeTypeName = value;
      try {
        currentAttributeClass = Class.forName(currentAttributeTypeName);
        info("current attribute type processed : "
            + currentAttributeClass.getName());
      } catch (Exception e) {
        error("attribute class could not be resolved : "
            + currentAttributeTypeName, e);
        currentAttributeClass = null;
      }
    }

    if (getCurrentElement().equals("accessor")) {
      currentAccessorName = value;
      try {
        currentAccessorClass = Class.forName(currentAccessorName);

        info("current accessor type processed : "
            + currentAccessorClass.getName());
      } catch (Exception e) {
        error("accessor class could not be resolved : " + currentAccessorName,
            e);

        currentAccessorClass = null;
      }
    }

    if (getCurrentElement().equals("parserclass")) {
      currentParserClassName = value;

      info("Parser Class Name : " + currentParserClassName);
      try {
        currentParserClass = Class.forName(currentParserClassName);

        info("currentParserClass : " + currentParserClass.getName());
      } catch (Exception e) {
        currentParserClass = null;
        error("failed to find Parser Class", e);
      }
    }

    if (getCurrentElement().equals("parsedtype")) {
      currentParsedTypeName = value;

      info("Parsed Class Name : " + currentParsedTypeName);
      try {
        currentParsedType = Class.forName(currentParsedTypeName);

        info("currentParsedType : " + currentParsedType.getName());
      } catch (Exception e) {
        currentParsedType = null;
        error("failed to find currentParsedType Class", e);
      }
    }

  }

  @Override
  public void endElement(String qName) throws SAXException {

    // ******************************************************************
    // create a new parser
    // ******************************************************************
    if (getCurrentElement().equals("parser")) {
      info("new parser for : " + currentParsedType.getName() + "\n"
          + currentParserClass.getName());

      addValueParser(currentParsedType, currentParserClass);

      currentParsedType = null;
      currentParsedTypeName = null;
      currentParserClass = null;
      currentParserClassName = null;
    }

    if (getCurrentElement().equals("class")) {
      if (!support.isRegistered(currentClass)) {
        support.registerClass(currentClass);
      }
    }
    if (getCurrentElement().equals("attribute")) {

      System.err.println("processing attribute element: ");

      if (currentClass != null && currentAttributeClass != null
          && currentAttributeName != null) {

        // we need a mapping from currentAttributeClass (type for
        // attribute)
        // to Attribute type.

        /*
         * Attribute newAttribute = new AbstractAttribute(currentAttributeName,
         * currentClass) { private Class type;
         * @Override public Class getClassType() { return type; } public
         * Attribute setClassType(Class type) { this.type = type; return this; }
         * }.setClassType(currentAttributeClass);
         */

        Attribute newAttribute = new UserTypedAttribute(currentAttributeName,
            currentClass, currentAttributeClass);

        AttributeAccessor accessor = null;

        if (currentAccessorClass == null) {
          accessor = new DefaultAttributeAccessor(newAttribute);
        } else {
          try {
            accessor = (AttributeAccessor) currentAccessorClass.newInstance();
          } catch (Exception e) {
            accessor = new DefaultAttributeAccessor(newAttribute);
          }
        }

        System.err.println("registering attribute : " + currentAttributeName);
        if (accessor != null) {
          support.registerAttribute(currentClass, newAttribute, accessor);

        } else {
          System.err.println("NO ACCESSOR registered for : "
              + currentAttributeName);
        }
      } else {
        System.err.println("not enough info for attribute: "
            + currentAttributeName);
      }
    }

  }

  @Override
  public void startElement(String qName, Attributes attr) throws SAXException {
    if (getCurrentElement().equals("class")) {
      currentClassName = getCurrentAttributes().getValue("classname");

      try {
        currentClass = Class.forName(currentClassName);

        info("processing owner class : " + currentClass.getSimpleName());

      } catch (Exception e) {
        currentClass = null;
        error("failed to find Owner Class", e);
      }
    }

    // setup for new attribute
    if (getCurrentElement().equals("attribute")) {
      currentAttributeName = null;
      currentAttributeTypeName = null;
      currentAttributeClass = null;
      currentAccessorName = null;
      currentAccessorClass = null;
    }
  }

  private static class IntegerParser implements ValueParser<Integer> {

    @Override
    public Integer parseValue(String stringValue) {
      return Integer.parseInt(stringValue);
    }

  }

  private static class DoubleParser implements ValueParser<Double> {

    @Override
    public Double parseValue(String stringValue) {
      return Double.parseDouble(stringValue);
    }

  }

  private static class StringParser implements ValueParser<String> {

    @Override
    public String parseValue(String stringValue) {
      return stringValue.trim();
    }

  }

  private static class BooleanParser implements ValueParser<Boolean> {

    @Override
    public Boolean parseValue(String stringValue) {
      return Boolean.parseBoolean(stringValue);
    }

  }

  private static class RegexParser implements ValueParser<Pattern> {

    @Override
    public Pattern parseValue(String stringValue) {
      Pattern pattern = Pattern.compile(stringValue.trim());
      return pattern;
    }

  }

}
