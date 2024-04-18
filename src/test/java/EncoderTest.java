import java.util.Arrays;

import org.mcmodule.silk.Decoder;
import org.mcmodule.silk.DecoderControl;
import org.mcmodule.silk.Encoder;
import org.mcmodule.silk.EncoderControl;
import org.mcmodule.silk.Native;

public class EncoderTest {

	private static final int SAMPLES_MS = 20 * 3;
	
	public static void main(String[] args) {
		System.out.println("Silk version: " + Native.getVersion());
		EncoderControl encControl = new EncoderControl();
		Encoder encoder = new Encoder(encControl);
		encControl.setSampleRate(48000);
		encControl.setMaxInternalSampleRate(24000);
		int sampleCount = (encControl.getSampleRate() * SAMPLES_MS) / 1000;
		encControl.setPacketSize(sampleCount);
		encControl.setPacketLossPercentage(0);
		encControl.setInBandFECEnabled(false);
		encControl.setDTXEnabled(false);
		encControl.setComplexity(2);
		encControl.setBitRate(25000);
		short[] samples = new short[sampleCount];
		byte[] encodedSamples = encoder.encode(samples, encControl);
		System.out.printf("Encoded data -> %s\n", Arrays.toString(encodedSamples));
		Decoder decoder = new Decoder();
		DecoderControl decControl = new DecoderControl();
		decControl.setSampleRate(48000);
		short[] decodedSamples = decoder.decode(encodedSamples, decControl);
		System.out.printf("Decoded samples (%d/%d) -> %s\n", decodedSamples.length, sampleCount, Arrays.toString(decodedSamples));
	}

}
