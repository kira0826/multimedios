package interfaces;

import javax.sound.sampled.AudioFormat;

import multimedia.Audio;

public interface IClientUtil {

    public void sendAudio(String username, Audio audio);
    public Audio recordAudio(AudioFormat audioFormat);

}
