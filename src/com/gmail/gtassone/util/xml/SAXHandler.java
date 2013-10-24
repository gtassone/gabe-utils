package com.gmail.gtassone.util.xml;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * DefaultHandler extension for SAX parser. This class manages the element stack
 * and maintains the element text buffer properly so you don't have to.
 * Extensions of this class still need to handle all the element processing
 * logic.
 * 
 * @author <a href=mailto:gtassone@gmail.com>gtassone</a>
 * @version $Revision$
 */
public abstract class SAXHandler extends DefaultHandler {

  private StringBuffer charbuf;

  private LinkedList<String> elementStack;

  private LinkedList<Attributes> attributeStack;

  private String currentElement;

  private Attributes currentAttributes;

  private Logger logger;

  /**
   * Called at {@link #endElement(String, String, String) endElement} before
   * popping the currentElement stack. The element containing param 'value' text
   * will still be currentElement at this point. {@link #endElement(String)
   * endElement} is called after this.
   * 
   * @param value
   *        element text.
   * @throws Exception
   *         if something goes wrong.
   */
  public abstract void applyElementText(String value) throws Exception;

  /**
   * Called at {@link #startElement(String, String, String, Attributes)
   * startElement} after managing the current Element and stack and resetting
   * the char buffer.
   * 
   * @param qName
   *        the QName for the current element.
   * @param attr
   *        the attributes list for the current element.
   * @throws SAXException
   *         if something goes wrong.
   */
  public abstract void startElement(String qName, Attributes attr)
      throws SAXException;

  /**
   * @param qName
   *        QName for the element.
   * @throws SAXException
   *         if something goes wrong.
   */
  public abstract void endElement(String qName) throws SAXException;

  /**
   * Constructor sets up the stacks.
   */
  protected SAXHandler() {
    elementStack = new LinkedList<String>();
    attributeStack = new LinkedList<Attributes>();

    currentElement = null;
    currentAttributes = null;
  }

  /**
   * returns current element.
   * 
   * @return current element.
   */
  public final String getCurrentElement() {
    return currentElement;
  }

  /**
   * @return current attributes
   */
  public final Attributes getCurrentAttributes() {
    return currentAttributes;
  }

  /**
   * implements DefaultHandler abstract method to handle the character buffer so
   * you don't have to.
   * 
   * @param ch
   *        char array.
   * @param start
   *        starting index.
   * @param length
   *        substring length.
   * @throws SAXException
   *         if something goes wrong.
   */
  public final void characters(char[] ch, int start, int length)
      throws SAXException {
    if (charbuf != null) {
      charbuf.append(ch, start, length);
    }
  }

  /**
   * Implements DefaultHandler startElement to manage the element stack and
   * character buffer so you don't have to.
   * 
   * @param uri
   *        the uri
   * @param localName
   *        the local name
   * @param qName
   *        the QName string
   * @param attr
   *        the Attributes
   * @throws SAXException
   *         if something goes wrong.
   */
  @Override
  public final void startElement(String uri, String localName, String qName,
      Attributes attr) throws SAXException {

    debug("starting element : " + qName);

    elementStack.push(qName);
    attributeStack.push(attr); // FIXME unneccessary, see SAX parser doc
    currentElement = qName;
    currentAttributes = attr;

    startElement(qName, attr);

    charbuf = new StringBuffer();
  }

  /**
   * Implements DefaultHandler endElement to handle the element Stack and
   * character buffer so you don't have to.
   * 
   * @param uri
   *        the uri string.
   * @param localName
   *        the local name string.
   * @param qName
   *        the QName string.
   * @throws SAXException
   *         if something goes wrong.
   */
  public final void endElement(String uri, String localName, String qName)
      throws SAXException {

    if (currentElement != null) {

      try {

        if (charbuf != null) {
          applyElementText(charbuf.toString().trim());
        }
        charbuf = null;

      } catch (Exception e) {

        error("Error applying value at endElement : " + currentElement
            + "\nbad chars : " + charbuf.toString(), e);
      }
    }

    endElement(qName);

    debug("finished processing element : " + currentElement);

    elementStack.pop();
    attributeStack.pop();

    if (elementStack.isEmpty()) {
      currentElement = null;
    } else {
      currentElement = elementStack.getFirst();
    }

    if (attributeStack.isEmpty()) {
      currentAttributes = null;
    } else {
      currentAttributes = attributeStack.getFirst();
    }
  }

  /**
   * sets the logger for this class.
   * 
   * @param log
   *        the Logger.
   */
  public final void setLogger(Logger log) {
    this.logger = log;
  }

  /**
   * gets the logger.
   * 
   * @return the logger.
   */
  public final Logger getLogger() {
    return this.logger;
  }

  /**
   * Utility stack trace retrieval.
   * 
   * @param t
   *        the throwable.
   * @return stack trace as string.
   */
  public static String getStackTrace(Throwable t) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw, true);
    t.printStackTrace(pw);
    pw.flush();
    sw.flush();
    return sw.toString();
  }

  /**
   * error logging.
   * 
   * @param msg
   *        error message.
   * @param ex
   *        error exception.
   */
  public final void error(String msg, Throwable ex) {
    if (logger != null) {
      logger.error(msg, ex);
    } else {
      System.err.println(msg);
      ex.printStackTrace(System.err);
    }
  }

  /**
   * info logging.
   * 
   * @param info
   *        info.
   */
  public final void info(String info) {
    if (logger != null) {
      logger.info(info);
    }
  }

  /**
   * info logging.
   * 
   * @param string
   *        info.
   */
  public final void debug(String string) {
    if (logger != null) {
      logger.debug(string);
    }
  }

}
