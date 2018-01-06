package synth.ui.components;

import net.beadsproject.beads.core.AudioContext;
import synth.auxilliary.SignalProcessor;
import synth.ui.components.swing.BlankLabel;
import synth.ui.components.swing.BlankPanel;

import javax.swing.*;

public class Signal {
    private Canvas signal;
    private SignalProcessor pc;
    private JFrame signalUI;
    private BlankPanel signalPane;
    public Signal(AudioContext ac){
        signalPane = new BlankPanel();
        signalPane.setLayout(new BoxLayout(signalPane, BoxLayout.PAGE_AXIS));
        signalPane.add(new BlankLabel("Signal"));

        this.pc = new SignalProcessor(ac);
        ac.out.addDependent(pc);
        pc.addInput(ac.out);

        signal = new Canvas();
        this.pc.bind(signal);
        signalPane.add(signal);

        signalUI = new JFrame("Signal");
        signalUI.setContentPane(signalPane);
        signalUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        signalUI.pack();
        signalUI.setResizable(true);
        signalUI.setVisible(true);
    }
}