package synth.ui;

import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.data.Pitch;
import synth.SynthController;
import synth.osc.Oscillator;
import synth.osc.UnisonOscillator;
import synth.osc.WavetableOscillator;
import synth.ui.components.swing.*;
import synth.ui.composition.EnvelopePanel;
import synth.ui.composition.LFOPanel;

import javax.swing.*;
import java.awt.*;

public class OscillatorUI extends SynthesizerUserInterfaceModule {

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

    private BlankPanel pane, mainPane, optionsPane;
    private BlankPanel topPane, basicPane, noteTriggerPane;
    private BlankPanel frequencyPane, unisonPane, gainPane, unisonParameterPane, unisonVoicesPane;
    private BlankKnob frequencyKnob;
    private BlankKnob phaseKnob;
    private BlankSlider unisonVoicesSlider;
    private BlankToggleButton unisonEnableButton;
    private BlankKnob unisonSpreadKnob, unisonBlendKnob;
    private BlankImageSpinner wavetableSpinner;
    private BlankKnob gainKnob;
    private BlankLabel unisonVoicesLabel;

    private BlankLabel oscillatorLabel;
    private BlankToggleButton optionsButton;

    private BlankPanel _pane;
    private BlankLabel _headline;
    private BlankLabel _frequencyLabel, _gainLabel, _wavetableLabel, _unisonVoicesLabel, _unisonSpreadLabel, _unisonBlendLabel;

    private BlankSpinner _frequency;
    private BlankSpinner _gain;
    private BlankSpinner _unisonVoices;
    private BlankSpinner _unisonSpread;
    private BlankSpinner _unisonBlend;
    private BlankImageSpinner _wavetableSpinner;

    private BlankPanel modulationPanel;
    private EnvelopePanel freqEnv, ampEnv;
    private LFOPanel freqLFO, ampLFO;

    public JFrame ui;

    private WaveType[] icons = {
        new WaveType("sine.png", 0),
        new WaveType("triangle.png",1),
        new WaveType("saw.png",2),
        new WaveType("square.png", 3),
        new WaveType("noise.png", 4)
    };

