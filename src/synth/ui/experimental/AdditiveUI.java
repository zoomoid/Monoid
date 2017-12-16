package synth.ui.experimental;

import synth.osc.AdditiveOscillator;
import synth.osc.BasicOscillator;
import synth.ui.components.swing.BlankSlider;

import javax.swing.*;
import java.awt.*;

public class AdditiveUI {
    /**The controlled additive osc */
    private AdditiveOscillator addOsc;

    private JPanel contentPane;
    private GridLayout grid;

    AdditiveUI(AdditiveOscillator addOsc) {
        this.addOsc = addOsc; //the controlled additive Osc
        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        for(int i = 0; i < addOsc.getOscillators().size(); i++) {
            int lambdaVar = i;
            BlankSlider curr = new BlankSlider(JSlider.HORIZONTAL, 0, 440000, (int) this.addOsc.getBasicFreq()*100);
            curr.setSnapToTicks(false);
            curr.setPaintTicks(true);
            contentPane.add(curr);
            curr.addChangeListener(e-> {
                float value = ((JSlider) e.getSource()).getValue() / 100f; //floats in 0.01 steps
                //get the current basic oscillator and assign to slider
                BasicOscillator currBasicOsc = (BasicOscillator) this.addOsc.getOscillators().get(lambdaVar);
                currBasicOsc.setFrequency(value);
            });
            contentPane.updateUI();
        }
        grid = new GridLayout(this.addOsc.getOscillators().size(), 1, 5, 5);
        contentPane.setLayout(grid);
        contentPane.updateUI();
    }
}
