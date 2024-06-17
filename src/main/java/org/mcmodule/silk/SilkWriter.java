package org.mcmodule.silk;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.spi.AudioFileWriter;

public class SilkWriter extends AudioFileWriter {

	public static final Type STANDARD_SILK = new Type("Standard silk", "bit");
	public static final Type TENCENT_SILK = new Type("Tencent silk", "slk");
	
	@Override
	public Type[] getAudioFileTypes() {
		return new Type[] {STANDARD_SILK, TENCENT_SILK};
	}

	@Override
	public Type[] getAudioFileTypes(AudioInputStream stream) {
		AudioFormat format = stream.getFormat();
		if (format.getChannels() != 1 || format.getSampleSizeInBits() != 16 || format.getFrameSize() != 2) {
			return new Type[0];
		}
		return getAudioFileTypes();
	}

	@Override
	public int write(AudioInputStream stream, Type fileType, OutputStream out) throws IOException {
		assert fileType == STANDARD_SILK || fileType == TENCENT_SILK;
		AudioFormat format = stream.getFormat();
		boolean bigEndian = format.isBigEndian();
		int bytesWritten = 0;
		DataOutputStream output;
		if (out instanceof DataOutputStream) {
			output = (DataOutputStream) out;
		} else {
			output = new DataOutputStream(out);
		}
		if (fileType == TENCENT_SILK) {
			bytesWritten += 1;
			output.write(0x02);
		}
		output.write("#!SILK_V3".getBytes());
		bytesWritten += "#!SILK_V3".length();
		EncoderControl encControl = new EncoderControl();
		Encoder encoder = new Encoder();
		encControl.setSampleRate((int) format.getSampleRate());
		encControl.setMaxInternalSampleRate(24000);
		int sampleCount = (encControl.getSampleRate() * 20) / 1000;
		encControl.setPacketSize(sampleCount);
		encControl.setPacketLossPercentage(0);
		encControl.setInBandFECEnabled(false);
		encControl.setDTXEnabled(false);
		encControl.setComplexity(2);
		encControl.setBitRate(25000);
		short[] shortBuffer = new short[sampleCount];
		byte[] byteBuffer = new byte[sampleCount * 2];
		int bytesRead;
		do {
			Arrays.fill(shortBuffer, (short) 0);
			bytesRead = stream.read(byteBuffer);
			if (bytesRead == 0 && fileType != TENCENT_SILK) {
				break;
			}
			toShort(shortBuffer, byteBuffer, bytesRead, bigEndian);
			byte[] encoded = encoder.encode(shortBuffer, encControl);
			output.writeShort(Short.reverseBytes((short) encoded.length));
			int len = fileType != TENCENT_SILK || bytesRead == byteBuffer.length ? encoded.length : encoded.length - 1;
			output.write(encoded, 0, len);
			bytesWritten += 2 + len;
		} while (bytesRead >= byteBuffer.length);
		if (fileType != TENCENT_SILK) {
			output.writeShort(-1);
			bytesWritten += 2;
		}
		return bytesWritten;
	}

	@Override
	public int write(AudioInputStream stream, Type fileType, File out) throws IOException {
		try (FileOutputStream output = new FileOutputStream(out)) {
			return write(stream, fileType, output);
		}
	}
	
	private void toShort(short[] shortBuffer, byte[] byteBuffer, int length, boolean bigEndian) {
		for (int i = 0; i < length; i += 2) {
			shortBuffer[i >> 1] = (short) (bigEndian ? (byteBuffer[i + 1] & 0xFF) | ((byteBuffer[i + 0] & 0xFF) << 8) : (byteBuffer[i + 0] & 0xFF) | ((byteBuffer[i + 1] & 0xFF) << 8));
		}
	}

}
