package synth.auxilliary;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.IOAudioFormat;
import org.jaudiolibs.beads.AudioServerIO;

public class ASIOAudioContext extends AudioContext {

    public ASIOAudioContext(){
        super(new AudioServerIO.JavaSound("Komplete Audio 6 WDM Audio"), 192, new IOAudioFormat(44100, 16, 0, 2));
    }

    public ASIOAudioContext(int bufferSize){
        super(new AudioServerIO.JavaSound("Komplete Audio 6 WDM Audio"), bufferSize, new IOAudioFormat(44100, 16, 0, 2));
    }

    public ASIOAudioContext(String device){
        super(new AudioServerIO.JavaSound(device), 192, new IOAudioFormat(44100, 16, 0, 2));
    }

    public ASIOAudioContext(String device, int bufferSize){
        super(new AudioServerIO.JavaSound(device), bufferSize, new IOAudioFormat(44100, 16, 0, 2));
    }
}
