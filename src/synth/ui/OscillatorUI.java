package synth.ui;

import javafx.geometry.Orientation;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.data.Pitch;
import synth.bus.Bus;
import synth.osc.Oscillator;
import synth.osc.SmartOscillator;
import synth.ui.components.swing.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;

public class OscillatorUI {

    private SmartOscillator associatedOscillator;

    public JPanel pane;
    private JPanel frequencyPane, unisonPane, wavetablePane, gainPane;
    private BlankKnob frequencyKnob;
    private BlankSlider unisonVoicesSlider;
    private BlankKnob unisonSpreadKnob, unisonBlendKnob;
    private BlankSlider wavetableSlider;
    private BlankKnob oscGainKnob;
    private GridLayout grid;
    public OscillatorUI(SmartOscillator associatedOscillator){
        this.associatedOscillator = associatedOscillator;
        pane = new JPanel();
        pane.setBackground(Color.WHITE);
        frequencyPane = new JPanel();
        frequencyPane.setBackground(Color.WHITE);
        unisonPane = new JPanel();
        unisonPane.setBackground(Color.WHITE);
        wavetablePane = new JPanel();
        wavetablePane.setBackground(Color.WHITE);
        gainPane = new JPanel();
        gainPane.setBackground(Color.WHITE);

        frequencyKnob = new BlankKnob(new BlankKnob.Parameters(1, 1000, 1, false, false), new BlankKnob.Size(48, 48/3), 300, "Frequency");
        frequencyKnob.addPropertyChangeListener(evt -> associatedOscillator.setFrequency(frequencyKnob.params().scale(Pitch.mtof(0), Pitch.mtof(127),(float)evt.getNewValue())));

        unisonVoicesSlider = new BlankSlider(1,8,1);
        unisonVoicesSlider.setSnapToTicks(true);
        unisonVoicesSlider.setPaintLabels(true);
        unisonVoicesSlider.setPaintTicks(true);
        unisonVoicesSlider.addChangeListener(e -> {
            int voices = ((JSlider) e.getSource()).getValue();
            associatedOscillator.setVoices(voices);
        });

        unisonSpreadKnob = new BlankKnob(new BlankKnob.Parameters(0, 10, 0.1f, false, true), BlankKnob.SMALL, 0, "Spread");
        unisonSpreadKnob.addPropertyChangeListener(evt -> associatedOscillator.setSpread((float)evt.getNewValue()));
        unisonBlendKnob = new BlankKnob(new BlankKnob.Parameters(0, 1, 0.01f, false, true), BlankKnob.SMALL, 0.5f, "Blend");
        unisonBlendKnob.addPropertyChangeListener(evt -> associatedOscillator.setBlend((float)evt.getNewValue()));
        pane.setLayout(grid);
        pane.add(frequencyPane);
        pane.add(unisonPane);
        pane.add(wavetablePane);
        pane.add(gainPane);
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        frequencyPane.add(frequencyKnob);
        unisonPane.add(unisonVoicesSlider);
        unisonPane.add(unisonSpreadKnob);
        unisonPane.add(unisonBlendKnob);

        wavetableSlider = new BlankSlider(0, 4, 2);
        wavetableSlider.setSnapToTicks(true);
        Hashtable labelTable = new Hashtable();
        labelTable.put(0, new JLabel("Sine"));
        labelTable.put(1, new JLabel("Tri"));
        labelTable.put(2, new JLabel("Saw"));
        labelTable.put(3, new JLabel("Square"));
        labelTable.put(4, new JLabel("Noise"));
        wavetableSlider.setLabelTable(labelTable);
        wavetableSlider.setPaintTicks(true);
        wavetableSlider.setPaintLabels(true);
        wavetablePane.add(wavetableSlider);
        wavetableSlider.addChangeListener(e -> {
            int value = ((JSlider)e.getSource()).getValue();
            switch(value){
                case 0:
                    associatedOscillator.setWave(Buffer.SINE);
                    break;
                case 1:
                    associatedOscillator.setWave(Buffer.TRIANGLE);
                    break;
                case 2:
                    associatedOscillator.setWave(Buffer.SAW);
                    break;
                case 3:
                    associatedOscillator.setWave(Buffer.SQUARE);
                    break;
                case 4:
                    associatedOscillator.setWave(Buffer.NOISE);
                    break;
                default: break;
            }
        });

        oscGainKnob = new BlankKnob(new BlankKnob.Parameters(0,1, 0.01f, false, true), BlankKnob.LARGE, 1,"Gain");
        oscGainKnob.addPropertyChangeListener(evt -> associatedOscillator.output().setGain((float)evt.getNewValue()));
        gainPane.add(oscGainKnob);

        BlankButton interrupt = new BlankButton("Stop");
        interrupt.addActionListener(e -> associatedOscillator.kill());
        gainPane.add(interrupt);
    }
}
