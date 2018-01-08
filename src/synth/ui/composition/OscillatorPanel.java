package synth.ui.composition;

import synth.osc.Oscillator;
import synth.ui.components.swing.BlankKnob;
import synth.ui.components.swing.BlankPanel;
import synth.ui.components.swing.BlankSlider;
import synth.ui.components.swing.BlankTextfield;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class OscillatorPanel extends BlankPanel {

    BlankTextfield freq;
    BlankSlider slider;
    Oscillator osc;
    /** This should not be allowed to create since it will cause null pointers of doom */
    //public OscillatorPanel() {super();}

    public OscillatorPanel(Oscillator osc) {
        this.osc = osc;
        this.freq = new BlankTextfield(osc.getFrequency().getValue() + "");
        //slider to adjust frequency
        if((int) osc.getFrequency().getValue() * 100 <= 440000) {
            this.slider = new BlankSlider(JSlider.HORIZONTAL, 0, 440000, (int) osc.getFrequency().getValue() * 100);
        } else {
            this.slider = new BlankSlider(JSlider.HORIZONTAL, 0, 440000, 440000);
        }
        slider.setSnapToTicks(false);
        slider.setPaintTicks(true);
        freq.addActionListener(e -> {
            float newFreq = Float.parseFloat(((BlankTextfield)e.getSource()).getText());
            this.slider.setValue((int) (newFreq * 100));
            osc.setFrequency(newFreq);
        });
        slider.addChangeListener(e -> {
            float value = ((JSlider)e.getSource()).getValue() / 100f;
            this.osc.setFrequency(value);
            freq.setText(Float.toString(value));
        });
        //knobs to control velocity
        BlankKnob blankKnob = new BlankKnob(new BlankKnob.Parameters(0, 1, 0.01f, false, true), BlankKnob.SMALL, (osc.getGain().getValue()), "Amp");

        blankKnob.addPropertyChangeListener(e -> {
            this.osc.setGain(((float) e.getNewValue()));
        });

        this.add(slider);
        this.add(freq);
        this.add(blankKnob);
        this.updateUI();
    }

    public void remove() {
        this.osc.kill();
    }
}
