import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.mcmodule.silk.SilkWriter;

import javax.sound.sampled.AudioFormat.Encoding;

public class SilkFileEncodeTest {

	public static void main(String[] args) throws UnsupportedAudioFileException, IOException {
		System.out.printf("Providers: %s\n", Arrays.toString(AudioSystem.getAudioFileTypes()));
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new AudioFormat(Encoding.PCM_SIGNED, 48000, 16, 1, 2, 48000, false), AudioSystem.getAudioInputStream(SilkFileEncodeTest.class.getResource("/Test.wav")));
		new SilkWriter().write(audioInputStream, SilkWriter.STANDARD_SILK, new File("output.slk"));
	}

}
