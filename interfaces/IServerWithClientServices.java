package interfaces;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;

import multimedia.Audio;

public interface IServerWithClientServices {

    void recieveAudio(Audio audio) ;

}
