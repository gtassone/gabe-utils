package com.gmail.gtassone.util.bit;

public class BitPosMask implements BitMask {

	private int mask = 0;

	public BitPosMask(int... positions) {
		mask = 0;
		for (int pos : positions) {
			mask |= (int) Math.pow(2, pos);
		}
	}

	@Override
	public int mask() {
		return mask;
	}

}
