package tests.sounds;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.RangeLimiter;
import synth.modulation.LFO;
import synth.osc.BasicOscillator;
import synth.osc.Oscillator;
import synth.ui.components.Signal;
import synth.auxilliary.ContextProvider;

public class ModulationTest {
    public static void main(String[] args){
        AudioContext ac = ContextProvider.ac();

        Oscillator osc = new BasicOscillator(ac, 500, Buffer.SQUARE);
        LFO volumeLFO = new LFO(ac, LFO.Type.SINE, LFO.Mode.FREE, 0.25f, 0.5f);

        osc.setGain(volumeLFO);
        osc.setGain(0.5f);
        osc.noteOn();

        Gain g = new Gain(ac, 2, 0.5f);
        g.addInput(osc);
        RangeLimiter l = new RangeLimiter(ac, 2);
        l.addInput(g);

        Signal s = new Signal(ac);
        ac.out.addInput(l);
        ac.start();
    }
}
