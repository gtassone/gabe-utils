/**
 * Copyright 2001-2008, Cougaar Technologies, Inc. Originally authored under
 * agreement by Cougaar Software, Inc. with all Copyright and Rights
 * transferred. This file constitutes Proprietary Data. Cougaar Technologies,
 * Inc. reserves All Rights to this work product and any updates or derivatives.
 * This software is provided as part of the ActiveEdge product and use implies
 * acceptance of the ActiveEdge End-User License Agreement. Use of this software
 * or any derivative by any party outside of that License Agreement is strictly
 * prohibited. For a copy of the license, mail info@cougaarsoftware.com Cougaar
 * Software, Inc. is the exclusive reseller of Cougaar Technologies ActiveEdge
 * product through 2010 and is authorized as part of that agreement to re-brand
 * ActiveEdge as a Cougaar Software, Inc. product for purposes of bids,
 * marketing, sales and contracting.
 */
package com.gmail.gtassone.util.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

/**
 * Utility builder class for setting Swing Component settings and GridBagLayout
 * management with less typing. This builder allows you to chain setter
 * functions, saving a LOT of typing. Setter functions also have the 'set'
 * portion removed from the name, both to save typing and to look more like the
 * style of the GridBagConstraints which are used internally and opt for public
 * members over setters. The proper usage is
 * <p>
 * <code>
 * JPanel parentComponent = new JPanel();
 * 
 * GridBagBuilder builder = new GridBagBuilder(parentComponent);
 *  
 * builder.with(child).gridx(0).gridy(0).weightx(0.1).weighty(0.1).gridheight(1)
 * .gridwidth(2).bg(Color.black).fg(Color.white).etch().apply();
 * </code>
 * <p>
 * where you can arbitrarily set constraints so long as you first properly call
 * with(Component child) and last call apply().
 * 
 * @author <a href=mailto:GTassone@gmail.com>GTassone</a>
 * @version $Revision: 4272 $
 */
public class GridBagBuilder {

  private Container parent;

  private GridBagLayout layout;

  private Component currentChild;

  private GridBagConstraints currentConstraints;

  /**
   * Constructs a new builder for a given container object.
   * 
   * @param c
   *        the parent container which will use this builder and layout.
   */
  public GridBagBuilder(Container c) {
    parent = c;
    layout = new GridBagLayout();
    c.setLayout(layout);
  }

  /**
   * sets the current component to apply constraints to. This component will
   * receive all constraint modifications when apply() is called.
   * 
   * @param c
   *        the component subsequent constraints will apply to when apply() is
   *        called.
   * @return this object.
   */
  public final GridBagBuilder with(Component c) {
    currentChild = c;
    currentConstraints = new GridBagConstraints();
    return this;
  }

  /**
   * applies the current set of constraints to the current component.
   * 
   * @return this object.
   */
  public final GridBagBuilder apply() {
    layout.setConstraints(currentChild, currentConstraints);
    parent.add(currentChild);
    return this;
  }

  /**
   * FIXME this is problematic because it can't reset the constraints. so,
   * you'll be starting with a non-fresh set of constraints and by the time you
   * make this call you will not be able to reset them, so any hold-overs will
   * remain.
   * 
   * @param child
   *        the object to apply current constraints to. useful if with() was not
   *        called, or if applying the same set of constraints to multiple
   *        objects.
   * @return this object.
   */
  public final GridBagBuilder applyTo(Component child) {
    layout.setConstraints(child, currentConstraints);
    parent.add(child);
    return this;
  }

  /**
   * Sets the background color for the current object.
   * 
   * @param c
   *        the color to set the current object background to.
   * @return this object.
   */
  public final GridBagBuilder bg(Color c) {
    currentChild.setBackground(c);
    return this;
  }

  /**
   * Sets the foreground color for the current object.
   * 
   * @param c
   *        The color to set the current object foreground to.
   * @return this object.
   */
  public final GridBagBuilder fg(Color c) {
    currentChild.setForeground(c);
    return this;
  }

  /**
   * Sets the bounds for the current object.
   * 
   * @param x
   *        x coordinate.
   * @param y
   *        y coordinate.
   * @param height
   *        height value in pixels.
   * @param width
   *        width value in pixels.
   * @return this object.
   */
  public final GridBagBuilder bounds(int x, int y, int height, int width) {
    currentChild.setBounds(x, y, height, width);
    return this;
  }

  /**
   * Sets the size for the current object.
   * 
   * @param dim
   *        the size dimension.
   * @return this object.
   */
  public final GridBagBuilder size(Dimension dim) {
    currentChild.setSize(dim);
    return this;
  }