    public OscillatorUI(Oscillator associatedOscillator){
        this.associatedOscillator = associatedOscillator;
        this.associatedOscillator.noteOn();

        pane = new BlankPanel();
        //pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        mainPane = new BlankPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
        topPane = new BlankPanel(new FlowLayout(FlowLayout.LEFT));
        basicPane = new BlankPanel(new FlowLayout(FlowLayout.LEFT));
        frequencyPane = new BlankPanel();
        gainPane = new BlankPanel();
        gainPane.setLayout(new BoxLayout(gainPane, BoxLayout.Y_AXIS));
        unisonPane = new BlankPanel();
        unisonPane.setLayout(new BoxLayout(unisonPane, BoxLayout.Y_AXIS));
        unisonParameterPane = new BlankPanel(new FlowLayout(FlowLayout.LEFT));
        unisonVoicesPane = new BlankPanel(new FlowLayout(FlowLayout.LEFT));
        modulationPanel = new BlankPanel();
        modulationPanel.setLayout(new BoxLayout(modulationPanel, BoxLayout.Y_AXIS));
        oscillatorLabel = new BlankLabel("Oscillator " + associatedOscillator.getName());
        optionsButton = new BlankToggleButton("Options");
        optionsButton.pack();
        optionsButton.setFont(new Font("Fira Mono", Font.BOLD, 10));
        optionsButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        optionsButton.addActionListener(e -> {
            if(optionsPane.isVisible()){
                this.hideFineTunePanel();
            } else {
                this.showFineTunePanel();
            }
        });
        topPane.add(oscillatorLabel);
        topPane.add(optionsButton);

        basicPane.add(frequencyPane);
        basicPane.add(gainPane);

        noteTriggerPane = new BlankPanel();
        BlankToggleButton noteTrigger = new BlankToggleButton("Note On");
        noteTrigger.toggle();
        noteTrigger.addActionListener(e -> {
            BlankToggleButton b = ((BlankToggleButton)e.getSource());
            if(b.isToggled()){
                associatedOscillator.noteOn();
                b.setText("Note Off");
            } else {
                associatedOscillator.noteOff();
                b.setText("Note On");
            }
        });

        noteTriggerPane.add(noteTrigger);

        mainPane.add(topPane);
        mainPane.add(noteTriggerPane);
        mainPane.add(basicPane);
        mainPane.add(unisonPane);
        mainPane.add(modulationPanel);
        pane.add(mainPane);

        frequencyKnob = new BlankKnob(new BlankKnob.Parameters(0, 127, 0.25f, true, true), new BlankKnob.Size(48, 48/3), 57, "Frequency");
        frequencyKnob.addPropertyChangeListener(e -> {
            associatedOscillator.setFrequency(Pitch.mtof((float)e.getNewValue()));
        });
        frequencyPane.add(frequencyKnob);

        gainKnob = new BlankKnob(new BlankKnob.Parameters(0,1, 0.01f, false, true), BlankKnob.MEDIUM, 1,"Gain");
        gainKnob.addPropertyChangeListener(e -> {
            associatedOscillator.setGain((float) e.getNewValue());
        });
        gainKnob.setAlignmentX(Component.LEFT_ALIGNMENT);
        gainPane.add(gainKnob);

        if(associatedOscillator instanceof WavetableOscillator){
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
                if(!((BlankToggleButton)e.getSource()).isToggled()){
                    ((UnisonOscillator)associatedOscillator).setVoices(unisonVoicesSlider.getValue());
                    ((UnisonOscillator)associatedOscillator).setSpread(unisonSpreadKnob.getValue());
                    ((UnisonOscillator)associatedOscillator).setBlend(unisonBlendKnob.getValue());
                } else {
                    ((UnisonOscillator)associatedOscillator).setVoices(1);
                    ((UnisonOscillator)associatedOscillator).setSpread(0f);
                    ((UnisonOscillator)associatedOscillator).setBlend(0f);
                }
            });
            unisonSpreadKnob = new BlankKnob(new BlankKnob.Parameters(0f, 1f, 0.01f, false, true), BlankKnob.SMALL, 0, "Spread");
            unisonSpreadKnob.addPropertyChangeListener(e -> {
                ((UnisonOscillator) associatedOscillator).setSpread((float) e.getNewValue());
            });
            unisonBlendKnob = new BlankKnob(new BlankKnob.Parameters(0f, 2f, 0.01f, false, true), BlankKnob.SMALL, 1f, "Blend");
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

        freqEnv = new EnvelopePanel(associatedOscillator.frequencyEnvelope(), "FREQUENCY ENVELOPE");
        ampEnv = new EnvelopePanel(associatedOscillator.gainEnvelope(), "AMPLITUDE ENVELOPE");

        freqLFO = new LFOPanel(associatedOscillator.frequencyLFO(), "FREQUENCY LFO");
        ampLFO = new LFOPanel(associatedOscillator.gainLFO(), "AMPLITUDE LFO");

        modulationPanel.add(freqEnv);
        modulationPanel.add(freqLFO);
        modulationPanel.add(ampEnv);
        modulationPanel.add(ampLFO);

        this.createFineTunePanel();

        pane.add(optionsPane);

