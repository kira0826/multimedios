package multimedia;

import java.io.Serializable;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

public class AudioFormatWrapper implements Serializable{
    
    private AudioFormat.Encoding encoding;
    private float sampleRate;
    private int sampleSizeInBits;
    private int channels;
    private int frameSize;
    private float frameRate;
    private  boolean bigEndian;

public AudioFormatWrapper(Encoding encoding, float sampleRate, int sampleSizeInBits, int channels, int frameSize,
    float frameRate, boolean bigEndian) {
this.encoding = encoding;
this.sampleRate = sampleRate;
this.sampleSizeInBits = sampleSizeInBits;
this.channels = channels;
this.frameSize = frameSize;
this.frameRate = frameRate;
this.bigEndian = bigEndian;


}

    public static AudioFormatWrapper fromAudioFormatToWrapper(AudioFormat audioFormat) {

        return new AudioFormatWrapper(audioFormat.getEncoding(), audioFormat.getSampleRate(), 
        audioFormat.getSampleSizeInBits(), audioFormat.getChannels(),
        audioFormat.getFrameSize(), audioFormat.getFrameRate(), audioFormat.isBigEndian());

    }

    public AudioFormat toAudioFormat() {
        return new AudioFormat(encoding, sampleRate, sampleSizeInBits, channels, frameSize, frameRate, bigEndian);
    }
    

}
