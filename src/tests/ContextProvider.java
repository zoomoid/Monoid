package tests;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.IOAudioFormat;
import org.jaudiolibs.beads.AudioServerIO;

public class ContextProvider {
    public static AudioContext ac = new AudioContext(new AudioServerIO.JavaSound("Primärer Soundtreiber"), 16000, new IOAudioFormat(44100, 24, 0, 2));
}
