package tests.sounds;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import synth.osc.AdditiveOscillator;
import synth.ui.experimental.AdditiveUI;
import synth.auxilliary.ContextProvider;

import javax.swing.*;

public class AddSynthTest {
    public static AudioContext ac = ContextProvider.ac();
    public static void main(String[] args) {
        AdditiveOscillator osc = new AdditiveOscillator(ac, 220f, 5, Buffer.SINE);
        //Gain g = new Gain(ac, 1, 0.5f);
        ac.out.addInput(osc);
        ac.start();

        JFrame frame = new JFrame("Additive Synthesis");
        frame.setContentPane(new AdditiveUI(osc).contentPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
