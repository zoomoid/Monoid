package synth.ui;

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
    Oscillator osc;
    //turned lambda function as seen in filterUI into SliderListener to prevent non final value i to make
    // lambda functionality useless
    class SliderListener implements ChangeListener {
        Oscillator osc;
        JTextField addControl; //textField, also controlling frequency

        public SliderListener(Oscillator osc, JTextField tf) {
            this.osc = osc;
            this.addControl = tf;
        }

        public void stateChanged(ChangeEvent e) {
            BlankSlider source = (BlankSlider) e.getSource();
            if (!source.getValueIsAdjusting()) {
                float value = source.getValue() / 100f;
                this.osc.setFrequency(value);
                this.addControl.setText(Float.toString(value));
            }
        }
    }
    //same here, but as knobListener
    class KnobListener implements PropertyChangeListener {
        Oscillator osc;
        KnobListener(Oscillator osc) {
            this.osc = osc;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            BlankKnob source = (BlankKnob) evt.getSource();
            this.osc = this.osc.setGain(source.getValue() / 100);
            this.osc.update();
        }
    }

    class FieldListener implements ActionListener {
        Oscillator osc;
        BlankSlider addControl; //slider, also controlling frequency

        FieldListener(Oscillator osc, BlankSlider bs) {
            this.osc = osc;
            this.addControl = bs;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            JTextField text = (JTextField) e.getSource();
            float newFreq= Float.parseFloat(text.getText());
            this.addControl.setValue((int) (newFreq * 100));
            osc.setFrequency(newFreq);
            osc.update();
        }
    }

    public OscillatorPanel() {super();}

    public OscillatorPanel(Oscillator osc) {
        this.osc = osc;
        BlankTextfield freq = new BlankTextfield(((Float)(osc.getFrequency().getValue())).toString());
        //slider to adjust frequency
        BlankSlider slider;
        if((int) osc.getFrequency().getValue() * 100 <= 440000) {
            slider = new BlankSlider(JSlider.HORIZONTAL, 0, 440000, (int) osc.getFrequency().getValue() * 100);
        } else {
            slider = new BlankSlider(JSlider.HORIZONTAL, 0, 440000, 440000);
        }
        slider.setSnapToTicks(false);
        slider.setPaintTicks(true);

        this.add(freq);
        freq.addActionListener(new FieldListener(osc, slider));

        this.add(slider);
        slider.addChangeListener(new SliderListener(osc, freq));

        //knobs to control velocity
        BlankKnob blankKnob = new BlankKnob(BlankKnob.DEFAULT, BlankKnob.SMALL, (osc.getGain().getValue() * 100f), "Amp");

        blankKnob.addPropertyChangeListener(new KnobListener(osc));

        this.add(blankKnob);
        this.updateUI();
    }

    public void remove() {
        this.osc.kill();
    }
}
