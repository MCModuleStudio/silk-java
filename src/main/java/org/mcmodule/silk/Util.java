package org.mcmodule.silk;

class Util {
	private Util() {}
	
	public static void rangeCheck(int off, int len, int realLen) {
		if (off + len > realLen) {
			throw new ArrayIndexOutOfBoundsException("off + len > array length");
		}
	}
}
