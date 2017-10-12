package synth.ui;

import synth.osc.OscillatorManager;
import net.beadsproject.beads.data.Buffer;
import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    OscillatorManager oscManager;

    /**
     * Default UI constructor
     */
    public OscillatorUI(){
        oscManager = new OscillatorManager();

        oscManager.setup(oscillatorPitch.getValue());

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
                oscManager.getOsc().setVoices(1);
            }
        });
        sine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                oscManager.getOsc().setWave(Buffer.SINE);
                System.out.println("Updated Wave Type to SINE");
            }
        });
        triangle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                oscManager.getOsc().setWave(Buffer.TRIANGLE);
                System.out.println("Updated Wave Type to TRIANGLE");
            }
        });
        saw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                oscManager.getOsc().setWave(Buffer.SAW);
                System.out.println("Updated Wave Type to SAW");
            }
        });
        square.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                oscManager.getOsc().setWave(Buffer.SQUARE);
                System.out.println("Updated Wave Type to SQUARE");
            }
        });
        adjustUnison.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                oscManager.getOsc().setVoices(Math.max(1, unisonVoices.getValue()));
                oscManager.getOsc().setBlend(unisonBlend.getValue()/100f);
                oscManager.getOsc().setSpread(unisonPitchSpread.getValue());
                System.out.println("Updated Unison to " + unisonVoices.getValue() + " Voices and Pitch Spread to " + unisonPitchSpread.getValue() + "Hz");
            }
        });
        adjustVolume.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                oscManager.getMaster().setGain(masterVolume.getValue()/100f);
                System.out.println("Updated Master Volume to " + masterVolume.getValue()/100f);
            }
        });
        adjustFrequency.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                oscManager.getOsc().setFrequency(oscillatorPitch.getValue());
                System.out.println("Updated frequency to " + oscillatorPitch.getValue() + "Hz");
            }
        });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame("OscillatorUI");

        OscillatorUI ui = new OscillatorUI();

        frame.setContentPane(ui.OscMainFrame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
