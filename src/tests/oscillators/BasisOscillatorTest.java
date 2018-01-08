package tests.oscillators;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import synth.modulation.LFO;
import synth.osc.BasicOscillator;
import synth.auxilliary.ContextProvider;

public class BasisOscillatorTest {

    public static boolean STATIC_FREQUENCY = true;

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
            LFO freqLFO = new LFO(ac, LFO.Type.SINE, LFO.Mode.FREE,2f, 1f);
            freqLFO.start();
            // modulator specific sets
            freqLFO.setCenterValue(220f);
            freqLFO.setModulationStrength(20f);
            osc.setFrequency(freqLFO);
        }
    }
}
