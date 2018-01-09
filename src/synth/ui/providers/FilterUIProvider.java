package synth.ui.providers;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.data.Pitch;
import net.beadsproject.beads.ugens.RangeLimiter;
import synth.Launcher;
import synth.auxilliary.BufferDebugger;
import synth.auxilliary.ContextProvider;
import synth.filter.Filter;
import synth.filter.models.BiquadFilter;
import synth.osc.SmartOscillator;
import synth.ui.FilterUI;
import synth.ui.OscillatorUI;
import synth.ui.components.swing.BlankPanel;
import synth.ui.components.swing.BlankToggleButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class FilterUIProvider implements Provider {

    private BlankPanel noteTriggerPane;
    private JFrame frame;
    private FilterUI filterUI;
    private OscillatorUI oscUI;

    private BlankPanel ui;

    public FilterUIProvider(){
        AudioContext ac = ContextProvider.ac();
        ac.start();

        SmartOscillator osc = new SmartOscillator(ac, true);
        osc.setFrequency(Pitch.mtof(48));
        osc.setBlend(1);
        osc.setSpread(0);
        osc.setVoices(1);
        osc.setWave(Buffer.SAW);
        osc.setName("1");

        osc.noteOff();

        Filter f = new Filter(ac, Filter.Type.BiquadFilter, BiquadFilter.Type.LPF, 300f, 0.76f, 0.71f);
        f.setName("Filter A");
        f.setGain(0.6f);

        BufferDebugger beforeFilter = new BufferDebugger(ac);
        BufferDebugger afterFilter = new BufferDebugger(ac);

        beforeFilter.addInput(osc);
        f.addInput(beforeFilter);
        afterFilter.addInput(f);
        RangeLimiter limiter = new RangeLimiter(ac, 2);
        limiter.addInput(afterFilter);

        ac.out.addInput(limiter);
        oscUI = new OscillatorUI(osc);
        oscUI.show();

        filterUI = new FilterUI(f);
        filterUI.show();

        noteTriggerPane = new BlankPanel();
        BlankToggleButton noteTrigger = new BlankToggleButton("Note On");
        noteTrigger.addActionListener(e -> {
            BlankToggleButton b = ((BlankToggleButton)e.getSource());
            if(!b.isToggled()){
                osc.noteOn();
                f.noteOn();
                b.setText("Note Off");
            } else {
                osc.noteOff();
                f.noteOff();
                b.setText("Note On");
            }
        });
        noteTriggerPane.add(noteTrigger);
        JFrame noteTriggerFrame = new JFrame("");
        noteTriggerFrame.setContentPane(noteTriggerPane);
        noteTriggerFrame.setVisible(true);
        noteTriggerFrame.setLocation(300, 85);
        noteTriggerFrame.pack();
        noteTriggerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        noteTriggerFrame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                noteTriggerFrame.dispose();
                close();
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });

        ui = new BlankPanel();
        this.ui.setLayout(new GridBagLayout());
        this.ui.add(oscUI.pane());
        this.ui.add(filterUI.pane());
        ui.setVisible(true);
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
                Launcher.main.filterButton().toggle(false);
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
        this.frame.setVisible(true);
    }

    public void close(){
        this.frame.dispatchEvent(new WindowEvent(this.frame, WindowEvent.WINDOW_CLOSING));
    }
}
