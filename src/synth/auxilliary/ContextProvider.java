package synth.auxilliary;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.IOAudioFormat;
import org.jaudiolibs.beads.AudioServerIO;
import synth.auxilliary.ASIOAudioContext;

public class ContextProvider {
    public static AudioContext ac(){
        return new AudioContext(new AudioServerIO.JavaSound("Primärer Soundtreiber"), 4096, new IOAudioFormat(44100, 16, 0, 2));
    }
}
