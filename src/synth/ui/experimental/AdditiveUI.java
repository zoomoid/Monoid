package synth.ui.experimental;

import net.beadsproject.beads.ugens.Gain;
import synth.osc.AdditiveOscillator;
import synth.osc.BasicOscillator;
import synth.ui.components.swing.BlankKnob;
import synth.ui.components.swing.BlankSlider;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
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
        for(int i = 0; i < addOsc.getOscillators().size(); i++) {
            //slider to adjust frequency TODO add label to set up frequency manually
            BlankSlider currSlider = new BlankSlider(JSlider.HORIZONTAL, 0, 440000, (int) this.addOsc.getBasicFreq()
                    *100);
            currSlider.setSnapToTicks(false);
            currSlider.setPaintTicks(true);
            contentPane.add(currSlider);

            //turned lambda function as seen in filterUI into SliderListener to prevent non final value i to make
            // lambda functionality useless
            class SliderListener implements ChangeListener {
                BasicOscillator osc;

                public SliderListener(BasicOscillator osc) {
                    this.osc = osc;
                }

                public void stateChanged(ChangeEvent e) {
                    BlankSlider source = (BlankSlider) e.getSource();
                    if (!source.getValueIsAdjusting()) {
                        float value = source.getValue() / 100f;
                        this.osc.setFrequency(value);
                    }
                }
            }

            currSlider.addChangeListener(new SliderListener((BasicOscillator) this.addOsc.getOscillators().get(i)));


            //knobs to control velocity
            BlankKnob currBlankKnob = new BlankKnob(BlankKnob.DEFAULT, BlankKnob.SMALL, 10f, "Amp");

            class KnobListener implements PropertyChangeListener {
                //Gain gain;
                BasicOscillator osc;
                KnobListener(BasicOscillator osc) {
                    //this.gain = g
                    this.osc = osc;
                }

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    BlankKnob source = (BlankKnob) evt.getSource();
                    this.osc.setGain((float) source.getValue() / 100);
                    osc.update();
                }
            }

            currBlankKnob.addPropertyChangeListener(new KnobListener((BasicOscillator) this.addOsc.getOscillators().get(i)));

            contentPane.add(currBlankKnob);
            contentPane.updateUI();
        }
        grid = new GridLayout(this.addOsc.getOscillators().size(), 2, 5, 5);
        contentPane.setLayout(grid);
        contentPane.updateUI();
    }
}
