package tests.oscillators;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import synth.modulation.LFO;
import synth.osc.SmartOscillator;
import synth.auxilliary.ContextProvider;
import synth.osc.Waveform;

public class SmartOscillatorTest {

    public static boolean STATIC_FREQUENCY = false;
    public static boolean UNISON = true;

    public static void main(String[] args){
        AudioContext ac = ContextProvider.ac();
        SmartOscillator osc = new SmartOscillator(ac);
        osc.setWave(Waveform.SAW);
        osc.setGain(0.5f);
        ac.out.addInput(osc);
        if(STATIC_FREQUENCY){
            osc.setFrequency(110f);
        } else {
            LFO freqLFO = new LFO(ac, LFO.Type.SINE, 2f, 1f);
            // modulator specific sets
            freqLFO.setCenterValue(220f);
            freqLFO.setModulationStrength(20f);
            osc.setFrequency(freqLFO);
        }
        if(UNISON){
            osc.setVoices(5);
            osc.setBlend(1.5f);
            osc.setSpread(0.05f);
            osc.setSpreadFunction(1);
            osc.setPhase(-1);
        } else {
            osc.setVoices(1).setBlend(1).setSpread(0).setSpreadFunction(1);
        }
        ac.start();
        osc.noteOn();


    }
}
