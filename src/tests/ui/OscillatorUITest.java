package tests.ui;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.data.Pitch;
import net.beadsproject.beads.ugens.RangeLimiter;
import synth.osc.SmartOscillator;
import synth.ui.OscillatorUI;
import synth.auxilliary.ContextProvider;

public class OscillatorUITest {
    public static void main(String[] args){
        AudioContext ac = ContextProvider.ac();
        ac.start();
        SmartOscillator osc = new SmartOscillator(ac);
        osc.setFrequency(Pitch.mtof(48));
        osc.setBlend(1);
        osc.setSpread(0);
        osc.setVoices(1);
        osc.setWave(Buffer.SINE);

        RangeLimiter l = new RangeLimiter(ac,1);
        l.addInput(osc);
        ac.out.addInput(l);

        OscillatorUI oUI = new OscillatorUI(osc);
        oUI.show();
    }
}
