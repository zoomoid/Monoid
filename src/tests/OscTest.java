package tests;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.IOAudioFormat;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Gain;
import org.jaudiolibs.beads.AudioServerIO;
import synth.osc.WaveOscillator;


public class OscTest {
    public static void main(String[] args){
        AudioContext ac = new AudioContext(new AudioServerIO.JavaSound("Primärer Soundtreiber"), 4096, new IOAudioFormat(44100, 24, 0, 2));

        WaveOscillator osc = new WaveOscillator(ac, 200f, Buffer.SAW);

        osc.setUnisonVoices(6);
        osc.setUnisonSpread(2f);

        Gain g = new Gain(ac, 1, 1f);
        g.addInput(osc.output());

        ac.out.addInput(g);


        ac.start();


        System.out.print(osc.isSingleVoiced());

        //osc.setWaveshape(Buffer.SINE);

    }
}
