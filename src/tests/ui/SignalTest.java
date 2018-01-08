package tests.ui;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.RangeLimiter;
import synth.auxilliary.SignalProcessor;
import synth.osc.BasicOscillator;
import synth.ui.components.Signal;
import synth.auxilliary.ContextProvider;

public class SignalTest {

    public static void main(String[] args){
        AudioContext ac = ContextProvider.ac();

        BasicOscillator osc = new BasicOscillator(ac, 500, Buffer.SINE);

        SignalProcessor pc = new SignalProcessor(ac);
        pc.addInput(osc);

        RangeLimiter l = new RangeLimiter(ac, 2);
        l.addInput(pc);

        ac.out.addInput(l);
        ac.start();

        Signal s = new Signal(ac);
    }
}