  /**
   * Sets the size for the current object.
   * 
   * @param width
   *        width value in pixels.
   * @param height
   *        height value in pixels.
   * @return this object.
   */
  public final GridBagBuilder size(int width, int height) {
    return size(new Dimension(width, height));
  }

  /**
   * sets the minimum size for the current object.
   * 
   * @param dim
   *        minimum size dimension.
   * @return tihs object.
   */
  public final GridBagBuilder minSize(Dimension dim) {
    currentChild.setMinimumSize(dim);
    return this;
  }

  /**
   * sets the minimum size for the current object.
   * 
   * @param width
   *        width in pixels.
   * @param height
   *        height in pixels.
   * @return this object.
   */
  public final GridBagBuilder minSize(int width, int height) {
    return minSize(new Dimension(width, height));
  }

  /**
   * sets the maximum size for the current object.
   * 
   * @param dim
   *        maximum size dimension.
   * @return this object.
   */
  public final GridBagBuilder maxSize(Dimension dim) {
    currentChild.setMaximumSize(dim);
    return this;
  }

  /**
   * sets the maximum size for the current object.
   * 
   * @param width
   *        in pixels.
   * @param height
   *        in pixels.
   * @return this object.
   */
  public final GridBagBuilder maxSize(int width, int height) {
    return maxSize(new Dimension(width, height));
  }

  /**
   * sets the preferred size for the current object.
   * 
   * @param dim
   *        preferred size dimension.
   * @return this object.
   */
  public final GridBagBuilder prefSize(Dimension dim) {
    currentChild.setPreferredSize(dim);
    return this;
  }

  /**
   * sets the preferred size for the current object.
   * 
   * @param width
   *        in pixels.
   * @param height
   *        in pixels.
   * @return this object.
   */
  public final GridBagBuilder prefSize(int width, int height) {
    return prefSize(new Dimension(width, height));
  }

  /**
   * sets the current JLabel to opaque. does nothing if the current object is
   * not a JLabel.
   * 
   * @param isOpaque
   *        boolean opaqueness.
   * @return this object.
   */
  public final GridBagBuilder opaque(boolean isOpaque) {
    if (currentChild instanceof JLabel) {
      ((JLabel) currentChild).setOpaque(isOpaque);
    }
    return this;
  }

  /**
   * sets the border for the current object.
   * 
   * @param b
   *        border.
   * @return this object.
   */
  public final GridBagBuilder border(Border b) {
    if (currentChild instanceof JComponent) {
      ((JComponent) currentChild).setBorder(b);
    }
    return this;
  }

  /**
   * sets the bevel for the current object to RAISED.
   * 
   * @return this object.
   */
  public final GridBagBuilder bevelUp() {
    Border b = BorderFactory.createBevelBorder(BevelBorder.RAISED);
    return border(b);
  }

  /**
   * sets the bevel for the current object to LOWERED.
   * 
   * @return this object.
   */
  public final GridBagBuilder bevelDown() {
    Border b = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
    return border(b);
  }

  /**
   * sets the border for the current object to etched.
   * 
   * @return this object.
   */
  public final GridBagBuilder etch() {
    Border b = BorderFactory.createEtchedBorder();
    return border(b);
  }

  /**
   * sets the border for the current object to raised etched border.
   * 
   * @return this object.
   */
  public final GridBagBuilder etchUp() {
    Border b = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
    return border(b);
  }

  /**
   * sets the border for the current object to lowered etched border.
   * 
   * @return this object.
   */
  public final GridBagBuilder etchDown() {
    Border b = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
    return border(b);
  }

  /**
   * sets the border for the current object to black line.
   * 
   * @return this object.
   */
  public final GridBagBuilder line() {
    Border b = BorderFactory.createLineBorder(Color.black);
    return border(b);
  }

  /**
   * sets the border for the current object to colored line.
   * 
   * @param c
   *        border line color.
   * @return this object.
   */
  public final GridBagBuilder line(Color c) {
    Border b = BorderFactory.createLineBorder(c);
    return border(b);
  }

  /**
   * sets the border for the current object to colored line of given width.
   * 
   * @param c
   *        border line color.
   * @param width
   *        border line width.
   * @return this object.
   */
  public final GridBagBuilder line(Color c, int width) {
    Border b = BorderFactory.createLineBorder(c, width);
    return border(b);
  }

  /**
   * sets the border for the current object to titled border.
   * 
   * @param b
   *        the border.
   * @param title
   *        the title.
   * @return this object.
   */
  public final GridBagBuilder title(Border b, String title) {
    Border tb = BorderFactory.createTitledBorder(b, title);
    return border(tb);
  }

