import java.util.Arrays;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

import org.mcmodule.silk.Decoder;
import org.mcmodule.silk.DecoderControl;
import org.mcmodule.silk.Encoder;
import org.mcmodule.silk.EncoderControl;
import org.mcmodule.silk.Native;

public class EncoderTest3 {

	private static final int SAMPLES_MS = 20 * 3;
	
	public static void main(String[] args) throws Throwable {
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
		Decoder decoder = new Decoder();
		DecoderControl decControl = new DecoderControl();
		decControl.setSampleRate(48000);
		short[] samples = new short[sampleCount];
		byte[] samplesByte = new byte[sampleCount * 2];
		int len;
		int encodedLength = 0, encodedLength2 = 0;
		int samplesLength = 0, samplesLength2 = 0;
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new AudioFormat(Encoding.PCM_SIGNED, 48000, 16, 1, 2, 48000, false), AudioSystem.getAudioInputStream(EncoderTest3.class.getResource("/Test.wav")));
		SourceDataLine sourceDataLine = AudioSystem.getSourceDataLine(new AudioFormat(Encoding.PCM_SIGNED, 48000, 16, 1, 2, 48000, false));
		sourceDataLine.open();
		sourceDataLine.start();
		long seed = new Random().nextLong();
		System.out.printf("Seed: 0x%016X\n", seed);
		Random rand = new Random(seed);
		boolean lost = false;
		do {
			len = audioInputStream.read(samplesByte) / 2;
			Arrays.fill(samples, (short) 0);
			for (int i = 0; i < len; i++) {
				samples[i] = (short) ((samplesByte[(i << 1) + 0] & 0xFF) | ((samplesByte[(i << 1) + 1] & 0xFF) << 8));
			}
			byte[] encodedSamples = encoder.encode(samples, encControl);
//			System.out.printf("Encoded data -> %s\n", Arrays.toString(encodedSamples));
			if (rand.nextInt(100) <= 5) {
//				lost = true;
				System.out.println("Simulate packet loss!");
				continue;
			}
			encodedLength += encodedSamples.length;
			encodedLength2 += encodedSamples.length;
			short[] decodedSamples = decoder.decode(encodedSamples, decControl, lost);
//			System.out.printf("Decoded samples (%d/%d) -> %s\n", decodedSamples.length, sampleCount, Arrays.toString(decodedSamples));
			for (int i = 0; i < decodedSamples.length; i++) {
				samplesByte[(i << 1) + 0] = (byte) ( decodedSamples[i] & 0xFF);
				samplesByte[(i << 1) + 1] = (byte) ((decodedSamples[i] >>> 8) & 0xFF);
			}
			sourceDataLine.write(samplesByte, 0, decodedSamples.length * 2);
			samplesLength += decodedSamples.length;
			samplesLength2 += decodedSamples.length;
			if (samplesLength2 >= 48000) {
				System.out.printf("Bitrate: %.2f kBps\n", (encodedLength2 * 8) / (samplesLength2 / 48000f) / 1000f);
				encodedLength2 = 0;
				samplesLength2 = 0;
				int bitRate = 6000 + (rand.nextInt(95) * 1000);
				System.out.printf("Change bit rate to %d\n", bitRate);
				encControl.setBitRate(bitRate);
			}
			lost = false;
		} while (len > 0);
		System.out.printf("Total encoded bytes: %d Average bitrate: %.2f kBps\n", encodedLength, (encodedLength * 8) / (samplesLength / 48000f) / 1000f);
	}

}
