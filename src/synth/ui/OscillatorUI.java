package synth.ui;

import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.data.Pitch;
import synth.osc.SmartOscillator;
import synth.ui.components.swing.*;

import javax.swing.*;
import java.awt.*;

public class OscillatorUI {

    private static int waveTypesIndex;

    private SmartOscillator associatedOscillator;

    public BlankPanel pane;
    private BlankPanel topPanel;
    private BlankPanel frequencyPane, unisonPane, wavetablePane, unisonParameterPane, unisonVoicesPane;
    private BlankKnob frequencyKnob;
    private BlankSlider unisonVoicesSlider;
    private BlankToggleButton unisonEnableButton;
    private BlankKnob unisonSpreadKnob, unisonBlendKnob;
    private BlankImageSpinner wavetableSpinner;
    private BlankKnob gainKnob;
    private BlankLabel unisonVoicesLabel;

    private GridLayout grid;

    public OscillatorUI(SmartOscillator associatedOscillator){
        this.associatedOscillator = associatedOscillator;
        pane = new BlankPanel(new GridLayout(2, 1));
        topPanel = new BlankPanel(new FlowLayout(FlowLayout.LEFT));
        frequencyPane = new BlankPanel();
        wavetablePane = new BlankPanel();
        wavetablePane.setLayout(new BoxLayout(wavetablePane, BoxLayout.Y_AXIS));
        unisonPane = new BlankPanel();
        unisonPane.setLayout(new BoxLayout(unisonPane, BoxLayout.Y_AXIS));
        unisonParameterPane = new BlankPanel(new FlowLayout(FlowLayout.LEFT));
        unisonVoicesPane = new BlankPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(frequencyPane);
        topPanel.add(wavetablePane);

        pane.add(topPanel);
        pane.add(unisonPane);


        frequencyKnob = new BlankKnob(new BlankKnob.Parameters(1, 1000, 1, false, false), new BlankKnob.Size(48, 48/3), 300, "Frequency");
        frequencyKnob.addPropertyChangeListener(e -> {
            associatedOscillator.setFrequency(frequencyKnob.params().scale(
                    Pitch.mtof(0), Pitch.mtof(127),(float) e.getNewValue()));
        });
        frequencyPane.add(frequencyKnob);

        WaveType[] icons = {
                new WaveType("src/sine.png", 0),
                new WaveType("src/triangle.png",1),
                new WaveType("src/saw.png",2),
                new WaveType("src/square.png", 3),
                new WaveType("src/noise.png", 4)
        };
        wavetableSpinner = new BlankImageSpinner(new SpinnerListModel(icons));
        wavetableSpinner.addChangeListener(e -> {
            int value = ((WaveType)((JSpinner)e.getSource()).getValue()).getIndex();
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
        wavetableSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        gainKnob = new BlankKnob(new BlankKnob.Parameters(0,1, 0.01f, false, true), BlankKnob.MEDIUM, 1,"Gain");
        gainKnob.addPropertyChangeListener(e -> associatedOscillator.output().setGain((float)e.getNewValue()));
        gainKnob.setAlignmentX(Component.LEFT_ALIGNMENT);
        wavetablePane.add(wavetableSpinner);
        wavetablePane.add(gainKnob);

        unisonEnableButton = new BlankToggleButton("Unison");
        unisonEnableButton.addActionListener(e -> {
            if(((BlankToggleButton)e.getSource()).isToggled()){
                associatedOscillator.setVoices(unisonVoicesSlider.getValue());
                associatedOscillator.setSpread(unisonSpreadKnob.getValue());
                associatedOscillator.setBlend(unisonBlendKnob.getValue());
            } else {
                associatedOscillator.setVoices(1);
                associatedOscillator.setSpread(0f);
                associatedOscillator.setBlend(0f);
            }
        });
        unisonSpreadKnob = new BlankKnob(new BlankKnob.Parameters(0, 10, 0.1f, false, true), BlankKnob.SMALL, 0, "Spread");
        unisonSpreadKnob.addPropertyChangeListener(e -> associatedOscillator.setSpread((float)e.getNewValue()));
        unisonBlendKnob = new BlankKnob(new BlankKnob.Parameters(0, 1, 0.01f, false, true), BlankKnob.SMALL, 0.5f, "Blend");
        unisonBlendKnob.addPropertyChangeListener(e -> associatedOscillator.setBlend((float)e.getNewValue()));
        unisonParameterPane.add(unisonEnableButton);
        unisonParameterPane.add(unisonSpreadKnob);
        unisonParameterPane.add(unisonBlendKnob);
        unisonPane.add(unisonParameterPane);

        unisonVoicesLabel = new BlankLabel("Voices");
        unisonVoicesPane.add(unisonVoicesLabel);

        unisonVoicesSlider = new BlankSlider(1,8,1);
        unisonVoicesSlider.setSnapToTicks(true);
        unisonVoicesSlider.setPaintLabels(true);
        unisonVoicesSlider.setPaintTicks(true);
        unisonVoicesSlider.addChangeListener(e -> {
            int voices = ((JSlider) e.getSource()).getValue();
            associatedOscillator.setVoices(voices);
        });
        unisonVoicesPane.add(unisonVoicesSlider);

        unisonPane.add(unisonVoicesPane);
    }
}
