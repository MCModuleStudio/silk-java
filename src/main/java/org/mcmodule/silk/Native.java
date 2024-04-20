package org.mcmodule.silk;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public class Native {

	private static final Unsafe UNSAFE;

	public static Unsafe getUnsafe() {
		return UNSAFE;
	}
	
	public static native String getVersion();
	
	/* ******** Package private natives ******** */
	/**
	 * Get size in bytes of the Silk encoder state
	 * */
	static native int getEncoderSize();
	
	/**
	 * Init or reset encoder
	 * */
	static native void initEncoder(long encState, long encStatus);
	
	/**
	 * Read control structure from encoder
	 * */
	static native void queryEncoder(long encState, long encStatus);
	
	/**
	 * Encode frame with Silk
	 * */
	static native byte[] encode(long encState, long encControl, short[] samplesIn, int off, int len);
	
	/**
	 * Get size in bytes of the Silk decoder state
	 * */
	static native int getDecoderSize();
	
	/**
	 * Init or Reset decoder
	 * */
	static native void initDecoder(long decState);
	
	/**
	 * Decode a frame
	 * */
	static native short[] decode(long decState, long decControl, boolean lostFlag, byte[] inData, int off, int len);
	
	/**
	 * Decode a frame
	 * */
	static native short[] decodeRaw(long decState, long decControl, boolean lostFlag, byte[] inData, int off, int len);
	
	/**
	 * Find Low Bit Rate Redundancy (LBRR) information in a packet
	 * */
	static native byte[] searchForLBRR(byte[] inData, int off, int len, int lostOffset);
	/* ***************************************** */

	static {
		Unsafe unsafe;
		try {
			Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafeField.setAccessible(true);
			unsafe = (Unsafe) theUnsafeField.get(null);
		} catch(Throwable t) {
			throw new Error("Unable to get unsafe", t);
		}
		UNSAFE = unsafe;
		System.loadLibrary("silk-java");
	}
}
