package multimedia;

import java.util.Queue;

public class Audio extends Multimedia {

    private Queue <byte[]> queue;
    private AudioFormatWrapper audioFormatWrapper;


    public Audio(Queue<byte[]> queue, AudioFormatWrapper audioFormatWrapper) {
        this.queue = queue;
        this.audioFormatWrapper = audioFormatWrapper;
    }


    public Queue<byte[]> getQueue() {
        return queue;
    }


    public AudioFormatWrapper getAudioFormatWrapper() {
        return audioFormatWrapper;
    }
    
    
}
