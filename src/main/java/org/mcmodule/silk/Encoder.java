package org.mcmodule.silk;

public class Encoder extends Struct {

	public Encoder() {
		super();
		long memory = getMemory();
		Native.initEncoder(memory, 0L);
	}
	
	public Encoder(EncoderControl encStatus) {
		super();
		long memory = getMemory();
		Native.initEncoder(memory, encStatus.getMemory());
	}
	
	@Override
	protected int getStructSize() {
		return Native.getEncoderSize();
	}
	
	public byte[] encode(short[] samples, EncoderControl encControl) {
		return Native.encode(getMemory(), encControl.getMemory(), samples, 0, samples.length);
	}
	
	public byte[] encode(short[] samples, int off, int len, EncoderControl encControl) {
		Util.rangeCheck(off, len, samples.length);
		return Native.encode(getMemory(), encControl.getMemory(), samples, off, len);
	}

}
