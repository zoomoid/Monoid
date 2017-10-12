package tests;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.IOAudioFormat;
import net.beadsproject.beads.core.io.JavaSoundAudioIO;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.WavePlayer;

import net.beadsproject.beads.ugens.*;
import org.jaudiolibs.beads.AudioServerIO;
import synth.auxilliary.ASIOAudioContext;

public class FMOscillator {

    AudioContext ac;

    WavePlayer wave1, wave2;
    WavePlayer lfo1, lfo2, lfo3;

    Envelope gainEnvelope;
    Gain vca1, vca2, vca_master;

    BiquadFilter lpf;

    public FMOscillator(){

    }

    public void setup(float freq){
        AudioContext ac = new AudioContext(new AudioServerIO.Jack(), 2048, new IOAudioFormat(44100, 16, 2, 2));

        wave1 = new WavePlayer(ac, freq, Buffer.SAW);
        wave2 = new WavePlayer(ac, freq/4, Buffer.SQUARE);
        lfo1 = new WavePlayer(ac, 0.5f, Buffer.SINE);
        lfo2 = new WavePlayer(ac, 2.0f, Buffer.SINE);
        lfo3 = new WavePlayer(ac, 4.0f, Buffer.SINE);
        Function lfo1State = new Function(lfo1){
            @Override
            public float calculate(){
                return 0.5f * (x[0]);
            }
        };

        Function lfo2State = new Function(lfo2){
            @Override
            public float calculate(){
                return 0.33f * (x[0]);
            }
        };

        Function lfo3State = new Function(lfo3){
            @Override
            public float calculate(){
                return (x[0] * 300.0f) + 1000.0f;
            }
        };

        lpf = new BiquadFilter(ac, BiquadFilter.BESSEL_LP, lfo3State, 48);

        lpf.addInput(wave2);



        Gain vca1 = new Gain(ac, 1, lfo1State);
        vca1.addInput(wave1);

        vca2 = new Gain(ac, 1, lfo2State);
        vca2.addInput(lpf);

        vca_master = new Gain(ac, 1, 1.0f);
        //vca_master.addInput(vca1);
        vca_master.addInput(vca2);
        ac.out.addInput(vca_master);
        ac.start();
    }

    public static void main(String args[]){
        FMOscillator synth = new FMOscillator();
        synth.setup(220.0f);
    }
}
