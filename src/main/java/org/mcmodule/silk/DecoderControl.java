package org.mcmodule.silk;

public class DecoderControl extends Struct {

	public DecoderControl() {
		super();
		setSampleRate(24000);
	}
	
	public int getSampleRate() {
		return getInteger(0);
	}
	
	public void setSampleRate(int sampleRate) {
		setInteger(0, sampleRate);
	}
	
	public int getFrameSize() {
		return getInteger(4);
	}
	
	public int getFramesPerPacket() {
		return getInteger(8);
	}
	
	public boolean isMoreInternalDecoderFrames() {
		return getInteger(12) != 0;
	}
	
	public int getInBandFECOffset() {
		return getInteger(16);
	}
	
	@Override
	protected int getStructSize() {
		return 20;
	}

}
