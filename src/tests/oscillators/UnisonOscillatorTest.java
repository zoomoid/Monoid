package tests.oscillators;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.RangeLimiter;
import net.beadsproject.beads.ugens.Static;
import synth.osc.SmartOscillator;
import synth.osc.UnisonOscillator;
import tests.ContextProvider;

public class UnisonOscillatorTest {
    public static void main(String[] args){
        AudioContext ac = ContextProvider.ac();
        UnisonOscillator osc = new UnisonOscillator(ac, Buffer.SAW, 5);

        float[] frequencies = SmartOscillator.calculateUnisonPitch(5, new Static(ac, 220f), 1, 0.001f);
        osc.setPhase(0);

        osc.setFrequencies(frequencies);

        ac.start();
        RangeLimiter l = new RangeLimiter(ac, 2);
        l.addInput(osc);
        ac.out.addInput(l);
    }
}
