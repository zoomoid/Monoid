package tests.sounds;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Panner;
import synth.osc.SmartOscillator;
import tests.ContextProvider;

public class Supersaw {
    public static void main(String[] args){
        AudioContext ac = ContextProvider.ac();
        ac.start();
        SmartOscillator osc = new SmartOscillator(ac);
        // set the pad specific parameters
        osc.setup();
        osc.start();
        osc.setFrequency(220f);
        osc.setBlend(1);
        osc.setWave(Buffer.SAW);
        osc.setVoices(7);
        osc.setSpread(0.25f);
        osc.output().setGain(0.25f);
        ac.out.addInput(osc);
    }
}
