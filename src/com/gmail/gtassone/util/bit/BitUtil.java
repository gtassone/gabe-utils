package com.gmail.gtassone.util.bit;

public class BitUtil {

	private BitUtil() {
	}

	/**
	 * since this is bitwise OR, maybe it shouldn't be called AND?
	 * 
	 * @param masks
	 * @return
	 */
	public static <T extends BitMask> BitMask combine(T... masks) {
		int combinedMask = 0;
		for (BitMask mask : masks) {
			combinedMask |= mask.mask();
		}
		return (new BitMask() {
			private int maskBits = 0;

			private BitMask setMaskValue(int maskValue) {
				this.maskBits = maskValue;
				return this;
			}

			@Override
			public int mask() {
				return maskBits;
			}
		}).setMaskValue(combinedMask);
	}
}
