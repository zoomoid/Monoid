package tests.oscillators;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import synth.modulation.LFO;
import synth.osc.BasicOscillator;
import tests.ContextProvider;

public class BasisOscillatorTest {

    public static boolean STATIC_FREQUENCY = false;

    public static void main(String[] args){
        AudioContext ac = ContextProvider.ac();
        BasicOscillator osc = new BasicOscillator(ac);
        osc.setWave(Buffer.SINE);
        ac.out.addInput(osc);
        ac.start();
        ac.out.setGain(0.1f);
        if(STATIC_FREQUENCY){
            osc.setFrequency(220f);
        } else {
            LFO freqLFO = new LFO(ac, LFO.Type.SINE, 2f, 1f);
            freqLFO.start();
            // modulator specific sets
            freqLFO.setCenterValue(220f);
            freqLFO.setRange(20f);
            osc.setFrequency(freqLFO);
        }
    }
}
