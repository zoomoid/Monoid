package tests.sounds;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.RangeLimiter;
import synth.filter.Filter;
import synth.filter.models.BiquadFilter;
import synth.osc.SmartOscillator;
import synth.ui.FilterUI;
import tests.ContextProvider;

import javax.swing.*;

public class Pad {
    public static void main(String[] args){
        AudioContext ac = ContextProvider.ac();
        ac.start();
        SmartOscillator osc = new SmartOscillator(ac);
        // set the pad specific parameters
        osc.setup();
        osc.start();
        osc.setFrequency(55f);
        osc.setBlend(1);
        osc.setWave(Buffer.SAW);
        osc.setVoices(7);
        osc.setSpread(0.5f);
        osc.output().setGain(0.2f);

        Filter lp = new Filter(ac, BiquadFilter.BUTTERWORTH_LP, 20f, 24f, 0.1f);
        lp.addInput(osc);

        RangeLimiter l = new RangeLimiter(ac, 2);
        l.addInput(lp);

        ac.out.addInput(l);



        JFrame frame = new JFrame("Filter A");
        frame.setContentPane(new FilterUI(lp).pane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
    }
}
