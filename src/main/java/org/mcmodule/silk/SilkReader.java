package org.mcmodule.silk;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;

public class SilkReader extends AudioFileReader {

	@Override
	public AudioFileFormat getAudioFileFormat(InputStream stream) throws UnsupportedAudioFileException, IOException {
		stream.mark(200);
		try {
			byte[] header = new byte[9];
			stream.read(header);
			if (header[0] != 0x02 && header[0] != 0x23) {
				throw new UnsupportedAudioFileException();
			}
			boolean tencent = header[0] == 0x02;
			if (tencent) {
				if (!Arrays.equals(Arrays.copyOfRange(header, 1, 9), "#!SILK_V".getBytes()) || stream.read() != 0x33) {
					throw new UnsupportedAudioFileException();
				}
			} else {
				if (!Arrays.equals(header, "#!SILK_V3".getBytes())) {
					throw new UnsupportedAudioFileException();
				}
			}
			DataInputStream input;
			if (stream instanceof DataInputStream) {
				input = (DataInputStream) stream;
			} else {
				input = new DataInputStream(stream);
			}
			int len = Short.reverseBytes(input.readShort());
			if (len <= 0) {
				throw new UnsupportedAudioFileException();
			}
			byte[] bytes = new byte[len];
			if (len != input.read(bytes)) {
				throw new UnsupportedAudioFileException();
			}
			Decoder decoder = new Decoder();
			DecoderControl decControl = new DecoderControl();
			int frameSize = decoder.decode(bytes, decControl).length << 1;
			int sampleRate = decControl.getSampleRate();
			int frames = 1;
			try {
				do {
					len = Short.reverseBytes(input.readShort());
					if (len <= 0 || input.skip(len) != len) {
						break;
					}
					frames++;
				} while (true);
			} catch(EOFException e) {}
			return new AudioFileFormat(tencent ? SilkWriter.TENCENT_SILK : SilkWriter.STANDARD_SILK, new AudioFormat(Encoding.PCM_SIGNED, sampleRate, 16, 1, 2, sampleRate, false), frames * frameSize);
		} finally {
			stream.reset();
		}
	}

	@Override
	public AudioFileFormat getAudioFileFormat(URL url) throws UnsupportedAudioFileException, IOException {
		try (InputStream is = new BufferedInputStream(url.openStream())) {
			return getAudioFileFormat(is);
		}
	}

	@Override
	public AudioFileFormat getAudioFileFormat(File file) throws UnsupportedAudioFileException, IOException {
		try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
			return getAudioFileFormat(is);
		}
	}

	@Override
	public AudioInputStream getAudioInputStream(InputStream stream) throws UnsupportedAudioFileException, IOException {
		byte[] header = new byte[9];
		stream.read(header);
		if (header[0] != 0x02 && header[0] != 0x23) {
			throw new UnsupportedAudioFileException();
		}
		boolean tencent = header[0] == 0x02;
		if (tencent) {
			if (!Arrays.equals(Arrays.copyOfRange(header, 1, 9), "#!SILK_V".getBytes()) || stream.read() != 0x33) {
				throw new UnsupportedAudioFileException();
			}
		} else {
			if (!Arrays.equals(header, "#!SILK_V3".getBytes())) {
				throw new UnsupportedAudioFileException();
			}
		}
		stream.mark(200);
		DataInputStream input;
		if (stream instanceof DataInputStream) {
			input = (DataInputStream) stream;
		} else {
			input = new DataInputStream(stream);
		}
		int len = Short.reverseBytes(input.readShort());
		if (len <= 0) {
			throw new UnsupportedAudioFileException();
		}
		byte[] bytes = new byte[len];
		if (len != input.read(bytes)) {
			throw new UnsupportedAudioFileException();
		}
		Decoder decoder = new Decoder();
		DecoderControl decControl = new DecoderControl();
		int frameSize = decoder.decode(bytes, decControl).length << 1;
		int sampleRate = decControl.getSampleRate();
		decoder.reset();
		stream.reset();
		return new AudioInputStream(new BufferedInputStream(new InputStream() {

			@Override
			public int read() throws IOException {
				throw new IOException("Can't read single byte");
			}
			
			public int read(byte b[], int off, int len) throws IOException {
				if (len < frameSize) {
					throw new IOException("Buffer size small than single frame");
				}
				try {
					len = Short.reverseBytes(input.readShort());
					if (len <= 0) {
						return -1;
					}
					byte[] bytes = new byte[len];
					if (len != input.read(bytes)) {
						return -1;
					}
					short[] decoded = decoder.decode(bytes, decControl);
					return toByte(b, off, decoded);
				} catch(EOFException e) {
					return -1;
				}
			}

			private int toByte(byte[] byteBuffer, int off, short[] shortBuffer) {
				for (int i = 0, len = shortBuffer.length; i < len; i++) {
					byteBuffer[off + (i << 1) + 0] = (byte) (shortBuffer[i] & 0xFF);
					byteBuffer[off + (i << 1) + 1] = (byte) ((shortBuffer[i] >>> 8) & 0xFF);
				}
				return shortBuffer.length << 1;
			}
		}, frameSize << 1), new AudioFormat(Encoding.PCM_SIGNED, sampleRate, 16, 1, 2, sampleRate, false), AudioSystem.NOT_SPECIFIED);
	}

	@Override
	public AudioInputStream getAudioInputStream(URL url) throws UnsupportedAudioFileException, IOException {
		return getAudioInputStream(new BufferedInputStream(url.openStream()));
	}

	@Override
	public AudioInputStream getAudioInputStream(File file) throws UnsupportedAudioFileException, IOException {
		return getAudioInputStream(new BufferedInputStream(new FileInputStream(file)));
	}

}
