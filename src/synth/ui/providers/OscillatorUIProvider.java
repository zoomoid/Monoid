package synth.ui.providers;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.data.Pitch;
import net.beadsproject.beads.ugens.RangeLimiter;
import synth.auxilliary.ContextProvider;
import synth.auxilliary.SignalProcessor;
import synth.osc.SmartOscillator;
import synth.osc.Waveform;
import synth.ui.OscillatorUI;
import synth.ui.components.Canvas;
import synth.ui.components.swing.BlankPanel;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class OscillatorUIProvider implements Provider {

    private JFrame frame;
    private OscillatorUI ui;

    public OscillatorUIProvider(){
        AudioContext ac = ContextProvider.ac();
        ac.start();

        SmartOscillator osc = new SmartOscillator(ac);
        osc.setFrequency(Pitch.mtof(48));
        osc.setBlend(1);
        osc.setSpread(0);
        osc.setVoices(1);
        osc.setWave(Waveform.SINE);
        osc.setName("Demo");

        RangeLimiter l = new RangeLimiter(ac,2);
        l.addInput(osc);


        this.ui = new OscillatorUI(osc);
        ui.show();

        this.frame = new JFrame("Oscillator");
        this.frame.setContentPane(this.ui.pane());
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

        SignalProcessor pc = new SignalProcessor(ac);

        // Patch outputs
        pc.addInput(l);

        ac.out.addInput(pc);

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
    }

    public void close(){
        this.frame.dispatchEvent(new WindowEvent(this.frame, WindowEvent.WINDOW_CLOSING));
    }
}
