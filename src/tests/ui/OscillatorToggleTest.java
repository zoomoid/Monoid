package tests.ui;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import synth.osc.BasicOscillator;
import synth.osc.SmartOscillator;
import synth.osc.Waveform;
import synth.ui.components.swing.OscillatorToggle;
import synth.auxilliary.ContextProvider;

import javax.swing.*;

public class OscillatorToggleTest {

    public static void main(String[] args){
        AudioContext ac = ContextProvider.ac();
        SmartOscillator smartOSC = new SmartOscillator(ac, 220f, Waveform.SAW, 3, 2f, 1);
        BasicOscillator basicOSC = new BasicOscillator(ac, 330f, Waveform.SAW);

        JPanel pane = new JPanel();

        OscillatorToggle smartToggle, basicToggle;

        smartOSC.setName("OSC 1");
        smartToggle = new OscillatorToggle(smartOSC);

        basicOSC.setName("OSC 2");
        basicToggle = new OscillatorToggle(basicOSC);

        pane.add(smartToggle);
        pane.add(basicToggle);

        ac.out.addInput(smartOSC);
        ac.out.addInput(basicOSC);
        ac.start();

        JFrame frame = new JFrame("OSC-ToggleTest");
        frame.setContentPane(pane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(true);
    }
}
