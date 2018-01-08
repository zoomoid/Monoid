package tests.ui;

import net.beadsproject.beads.core.AudioContext;
import synth.auxilliary.ContextProvider;
import synth.ui.AdditiveUI;
import synth.ui.AdditiveUIProvider;
import synth.ui.components.swing.BlankButton;
import synth.ui.components.swing.BlankPanel;
import synth.ui.components.swing.BlankTextfield;

import javax.swing.*;
import java.awt.*;

public class AddSynthTest {


    public static void main(String[] args) {
        AudioContext ac = ContextProvider.ac();
        ac.start();

        JFrame frame;
        frame = new JFrame("Additive Synthesis");

        AdditiveUIProvider ui = new AdditiveUIProvider(ac, frame);

        frame.setContentPane(ui.mainPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.pack();
        frame.setVisible(true);
    }
}
