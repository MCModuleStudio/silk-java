package org.mcmodule.silk;

public class EncoderControl extends Struct {

	public EncoderControl() {
		super();
		setSampleRate(24000);
	}
	
	public int getSampleRate() {
		return getInteger(0);
	}
	
	public void setSampleRate(int sampleRate) {
		setInteger(0, sampleRate);
	}
	
	public int getMaxInternalSampleRate() {
		return getInteger(4);
	}
	
	public void setMaxInternalSampleRate(int sampleRate) {
		setInteger(4, sampleRate);
	}
	
	public int getPacketSize() {
		return getInteger(8);
	}
	
	public void setPacketSize(int packetSize) {
		setInteger(8, packetSize);
	}
	
	public int getBitRate() {
		return getInteger(12);
	}
	
	public void setBitRate(int bitRate) {
		setInteger(12, bitRate);
	}
	
	public int getPacketLossPercentage() {
		return getInteger(16);
	}
	
	public void setPacketLossPercentage(int packetLossPercentage) {
		setInteger(16, packetLossPercentage);
	}
	
	public int getComplexity() {
		return getInteger(20);
	}
	
	public void setComplexity(int complexity) {
		setInteger(20, complexity);
	}
	
	public boolean isInBandFECEnabled() {
		return getInteger(24) == 1;
	}
	
	public void setInBandFECEnabled(boolean state) {
		setInteger(24, state ? 1 : 0);
	}
	
	public boolean isDTXEnabled() {
		return getInteger(28) == 1;
	}
	
	public void setDTXEnabled(boolean state) {
		setInteger(28, state ? 1 : 0);
	}
	
	@Override
	protected int getStructSize() {
		return 32;
	}

}
