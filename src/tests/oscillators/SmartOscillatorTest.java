package tests.oscillators;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import synth.modulation.LFO;
import synth.osc.SmartOscillator;
import tests.ContextProvider;

public class SmartOscillatorTest {

    public static boolean STATIC_FREQUENCY = true;
    public static boolean UNISON = true;

    public static void main(String[] args){
        AudioContext ac = ContextProvider.ac();
        SmartOscillator osc = new SmartOscillator(ac);
        osc.setWave(Buffer.SAW);
        ac.out.addInput(osc);
        ac.start();
        ac.out.setGain(0.1f);
        if(STATIC_FREQUENCY){
            osc.setFrequency(220f);
        } else {
            LFO freqLFO = new LFO(ac, LFO.Type.SINE, 2f, 1f);
            // modulator specific sets
            freqLFO.setCenterValue(220f).setRange(20f);
            osc.setFrequency(freqLFO);
        }

        if(UNISON){
            osc.setVoices(5).setBlend(1).setSpread(2).setSpreadFunction(2);
        } else {
            osc.setVoices(1).setBlend(1).setSpread(0).setSpreadFunction(1);
        }
    }
}
