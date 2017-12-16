package tests;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.IOAudioFormat;
import org.jaudiolibs.beads.AudioServerIO;

public class ContextProvider {
    public static AudioContext ac(){
        return new AudioContext(new AudioServerIO.JavaSound("Primärer Soundtreiber"), 2048, new IOAudioFormat(48000, 16, 0, 2));
    }
}
