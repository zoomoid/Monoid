package synth.ui.providers;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.RangeLimiter;
import synth.auxilliary.ContextProvider;
import synth.auxilliary.SignalProcessor;
import synth.modulation.ModulationOscillator;
import synth.osc.BasicOscillator;
import synth.ui.ModulationUI;
import synth.ui.components.Canvas;
import synth.ui.components.swing.BlankPanel;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ModulationUIProvider implements Provider {

    private JFrame frame;
    private ModulationUI ui;

    public ModulationUIProvider(){
        AudioContext ac = ContextProvider.ac();
        ac.start();
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

        // Signal Device
        BlankPanel signalPane = new BlankPanel();
        Canvas c = new Canvas();
        pc.bind(c);
        signalPane.add(c);

        // Signal Plot
        JFrame signalUI = new JFrame("Signal");
        signalUI.setContentPane(signalPane);
        signalUI.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        signalUI.setLocation(0,500);
        signalUI.pack();
        signalUI.setResizable(true);
        signalUI.setVisible(true);

        this.frame = new JFrame("Modulation");
        this.frame.setContentPane(ui.pane());
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}

            @Override
            public void windowClosing(WindowEvent e) {
                ac.stop();
                frame.setVisible(false);
                frame.dispose();
            }

            @Override
            public void windowClosed(WindowEvent e) {}

            @Override
            public void windowIconified(WindowEvent e) {}

            @Override
            public void windowDeiconified(WindowEvent e) {}

            @Override
            public void windowActivated(WindowEvent e) {}

            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        this.frame.pack();
        this.frame.setResizable(true);
        this.frame.setVisible(true);
    }

    public void close(){
        this.frame.dispatchEvent(new WindowEvent(this.frame, WindowEvent.WINDOW_CLOSING));
    }
}
