package synth.ui;

import net.beadsproject.beads.data.Pitch;
import synth.auxilliary.SignalProcessor;
import synth.modulation.Modulatable;
import synth.modulation.ModulationOscillator;
import synth.modulation.Modulator;
import synth.osc.Oscillator;
import synth.ui.components.Canvas;
import synth.ui.components.swing.BlankKnob;
import synth.ui.components.swing.BlankLabel;
import synth.ui.components.swing.BlankPanel;
import synth.ui.components.swing.BlankSpinner;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.ParameterizedType;

public class ModulationUI {

    public JFrame ui;
    private BlankPanel mainPane, topPane, pane;
    private BlankPanel fmPane, amPane;
    private BlankLabel label;
    private BlankSpinner type;
    private BlankKnob amCarrierFrequency, amModulatorRatio, amModulationStrength, amGain;
    private BlankKnob fmCarrierFrequency, fmModulatorRatio, fmModulationStrength, fmGain;

    Oscillator carrier;
    ModulationOscillator amModulator;
    ModulationOscillator fmModulator;

    public ModulationUI(Oscillator carrier, ModulationOscillator amModulator, ModulationOscillator fmModulator){
        this.carrier = carrier;
        this.fmModulator = fmModulator;
        this.amModulator = amModulator;

        pane = new BlankPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

        topPane = new BlankPanel();
        topPane.setLayout(new FlowLayout(FlowLayout.LEFT));
        label = new BlankLabel("Modulation Demo");
        topPane.add(label);
        type = new BlankSpinner(new SpinnerListModel(new String[]{"AM", "FM"}));
        topPane.add(type);

        mainPane = new BlankPanel();

        fmPane = new BlankPanel();
        amPane = new BlankPanel();

        amCarrierFrequency = new BlankKnob(new BlankKnob.Parameters(0, 127, 0.25f, true, true), BlankKnob.LARGE, 57, "Frequency");
        amModulatorRatio = new BlankKnob(new BlankKnob.Parameters(0.125f, 8f, 0.1f, false, true), BlankKnob.LARGE, 0, "Ratio");
        amModulationStrength = new BlankKnob(new BlankKnob.Parameters(0, 1.5f, 0.02f, true, true), BlankKnob.LARGE, 1, "Mod");
        amGain = new BlankKnob(new BlankKnob.Parameters(0, 1, 0.01f, true, true), BlankKnob.LARGE, 1f, "Gain");

        amCarrierFrequency.addPropertyChangeListener(e -> {
            this.carrier.setFrequency(Pitch.mtof((float)e.getNewValue()));
        });

        amModulatorRatio.addPropertyChangeListener(e -> {
            this.amModulator.setFrequency(Pitch.mtof(this.amCarrierFrequency.getValue()) * (float)e.getNewValue());
        });

        amModulationStrength.addPropertyChangeListener(e -> {
            this.amModulator.setModulationStrength((float)e.getNewValue());
        });

        amGain.addPropertyChangeListener(e -> {
            this.amModulator.setValue((float)e.getNewValue());
        });


        amPane.add(amCarrierFrequency);
        amPane.add(amModulatorRatio);
        amPane.add(amModulationStrength);
        amPane.add(amGain);

        fmCarrierFrequency = new BlankKnob(new BlankKnob.Parameters(0, 127, 0.25f, true, true), BlankKnob.LARGE, 57, "Frequency");
        fmModulatorRatio = new BlankKnob(new BlankKnob.Parameters(0.125f, 8f, 0.1f, false, true), BlankKnob.LARGE, 0, "Ratio");
        fmModulationStrength = new BlankKnob(new BlankKnob.Parameters(0f, 1000f, 5f, true, true), BlankKnob.LARGE, 1, "Mod");
        fmGain = new BlankKnob(new BlankKnob.Parameters(0, 1, 0.01f, true, true), BlankKnob.LARGE, 1f, "Gain");

        fmCarrierFrequency.addPropertyChangeListener(e -> {
            this.fmModulator.setFrequency((float)e.getNewValue());
            this.fmModulator.setFrequency(Pitch.mtof((float)e.getNewValue()) * (float)e.getNewValue());
            this.fmModulator.setValue(Pitch.mtof((float)e.getNewValue()) * (float)e.getNewValue());
            this.carrier.setFrequency(fmModulator);
        });

        fmModulatorRatio.addPropertyChangeListener(e -> {
            this.fmModulator.setValue(Pitch.mtof(this.fmCarrierFrequency.getValue()) * (float)e.getNewValue());
            this.fmModulator.setFrequency(Pitch.mtof((float)e.getNewValue()) * (float)e.getNewValue());
        });

        fmModulationStrength.addPropertyChangeListener(e -> {
            this.fmModulator.setModulationStrength((float)e.getNewValue());
        });

        fmGain.addPropertyChangeListener(e -> {
            this.carrier.setGain((float)e.getNewValue());
        });

        fmPane.add(fmCarrierFrequency);
        fmPane.add(fmModulatorRatio);
        fmPane.add(fmModulationStrength);
        fmPane.add(fmGain);

        type.addChangeListener(e -> {
            String value = (String)((JSpinner)e.getSource()).getValue();
            if(value.equals("AM")){
                amPane.setVisible(true);
                fmPane.setVisible(false);
                carrier.setGain(amModulator);
                carrier.setFrequency(Pitch.mtof(amCarrierFrequency.getValue()));
                amGain.setValue(1f);
                amModulationStrength.setValue(0f);
                amModulatorRatio.setValue(1f);
            } else {
                amPane.setVisible(false);
                fmPane.setVisible(true);
                carrier.setFrequency(fmModulator);
                carrier.setGain(1f);
                fmCarrierFrequency.setValue(0f);
                fmGain.setValue(1f);
                fmModulationStrength.setValue(0f);
                fmModulatorRatio.setValue(1f);
            }
            ui.pack();
        });

        mainPane.add(amPane);
        mainPane.add(fmPane);

        pane.add(topPane);
        pane.add(mainPane);

        this.ui = new JFrame("Modulation Demo");
        this.ui.setContentPane(this.pane);
        this.ui.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.ui.setLocation(0,90);
        this.ui.pack();
        this.ui.setResizable(true);

        fmPane.setVisible(false);

        this.setDefault();
    }

    private void setDefault() {
        type.setValue("AM");
        amPane.setVisible(true);
        fmPane.setVisible(false);
        carrier.setGain(amModulator);
        carrier.setFrequency(Pitch.mtof(amCarrierFrequency.getValue()));
        amGain.setValue(1f);
        amModulator.setValue(1f);
        amModulationStrength.setValue(0f);
        amModulatorRatio.setValue(1f);
    }

    public void hide(){
        this.ui.setVisible(false);
    }

    public void show(){
        this.ui.setVisible(true);
    }
}
