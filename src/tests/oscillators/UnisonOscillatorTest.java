package tests.oscillators;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.RangeLimiter;
import synth.modulation.LFO;
import synth.modulation.Modulatable;
import synth.modulation.Static;
import synth.osc.SmartOscillator;
import synth.osc.MultivoiceOscillator;
import tests.ContextProvider;

public class UnisonOscillatorTest {
    public static void main(String[] args){
        AudioContext ac = ContextProvider.ac();
        MultivoiceOscillator osc = new MultivoiceOscillator(ac, Buffer.SAW, 6);

        Modulatable[] frequencies = SmartOscillator.calculateUnisonPitch(6, new Static(ac, 110f), 1, 0.5f);
        osc.setPhase(-1);

        osc.setFrequencies(frequencies);

        RangeLimiter l = new RangeLimiter(ac, 2);
        l.addInput(osc);
        ac.start();
        ac.out.addInput(l);
    }
}
