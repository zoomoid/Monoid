package synth.ui.components;

import net.beadsproject.beads.analysis.Analyzer;
import net.beadsproject.beads.analysis.featureextractors.FFT;
import net.beadsproject.beads.core.AudioContext;
import synth.auxilliary.SignalProcessor;
import synth.ui.components.swing.BlankLabel;
import synth.ui.components.swing.BlankPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedList;


/**
 * Creates a visualisation of a given signal
 * By default, this displays the master output
 * Usage: Simply create a Signal instance with the current audio context. Signal will add itself to the dependencies of ac.out
 * to get the final output buffer and update the canvas with this information.
 */
public class Signal {
    private Canvas signal;
    private SignalProcessor pc;
    private JFrame signalUI;
    private BlankPanel signalPane;

    public enum Type {
        SIGNAL, SPECTRUM
    }

    public static final Type SIGNAL = Type.SIGNAL;
    /** TODO Work in progress */
    public static final Type SPECTRUM = Type.SPECTRUM;

    private Type type;

    public Signal(AudioContext ac){
        this(ac, Signal.SIGNAL);
    }

    public Signal(AudioContext ac, Type type){
        signalPane = new BlankPanel();
        signalPane.setLayout(new BoxLayout(signalPane, BoxLayout.PAGE_AXIS));
        signalPane.add(new BlankLabel("Signal"));

        this.pc = new SignalProcessor(ac);

        switch(type){
            case SIGNAL: default :
                ac.out.addDependent(pc);
                pc.addInput(ac.out);
                break;
            case SPECTRUM:
                /* TODO WIP*/
                ac.out.addDependent(pc);
                pc.addInput(ac.out);
                break;
        }


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