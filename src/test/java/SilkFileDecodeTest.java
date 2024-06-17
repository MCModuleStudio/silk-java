import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.mcmodule.silk.SilkReader;

public class SilkFileDecodeTest {

	public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		SilkReader reader = new SilkReader();
		File file = new File("output.slk");
		System.out.printf("Audio format: %s\n", reader.getAudioFileFormat(file));
		AudioInputStream audioInputStream = reader.getAudioInputStream(file);
		AudioFormat format = audioInputStream.getFormat();
		SourceDataLine sourceDataLine = AudioSystem.getSourceDataLine(format);
		sourceDataLine.open();
		sourceDataLine.start();
		int len;
		byte[] buffer = new byte[1024];
		do {
			len = audioInputStream.read(buffer);
			if (len < 0) {
				break;
			}
			sourceDataLine.write(buffer, 0, len);
		} while(len > 0);
	}

}
