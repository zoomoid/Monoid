package synth.auxilliary;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.IOAudioFormat;
import org.jaudiolibs.beads.AudioServerIO;
import synth.auxilliary.ASIOAudioContext;

public class ContextProvider {
    public static AudioContext ac(){
        return new AdvancedAudioContext(new AudioServerIO.JavaSound("Primärer Soundtreiber"), 8192, new IOAudioFormat(48000, 24, 0, 2));
    }
}
