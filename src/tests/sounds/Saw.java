package tests.sounds;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.IOAudioFormat;
import net.beadsproject.beads.core.io.JavaSoundAudioIO;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.WavePlayer;

import net.beadsproject.beads.ugens.*;
import org.jaudiolibs.beads.AudioServerIO;

public class Saw {
    public Saw(){

    }

    public static void main(String[] args){
        AudioContext ac = new AudioContext(new AudioServerIO.JavaSound("Primärer Soundtreiber"), 4096, new IOAudioFormat(48000, 24, 0, 2));



        Envelope glide = new Envelope(ac);
        glide.addSegment(1f, 5000);

        Function sweep = new Function(glide) {
            @Override
            public float calculate() {
                return x[0] * 880f + 88f;
            }
        };

        Phasor saw = new Phasor(ac,sweep);

        Gain g = new Gain(ac,1,0.5f);

        g.addInput(saw);

        ac.out.addInput(g);
        ac.start();
    }
}
