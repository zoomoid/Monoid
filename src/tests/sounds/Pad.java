package tests.sounds;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.RangeLimiter;
import synth.filter.Filter;
import synth.filter.models.FilterModel;
import synth.osc.BasicOscillator;
import synth.osc.Waveform;
import synth.ui.FilterUI;
import synth.auxilliary.ContextProvider;

import javax.swing.*;

public class Pad {
    public static void main(String[] args){
        AudioContext ac = ContextProvider.ac();
        ac.start();
        BasicOscillator osc = new BasicOscillator(ac);
        // set the pad specific parameters
        osc.setFrequency(110f);
        osc.setWave(Waveform.SAW);
        osc.setGain(1f);

        Filter lp = new Filter(ac, FilterModel.Type.BiquadFilter, FilterModel.Mode.LPF, 22000f, 2, 1f);
        lp.addInput(osc);

        RangeLimiter l = new RangeLimiter(ac, 2);
        l.addInput(lp);

        ac.out.addInput(l);

        JFrame frame = new JFrame("Filter");
        frame.setContentPane(new FilterUI(lp).pane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
