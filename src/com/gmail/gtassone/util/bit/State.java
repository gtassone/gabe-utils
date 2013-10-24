package com.gmail.gtassone.util.bit;

import java.io.Serializable;

public class State<T extends BitMask> implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private int stateBits = 0;

  public void mark(T... masks) {
    for (T mask : masks) {
      stateBits |= mask.mask();
    }
  }

  public void unmark(T... masks) {
    for (T mask : masks) {
      stateBits &= (~mask.mask());
    }
  }

  public void flip(T... masks) {
    for (T mask : masks) {
      stateBits ^= mask.mask();
    }
  }

  public boolean is(T... masks) {
    return (stateBits == BitUtil.combine(masks).mask());
  }

  public boolean includes(T... masks) {
    BitMask combinedMask = BitUtil.combine(masks);
    return (stateBits & combinedMask.mask()) == combinedMask.mask();
  }
  
  public boolean excludes(T... masks) {
    BitMask combinedMask = BitUtil.combine(masks);
    return (stateBits & combinedMask.mask()) == 0;
  }

  
  public int bits() {
    return stateBits;
  }
}