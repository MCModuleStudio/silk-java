package org.mcmodule.silk;

public class Decoder extends Struct {

	public Decoder() {
		super();
		long memory = getMemory();
		Native.initDecoder(memory);
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
		return Native.decode(getMemory(), decControl.getMemory(), false, in, off, len);
	}
	
	public short[] decode(byte[] in, int off, int len, DecoderControl decControl, boolean lostFlag) {
		return Native.decode(getMemory(), decControl.getMemory(), lostFlag, in, off, len);
	}
}