package tests.ui;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import synth.modulation.Envelope;
import synth.modulation.Static;
import synth.osc.BasicOscillator;
import synth.ui.composition.EnvelopePanel;
import tests.ContextProvider;

import javax.swing.*;

public class EnvelopePanelTest {
    JPanel pane;
    EnvelopePanel p;
    public EnvelopePanelTest(){
        AudioContext ac = ContextProvider.ac();
        ac.start();
        Envelope e = new Envelope(ac);
        BasicOscillator osc = new BasicOscillator(ac, new Static(ac, 220f), Buffer.SAW);
        osc.setGain(e);
        pane = new JPanel();
        p = new EnvelopePanel(e);
        pane.add(p);
        p.test();
        ac.start();
        ac.out.addInput(osc);
        osc.noteOn();
    }

    public static void main(String[] args){
        JFrame frame = new JFrame("EnvelopePanelTest");
        frame.setContentPane(new EnvelopePanelTest().pane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(true);
    }
}
