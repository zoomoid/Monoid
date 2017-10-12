package tests;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.IOAudioFormat;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Envelope;
import net.beadsproject.beads.ugens.WavePlayer;
import net.beadsproject.beads.core.io.JavaSoundAudioIO;

import net.beadsproject.beads.ugens.*;
import synth.auxilliary.ASIOAudioContext;

public class simpleSineOscillator {
    public static void main(String[] args){
        AudioContext ac = new ASIOAudioContext("Komplete Audio 6 WDM Audio", 4096);
        int count = 128;

        WavePlayer[] waves = new WavePlayer[count];
        Gain[] g = new Gain[count];

        for(int i = 0; i < count; i++){
            waves[i] = new WavePlayer(ac, 55.0f * (i+1), Buffer.SINE);
            g[i] = new Gain(ac, 1, 0.66f / (i+1));

            g[i].addInput(waves[i]);;
            ac.out.addInput(g[i]);
        }
         ac.start();

    }
}
