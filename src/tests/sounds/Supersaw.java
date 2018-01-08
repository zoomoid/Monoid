package tests.sounds;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import synth.osc.SmartOscillator;
import synth.auxilliary.ContextProvider;

public class Supersaw {
    public static void main(String[] args){
        AudioContext ac = ContextProvider.ac();
        ac.start();
        SmartOscillator osc = new SmartOscillator(ac);
        // set the pad specific parameters
        osc.setFrequency(110f);
        osc.setBlend(1);
        osc.setWave(Buffer.SAW);
        osc.setVoices(7);
        osc.setSpread(5f);
        osc.setGain(0.25f);
        ac.out.addInput(osc);
    }
}
