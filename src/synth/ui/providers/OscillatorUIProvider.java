package synth.ui.providers;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.data.Pitch;
import net.beadsproject.beads.ugens.RangeLimiter;
import synth.auxilliary.ContextProvider;
import synth.osc.SmartOscillator;
import synth.ui.OscillatorUI;

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
        osc.setWave(Buffer.SINE);

        RangeLimiter l = new RangeLimiter(ac,1);
        l.addInput(osc);
        ac.out.addInput(l);

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
    }

    public void close(){
        this.frame.dispatchEvent(new WindowEvent(this.frame, WindowEvent.WINDOW_CLOSING));
    }
}
