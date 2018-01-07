package tests.ui;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.RangeLimiter;
import synth.auxilliary.SignalProcessor;
import synth.modulation.ModulationOscillator;
import synth.osc.BasicOscillator;
import synth.ui.ModulationUI;
import synth.ui.components.Canvas;
import synth.ui.components.swing.BlankPanel;
import synth.auxilliary.ContextProvider;

import javax.swing.*;

public class ModulationUITest {
    public static void main(String[] args){
        AudioContext ac = ContextProvider.ac();
        ModulationOscillator amModulator = new ModulationOscillator(ac, ModulationOscillator.Type.SINE, 0f, 0.5f);
        ModulationOscillator fmModulator = new ModulationOscillator(ac, ModulationOscillator.Type.SINE, 0f, 1);
        amModulator.noteOn();
        fmModulator.noteOn();
        BasicOscillator carrier = new BasicOscillator(ac, 0f, Buffer.SINE);

        ModulationUI ui = new ModulationUI(carrier, amModulator, fmModulator);
        ui.show();

        SignalProcessor pc = new SignalProcessor(ac);
        pc.addInput(carrier);

        RangeLimiter l = new RangeLimiter(ac, 2);
        l.addInput(pc);

        ac.out.addInput(l);
        ac.start();

        BlankPanel signalPane = new BlankPanel();
        Canvas c = new Canvas();
        pc.bind(c);
        signalPane.add(c);
        JFrame signalUI = new JFrame("Modulation Demo");
        signalUI.setContentPane(signalPane);
        signalUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        signalUI.pack();
        signalUI.setResizable(true);
        signalUI.setVisible(true);
    }
}
