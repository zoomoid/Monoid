package synth.ui;

import synth.osc.Oscillator;
import synth.osc.OscillatorController;
import net.beadsproject.beads.data.Buffer;
import synth.osc.SmartOscillator;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// TODO Port Swing usage to JavaFX with new components
public class OscillatorUI {
    private JPanel OscMainFrame;
    private JButton triangle;
    private JButton saw;
    private JButton square;
    private JButton sine;
    private JPanel WaveshapeSelector;
    private JPanel WaveshapesButtonGroup;
    private JPanel WaveshapesLabelWrapper;
    private JPanel TitleWrapper;
    private JPanel LabelWrapper;
    private JCheckBox unisonCheckBox;
    private JSlider unisonVoices;
    private JSlider unisonPitchSpread;
    private JSlider masterVolume;
    private JPanel UnisonWrapper;
    private JPanel AdjustmentWrapper;
    private JPanel MixerWrapper;
    private JLabel labelUnisonVoices;
    private JLabel labelUnisonPitchSpread;
    private JButton adjustUnison;
    private JButton adjustVolume;
    private JPanel OscillatorPitchWrapper;
    private JButton adjustFrequency;
    private JSlider oscillatorPitch;
    private JSlider unisonBlend;
    private JLabel labelUnisonBlend;
    private JComboBox oscillatorTypeSelector;
    private JLabel waveshapesLabel;

    OscillatorController oscillatorController;
    SmartOscillator assignedOsc;

    UIController context;

    /**
     * Default UI constructor
     */
    public OscillatorUI(UIController context, int oscID){
        this.context = context;
        oscillatorController = this.context.getContext().getOscs();

        oscillatorController.getOsc(oscID);

        assignedOsc.setFrequency(oscillatorPitch.getValue());
        assignedOsc.setup();

        unisonCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean toggle = unisonCheckBox.isSelected();
                unisonPitchSpread.setEnabled(toggle);
                unisonVoices.setEnabled(toggle);
                unisonBlend.setEnabled(toggle);
                labelUnisonPitchSpread.setEnabled(toggle);
                labelUnisonVoices.setEnabled(toggle);
                labelUnisonBlend.setEnabled(toggle);
                if(toggle){
                    System.out.println("Enabled Unison");
                } else {
                    System.out.println("Disabled Unison");
                }
                assignedOsc.setVoices(1);
            }
        });
        sine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                assignedOsc.setWave(Buffer.SINE);
                System.out.println("Updated Wave Type to SINE");
            }
        });
        triangle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                assignedOsc.setWave(Buffer.TRIANGLE);
                System.out.println("Updated Wave Type to TRIANGLE");
            }
        });
        saw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                assignedOsc.setWave(Buffer.SAW);
                System.out.println("Updated Wave Type to SAW");
            }
        });
        square.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                assignedOsc.setWave(Buffer.SQUARE);
                System.out.println("Updated Wave Type to SQUARE");
            }
        });
        adjustUnison.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                assignedOsc.setVoices(Math.max(1, unisonVoices.getValue()));
                assignedOsc.setBlend(unisonBlend.getValue()/100f);
                assignedOsc.setSpread(unisonPitchSpread.getValue());
                System.out.println("Updated Unison to " + unisonVoices.getValue() + " Voices and Pitch Spread to " + unisonPitchSpread.getValue() + "Hz");
            }
        });
        adjustVolume.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                assignedOsc.setOutput(masterVolume.getValue()/100f);
                System.out.println("Updated Master Volume to " + masterVolume.getValue()/100f);
            }
        });
        adjustFrequency.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                assignedOsc.setFrequency(oscillatorPitch.getValue());
                System.out.println("Updated frequency to " + oscillatorPitch.getValue() + "Hz");
            }
        });
    }
}