        this.initializeFromOscillator();
    }

    public void hide(){
        this.pane().setVisible(false);
    }

    public void show(){
        this.pane().setVisible(true);
    }

    private void createFineTunePanel(){
        this.optionsPane = new BlankPanel();
        this.optionsPane.setLayout(new BoxLayout(optionsPane, BoxLayout.Y_AXIS));
        this._pane = new BlankPanel(new GridLayout(6, 2));
        this._headline = new BlankLabel("Fine Tuning");
        this._headline.setFont(new Font("Fira Mono", Font.BOLD, 16));
        optionsPane.add(_headline);
        optionsPane.add(_pane);
        this._frequencyLabel = new BlankLabel("Frequency");
        this._frequency = new BlankSpinner(new SpinnerNumberModel(associatedOscillator.frequencyStatic().getValue(), 0.0, 22000.0, 1));
        this._frequency.addChangeListener(e -> {
            this.frequencyKnob.setValue(Float.parseFloat(_frequency.getValue() + ""));
        });
        _pane.add(_frequencyLabel);
        _pane.add(_frequency);
        this._gainLabel = new BlankLabel("Gain");
        this._gain = new BlankSpinner(new SpinnerNumberModel(associatedOscillator.gainStatic().getValue(), 0.0, 1.0, 0.01));
        this._gain.addChangeListener(e -> {
            this.gainKnob.setValue(Float.parseFloat(_gain.getValue() + ""));
        });
        _pane.add(_gainLabel);
        _pane.add(_gain);
        if(associatedOscillator instanceof WavetableOscillator){
            this._wavetableLabel = new BlankLabel("Wavetable");
            this._wavetableSpinner = new BlankImageSpinner(new SpinnerListModel(icons));
            this._wavetableSpinner.addChangeListener(e -> {
                this.wavetableSpinner.setValue((((JSpinner)e.getSource()).getValue()));
            });
            _pane.add(_wavetableLabel);
            _pane.add(_wavetableSpinner);
        }
        if(associatedOscillator instanceof UnisonOscillator){
            this._unisonVoicesLabel = new BlankLabel("Unison Voices");
            this._unisonVoices = new BlankSpinner(new SpinnerNumberModel(((UnisonOscillator) associatedOscillator).getVoices(), 0, 8, 1));
            this._unisonVoices.addChangeListener(e -> {
                if(Integer.parseInt(this._unisonVoices.getValue() + "") > 0){
                    this.unisonEnableButton.toggle(true);
                } else {
                    this.unisonEnableButton.toggle(false);
                }
                this.unisonVoicesSlider.setValue(Integer.parseInt(this._unisonVoices.getValue()+ ""));
            });
            _pane.add(_unisonVoicesLabel);
            _pane.add(_unisonVoices);
            this._unisonSpreadLabel = new BlankLabel("Unison Spread");
            this._unisonSpread = new BlankSpinner(new SpinnerNumberModel(((UnisonOscillator) associatedOscillator).getSpread(), 0.0, 1.0, 0.01));
            this._unisonSpread.addChangeListener(e -> {
                this.unisonSpreadKnob.setValue(Float.parseFloat(this._unisonSpread.getValue() + ""));
            });
            _pane.add(_unisonSpreadLabel);
            _pane.add(_unisonSpread);
            this._unisonBlendLabel = new BlankLabel("Unison Blend");
            this._unisonBlend = new BlankSpinner(new SpinnerNumberModel(((UnisonOscillator) associatedOscillator).getBlend(), 0.0, 2.0, 0.01));
            this._unisonBlend.addChangeListener(e -> {
                this.unisonBlendKnob.setValue(Float.parseFloat(this._unisonBlend.getValue() + ""));
            });
            _pane.add(_unisonBlendLabel);
            _pane.add(_unisonBlend);
        }
        optionsPane.setVisible(false);
    }

    private void showFineTunePanel(){
        optionsPane.setVisible(true);
    }

    private void hideFineTunePanel(){
        optionsPane.setVisible(false);
    }

    private void initializeFromOscillator(){
        this.frequencyKnob.setValue(associatedOscillator.getFrequency().getValue());
        this.gainKnob.setValue(associatedOscillator.getGain().getValue());
        if(associatedOscillator instanceof WavetableOscillator){
            this.wavetableSpinner.setValue(this.setWavetableType(((WavetableOscillator)associatedOscillator).getWaveType()));
        }
        if(associatedOscillator instanceof UnisonOscillator){
            this.unisonVoicesSlider.setValue(((UnisonOscillator) associatedOscillator).getVoices());
            this.unisonSpreadKnob.setValue(((UnisonOscillator) associatedOscillator).getSpread());
            this.unisonBlendKnob.setValue(((UnisonOscillator) associatedOscillator).getBlend());
            this.unisonEnableButton.toggle(true);
        }
    }

    public Oscillator getAssociatedDevice() {
        return associatedOscillator;
    }

    private WaveType setWavetableType(WavetableOscillator.WaveType waveType){
        switch(waveType){
            case SINE : return icons[0];
            case TRIANGLE : return icons[1];
            case SAW : return icons[2];
            case SQUARE : return icons[3];
            case NOISE : return icons[4];
            default : return icons[0];
        }
    }

    public JPanel pane(){
        return this.pane;
    }
}
