package synth.ui.experimental;

import net.beadsproject.beads.ugens.Gain;
import synth.osc.AdditiveOscillator;
import synth.osc.BasicOscillator;
import synth.ui.components.swing.BlankButton;
import synth.ui.components.swing.BlankKnob;
import synth.ui.components.swing.BlankSlider;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class AdditiveUI {
    /**The controlled additive osc */
    private AdditiveOscillator addOsc;

    public JPanel contentPane;
    private GridLayout grid;

    public AdditiveUI(AdditiveOscillator addOsc) {
        this.addOsc = addOsc; //the controlled additive Osc
        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);

        //turned lambda function as seen in filterUI into SliderListener to prevent non final value i to make
        // lambda functionality useless
        class SliderListener implements ChangeListener {
            BasicOscillator osc;
            JTextField addControl; //textField, also controlling frequency

            public SliderListener(BasicOscillator osc, JTextField tf) {
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
            BasicOscillator osc;
            KnobListener(BasicOscillator osc) {
                this.osc = osc;
            }

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                BlankKnob source = (BlankKnob) evt.getSource();
                this.osc.setGain((float) source.getValue() / 100);
                osc.update();
            }
        }

        class FieldListener implements ActionListener {
            BasicOscillator osc;
            BlankSlider addControl; //slider, also controlling frequency

            FieldListener(BasicOscillator osc, BlankSlider bs) {
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

        for(int i = 0; i < addOsc.getOscillators().size(); i++) {
            //textfield to adjust frequency
            JTextField freq = new JTextField(((Float)((BasicOscillator) addOsc.getOscillators().get(i)).getFrequency().getValue()).toString());
            //sldier to adjust frequency
            BlankSlider currSlider = new BlankSlider(JSlider.HORIZONTAL, 0, 440000, (int) this.addOsc.getBasicFreq()
                    *100);
            currSlider.setSnapToTicks(false);
            currSlider.setPaintTicks(true);

            contentPane.add(freq);
            freq.addActionListener(new FieldListener((BasicOscillator) this.addOsc.getOscillators().get(i), currSlider));

            contentPane.add(currSlider);
            currSlider.addChangeListener(new SliderListener((BasicOscillator) this.addOsc.getOscillators().get(i), freq));

            //knobs to control velocity
            BlankKnob currBlankKnob = new BlankKnob(BlankKnob.DEFAULT, BlankKnob.SMALL, ((BasicOscillator) this.addOsc.getOscillators().get(i)).getGain() * 100f, "Amp");

            currBlankKnob.addPropertyChangeListener(new KnobListener((BasicOscillator) this.addOsc.getOscillators().get(i)));

            contentPane.add(currBlankKnob);
            contentPane.updateUI();
        }
        grid = new GridLayout(this.addOsc.getOscillators().size(), 3, 5, 5);
        contentPane.setLayout(grid);
        contentPane.updateUI();
    }
}
