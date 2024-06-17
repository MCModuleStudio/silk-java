package org.mcmodule.silk;

public class Decoder extends Struct {

	public Decoder() {
		super();
		reset();
	}
	
	@Override
	protected int getStructSize() {
		return Native.getDecoderSize();
	}
	
	public short[] decode(byte[] in, DecoderControl decControl) {
		return Native.decode(getMemory(), decControl.getMemory(), false, in, 0, in.length);
	}
	
	public short[] decode(byte[] in, DecoderControl decControl, boolean lostFlag) {
		return Native.decode(getMemory(), decControl.getMemory(), lostFlag, in, 0, in.length);
	}
	
	public short[] decode(byte[] in, int off, int len, DecoderControl decControl) {
		Util.rangeCheck(off, len, in.length);
		return Native.decode(getMemory(), decControl.getMemory(), false, in, off, len);
	}
	
	public short[] decode(byte[] in, int off, int len, DecoderControl decControl, boolean lostFlag) {
		Util.rangeCheck(off, len, in.length);
		return Native.decode(getMemory(), decControl.getMemory(), lostFlag, in, off, len);
	}
	
	public static byte[] searchForLBRR(byte[] inData, int off, int len, int lostOffset) {
		Util.rangeCheck(off, len, inData.length);
		return Native.searchForLBRR(inData, off, len, lostOffset);
	}
	
	public void reset() {
		long memory = getMemory();
		Native.initDecoder(memory);
	}
}
