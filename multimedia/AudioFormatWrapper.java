package multimedia;

import java.io.Serializable;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

public class AudioFormatWrapper implements Serializable{
    
    private String encoding;
    private float sampleRate;
    private int sampleSizeInBits;
    private int channels;
    private int frameSize;
    private float frameRate;
    private  boolean bigEndian;

public AudioFormatWrapper(Encoding encoding, float sampleRate, int sampleSizeInBits, int channels, int frameSize,
    float frameRate, boolean bigEndian) {
this.encoding = encoding.toString();
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

        Encoding encoding;

        if (this.encoding.equals(Encoding.PCM_SIGNED.toString())) {
            encoding = Encoding.PCM_SIGNED;
        } else if (this.encoding.equals(Encoding.PCM_UNSIGNED.toString())) {
            encoding = Encoding.PCM_UNSIGNED;
        } else if (this.encoding.equals(Encoding.ULAW.toString())) {
            encoding = Encoding.ULAW;
        } else if (this.encoding.equals(Encoding.ALAW.toString())) {
            encoding = Encoding.ALAW;
        } else {
            throw new IllegalArgumentException("Unknown encoding: " + this.encoding);
        }

        return new AudioFormat(encoding, sampleRate, sampleSizeInBits, channels, frameSize, frameRate, bigEndian);
    }
    @Override
    public String toString() {
        return "AudioFormatWrapper{" +
                "encoding='" + encoding + '\'' +
                ", sampleRate=" + sampleRate +
                ", sampleSizeInBits=" + sampleSizeInBits +
                ", channels=" + channels +
                ", frameSize=" + frameSize +
                ", frameRate=" + frameRate +
                ", bigEndian=" + bigEndian +
                '}';
    }

}