  /**
   * sets the gridx property. see GridbagLayout.
   * 
   * @param x
   *        x grid coordinate.
   * @return this object.
   */
  public final GridBagBuilder gridx(int x) {
    currentConstraints.gridx = x;
    return this;
  }

  /**
   * sets the gridy property. see GridbagLayout.
   * 
   * @param y
   *        y grid coordinate.
   * @return this object.
   */
  public final GridBagBuilder gridy(int y) {
    currentConstraints.gridy = y;
    return this;
  }

  /**
   * sets the gridheight constraint. see GridbagLayout.
   * 
   * @param h
   *        grid height.
   * @return this object.
   */
  public final GridBagBuilder gridh(int h) {
    currentConstraints.gridheight = h;
    return this;
  }

  /**
   * sets the gridwidth constraint. see GridBagLayout.
   * 
   * @param w
   *        grid width.
   * @return this object.
   */
  public final GridBagBuilder gridw(int w) {
    currentConstraints.gridwidth = w;
    return this;
  }

  /**
   * sets the gridwidth constraint. see GridBagLayout.
   * 
   * @param w
   *        grid width.
   * @return this object.
   */
  public final GridBagBuilder gridwidth(int w) {
    return gridw(w);
  }

  /**
   * sets the gridheight constraint. see GridBagLayout.
   * 
   * @param h
   *        grid height.
   * @return this object.
   */
  public final GridBagBuilder gridheight(int h) {
    return gridh(h);
  }

  /**
   * sets the gridheight constraint. See GridBagLayout.
   * 
   * @param height
   *        grid height.
   * @return this object.
   */
  public final GridBagBuilder h(int height) {
    return gridh(height);
  }

  /**
   * sets the gridwidth constraint. See GridbagLayout.
   * 
   * @param width
   *        grid width.
   * @return this object.
   */
  public final GridBagBuilder w(int width) {
    return gridw(width);
  }

  /**
   * Sets the fill constraint for the current object.
   * 
   * @param fillConstant
   *        see GridBagConstraints.
   * @return this object.
   */
  public final GridBagBuilder fill(int fillConstant) {
    currentConstraints.fill = fillConstant;
    return this;
  }

  /**
   * sets the insets constraint for hte current object.
   * 
   * @param insets
   *        insets.
   * @return this object.
   */
  public final GridBagBuilder insets(Insets insets) {
    currentConstraints.insets = insets;
    return this;
  }

  /**
   * sets the insets constraint for hte current object.
   * 
   * @param top
   *        top inset.
   * @param left
   *        left inset.
   * @param bottom
   *        bottom inset.
   * @param right
   *        right inset.
   * @return this object.
   */
  public final GridBagBuilder insets(int top, int left, int bottom, int right) {
    currentConstraints.insets = new Insets(top, left, bottom, right);
    return this;
  }

  /**
   * sets the ipadx constraint for the current object.
   * 
   * @param padx
   *        in pixels.
   * @return this object.
   */
  public final GridBagBuilder padx(int padx) {
    currentConstraints.ipadx = padx;
    return this;
  }

  /**
   * sets the pady constraint for the current object.
   * 
   * @param pady
   *        in pixels.
   * @return this object.
   */
  public final GridBagBuilder pady(int pady) {
    currentConstraints.ipady = pady;
    return this;
  }

  /**
   * sets the ipadx constraint for the current object.
   * 
   * @param padx
   *        in pixels.
   * @return this object.
   */
  public final GridBagBuilder ipadx(int padx) {
    return padx(padx);
  }

  /**
   * sets the ipady constraint for the current object.
   * 
   * @param pady
   *        in pixels.
   * @return this object.
   */
  public final GridBagBuilder ipady(int pady) {
    return pady(pady);
  }

  /**
   * sets the weightx constraint for the current object.
   * 
   * @param wx
   *        weightx value 0.0 - 1.0
   * @return this object.
   */
  public final GridBagBuilder weightx(double wx) {
    currentConstraints.weightx = wx;
    return this;
  }

  /**
   * sets the weighty constraint for the current object.
   * 
   * @param wy
   *        weighty value 0.0 - 1.0
   * @return this object.
   */
  public final GridBagBuilder weighty(double wy) {
    currentConstraints.weighty = wy;
    return this;
  }

  /**
   * sets the anchorConstant constraint for the current object.
   * 
   * @param anchorConstant
   *        see GridBagConstraint.
   * @return this object.
   */
  public final GridBagBuilder anchor(int anchorConstant) {
    currentConstraints.anchor = anchorConstant;
    return this;
  }
}
