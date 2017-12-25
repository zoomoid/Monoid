package synth.ui;

import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.data.Pitch;
import synth.SynthController;
import synth.osc.Oscillator;
import synth.osc.UnisonOscillator;
import synth.osc.WavetableOscillator;
import synth.ui.components.swing.*;

import javax.swing.*;
import java.awt.*;

public class OscillatorUI {

    public class WaveType extends SpinnerImageContainer {

        public WaveType(ImageIcon img, int index){
            if(img == null){
                throw new IllegalArgumentException("WaveType expects icon to be not null");
            }
            this.index = index;
            this.img = img.getImage();
        }

        public WaveType(String path, int index){
            if(path == null){
                throw new IllegalArgumentException("WaveType expects path to be not null");
            }
            this.img = new ImageIcon((SynthController.class.getResource("assets/" + path)).getPath()).getImage();
            if(this.img == null){
                throw new IllegalArgumentException("Cannot load image from given path");
            }
            this.index = index;
        }

        public WaveType(String path){
            if(path == null){
                throw new IllegalArgumentException("WaveType expects path to be not null");
            }
            this.img = new ImageIcon((SynthController.class.getResource("assets/" + path)).getPath()).getImage();
            if(this.img == null){
                throw new IllegalArgumentException("Cannot load image from given path");
            }
            this.index = staticIndex;
            staticIndex++;
        }
    }

    private Oscillator associatedOscillator;

    private BlankPanel pane;
    private BlankPanel topPanel;
    private BlankPanel frequencyPane, unisonPane, gainPane, unisonParameterPane, unisonVoicesPane;
    private BlankKnob frequencyKnob;
    private BlankKnob phaseKnob;
    private BlankSlider unisonVoicesSlider;
    private BlankToggleButton unisonEnableButton;
    private BlankKnob unisonSpreadKnob, unisonBlendKnob;
    private BlankImageSpinner wavetableSpinner;
    private BlankKnob gainKnob;
    private BlankLabel unisonVoicesLabel;

    private JFrame ui;

    private GridLayout grid;

    public OscillatorUI(Oscillator associatedOscillator){
        this.associatedOscillator = associatedOscillator;
        pane = new BlankPanel(new GridLayout(2, 1));
        topPanel = new BlankPanel(new FlowLayout(FlowLayout.LEFT));
        frequencyPane = new BlankPanel();
        gainPane = new BlankPanel();
        gainPane.setLayout(new BoxLayout(gainPane, BoxLayout.Y_AXIS));
        unisonPane = new BlankPanel();
        unisonPane.setLayout(new BoxLayout(unisonPane, BoxLayout.Y_AXIS));
        unisonParameterPane = new BlankPanel(new FlowLayout(FlowLayout.LEFT));
        unisonVoicesPane = new BlankPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(frequencyPane);
        topPanel.add(gainPane);

        pane.add(topPanel);
        pane.add(unisonPane);


        frequencyKnob = new BlankKnob(new BlankKnob.Parameters(1, 1000, 1, false, false), new BlankKnob.Size(48, 48/3), 300, "Frequency");
        frequencyKnob.addPropertyChangeListener(e -> {
            associatedOscillator.setFrequency(frequencyKnob.params().scale(
                    Pitch.mtof(0), Pitch.mtof(127),(float) e.getNewValue()));
        });
        frequencyPane.add(frequencyKnob);

        phaseKnob = new BlankKnob(new BlankKnob.Parameters(-1, 1, 0.01f, false, true), BlankKnob.SMALL, -1, "Phase");
        phaseKnob.addPropertyChangeListener(e -> {
            associatedOscillator.setPhase((float)(e.getNewValue()));
        });
        gainKnob = new BlankKnob(new BlankKnob.Parameters(0,1, 0.01f, false, true), BlankKnob.MEDIUM, 1,"Gain");
        gainKnob.addPropertyChangeListener(e -> {
            associatedOscillator.setGain((float) e.getNewValue());
        });
        gainKnob.setAlignmentX(Component.LEFT_ALIGNMENT);
        gainPane.add(gainKnob);

        if(associatedOscillator instanceof WavetableOscillator){
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
                        ((WavetableOscillator)associatedOscillator).setWave(Buffer.SINE);
                        break;
                    case 1:
                        ((WavetableOscillator)associatedOscillator).setWave(Buffer.TRIANGLE);
                        break;
                    case 2:
                        ((WavetableOscillator)associatedOscillator).setWave(Buffer.SAW);
                        break;
                    case 3:
                        ((WavetableOscillator)associatedOscillator).setWave(Buffer.SQUARE);
                        break;
                    case 4:
                        ((WavetableOscillator)associatedOscillator).setWave(Buffer.NOISE);
                        break;
                    default: break;
                }
            });
            wavetableSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
            gainPane.add(wavetableSpinner);
        }


        if(associatedOscillator instanceof UnisonOscillator){
            unisonEnableButton = new BlankToggleButton("Unison");
            unisonEnableButton.addActionListener(e -> {
                if(((BlankToggleButton)e.getSource()).isToggled()){
                    ((UnisonOscillator)associatedOscillator).setVoices(unisonVoicesSlider.getValue());
                    ((UnisonOscillator)associatedOscillator).setSpread(unisonSpreadKnob.getValue());
                    ((UnisonOscillator)associatedOscillator).setBlend(unisonBlendKnob.getValue());
                } else {
                    ((UnisonOscillator)associatedOscillator).setVoices(1);
                    ((UnisonOscillator)associatedOscillator).setSpread(0f);
                    ((UnisonOscillator)associatedOscillator).setBlend(0f);
                }
            });
            unisonSpreadKnob = new BlankKnob(new BlankKnob.Parameters(0, 10, 0.1f, false, true), BlankKnob.SMALL, 0, "Spread");
            unisonSpreadKnob.addPropertyChangeListener(e -> {
                ((UnisonOscillator) associatedOscillator).setSpread((float) e.getNewValue());
            });
            unisonBlendKnob = new BlankKnob(new BlankKnob.Parameters(0, 1, 0.01f, false, true), BlankKnob.SMALL, 0.5f, "Blend");
            unisonBlendKnob.addPropertyChangeListener(e -> {
                ((UnisonOscillator) associatedOscillator).setBlend((float) e.getNewValue());
            });
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
                ((UnisonOscillator)associatedOscillator).setVoices(voices);
            });
            unisonVoicesPane.add(unisonVoicesSlider);

            unisonPane.add(unisonVoicesPane);
        }



        this.ui = new JFrame(associatedOscillator.getName());
        this.ui.setContentPane(this.pane);
        this.ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.ui.pack();
        this.ui.setResizable(false);
    }

    public void hide(){
        this.ui.setVisible(false);
    }

    public void show(){
        this.ui.setVisible(true);
    }
}
