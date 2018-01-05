package tests.oscillators;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.WavePlayer;
import synth.modulation.LFO;
import tests.ContextProvider;

public class WaveplayerTest {
    public static boolean STATIC_FREQUENCY = false;

    public static void main(String[] args){
        AudioContext ac = ContextProvider.ac();
        WavePlayer osc = new WavePlayer(ac, 0f, Buffer.SAW);
        ac.out.addInput(osc);
        ac.start();
        ac.out.setGain(0.1f);
        if(STATIC_FREQUENCY){
            osc.setFrequency(222f);
        } else {
            LFO freqLFO = new LFO(ac, LFO.Type.SINE, LFO.Mode.FREE,2f, 1);
            // modulator specific sets
            freqLFO.setCenterValue(144f);
            freqLFO.setModulationStrength(20f);
            osc.setFrequency(freqLFO);
        }
    }
}
