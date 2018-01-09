package synth.ui.providers;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.data.Pitch;
import synth.auxilliary.ContextProvider;
import synth.filter.Filter;
import synth.filter.models.BiquadFilter;
import synth.osc.SmartOscillator;
import synth.ui.FilterUI;
import synth.ui.OscillatorUI;
import synth.ui.components.swing.BlankPanel;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class FilterUIProvider implements Provider {

    private JFrame frame;
    private FilterUI filterUI;
    private OscillatorUI oscUI;

    private BlankPanel ui;

    public FilterUIProvider(){
        AudioContext ac = ContextProvider.ac();
        ac.start();

        SmartOscillator osc = new SmartOscillator(ac);
        osc.setFrequency(Pitch.mtof(48));
        osc.setBlend(2);
        osc.setSpread(0.25f);
        osc.setVoices(5);
        osc.setWave(Buffer.SAW);
        osc.setName("Oscillator A");

        Filter f = new Filter(ac, Filter.Type.BiquadFilter, BiquadFilter.Type.LPF, 300f, 0.76f, 0.71f);
        f.setName("Filter A");
        f.addInput(osc);

        ac.out.addInput(f);
        ac.out.setGain(0.66f);
        oscUI = new OscillatorUI(osc);
        oscUI.show();

        filterUI = new FilterUI(f);
        filterUI.show();

        ui = new BlankPanel();
        this.ui.add(oscUI.pane());
        this.ui.add(filterUI.pane());

        this.frame = new JFrame(f.getName());
        this.frame.setContentPane(this.ui);
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
        this.frame.setLocation(300, 300);
        this.frame.pack();
        this.frame.setResizable(false);
    }

    public void close(){
        this.frame.dispatchEvent(new WindowEvent(this.frame, WindowEvent.WINDOW_CLOSING));
    }
}
