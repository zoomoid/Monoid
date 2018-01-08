package tests.ui;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.data.Pitch;
import net.beadsproject.beads.ugens.RangeLimiter;
import synth.auxilliary.ContextProvider;
import synth.filter.Filter;
import synth.filter.models.BiquadFilter;
import synth.osc.SmartOscillator;
import synth.ui.AdditiveUIProvider;
import synth.ui.FilterUI;
import synth.ui.OscillatorUI;
import synth.ui.components.swing.BlankToggleButton;

import javax.swing.*;
import java.awt.*;

public class LauncherLayoutTest {
    public static AudioContext ac = ContextProvider.ac();

    /**The frame*/
    private static JFrame frame;

    /**All the used Panes*/
    private static JPanel mainPane;
    private static JPanel tabPane;

    //preset Panes
    private static JPanel oscPreset;
    private static JPanel oscAndFilterPreset;
    private static JPanel amPreset;
    private static JPanel fmPreset;

    /**Toggle buttons for tabs*/
    private static BlankToggleButton oscButton;
    private static BlankToggleButton addSynthButton;
    private static BlankToggleButton oscAndFilter;
    private static BlankToggleButton amSynth;
    private static BlankToggleButton fmSynth;

    private static BlankToggleButton currSelected;
    private static JPanel currPresetPane;
    private static JPanel currSynthPane;

    /**Gives us ability to close these frames*/
    private static JFrame oscFrame;
    private static JFrame filterFrame;

    /**Providers*/
    private static AdditiveUIProvider addUIprovider;

    public static void main(String args[]) {
        frame = new JFrame("Monoid");
        mainPane = new JPanel();

        //setup tab pane
        tabPane = new JPanel();
        //and buttons for tab pane
        setupButtons();
        //and presetPanes
        setupPresetPanes();

        currSynthPane = new JPanel();

        //set Layout of tab pane
        tabPane.add(oscButton);
        tabPane.add(oscAndFilter);
        tabPane.add(addSynthButton);
        tabPane.add(amSynth);
        tabPane.add(fmSynth);
        tabPane.setLayout(new GridLayout(1, 5, 5, 5));

        //add panes to main pane
        mainPane.add(tabPane);
        mainPane.add(oscPreset);
        mainPane.add(loadSynthPane(oscButton));
        //setup layout
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));

        frame.setContentPane(mainPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.pack();
        frame.setVisible(true);
    }

    public static void changeButton(BlankToggleButton button) {
        if(!(currSelected == button)) {
            ac.stop();
            ac = ContextProvider.ac();
            currSelected.toggle();
            currSelected = button;
            mainPane.remove(currPresetPane);
            mainPane.remove(currSynthPane);

            oscFrame.setVisible(false);
            filterFrame.setVisible(false);

            currPresetPane = loadPresetPane(button);
            mainPane.add(currPresetPane);
            currSynthPane = loadSynthPane(button);
            mainPane.add(currSynthPane);
            frame.pack();
        } else {
            button.toggle();
        }
    }

    public static JPanel loadPresetPane(BlankToggleButton button) {
        //select right preset pane
        if(button == oscButton) {
            return oscPreset;
        } else if(button == oscAndFilter) {
            return oscAndFilterPreset;
        } else if(button == addSynthButton) {
            return new JPanel();
        } else if(button == amSynth) {
            return amPreset;
        } else if(button == fmSynth) {
            return fmPreset;
        }
        return new JPanel();
    }

    public static JPanel loadSynthPane(BlankToggleButton button) {
        //select right synth pane
        if(button == oscButton) {
            //method as gotten from test
            ac.start();
            SmartOscillator osc = new SmartOscillator(ac);
            osc.setFrequency(Pitch.mtof(48));
            osc.setBlend(1);
            osc.setSpread(0);
            osc.setVoices(1);
            osc.setWave(Buffer.SINE);

            RangeLimiter l = new RangeLimiter(ac,1);
            l.addInput(osc);
            ac.out.addInput(l);

            OscillatorUI oUI = new OscillatorUI(osc);
            oUI.show();
            //end of method

            oscFrame = oUI.ui;
            filterFrame = new JFrame();

            return new JPanel();
        } else if(button == oscAndFilter) {
            //method as gotten from test
            ac.start();

            SmartOscillator osc = new SmartOscillator(ac, Pitch.mtof(57), Buffer.SINE, 1, 0f);
            osc.setName("Oscillator A");

            Filter f = new Filter(ac, Filter.Type.BiquadFilter, BiquadFilter.Type.LPF, 300f, 0.76f, 0.71f);
            f.setName("Filter A");
            f.addInput(osc);

            ac.out.addInput(f);
            ac.out.setGain(0.66f);
            OscillatorUI oUI = new OscillatorUI(osc);
            oUI.show();

            FilterUI fUI = new FilterUI(f);
            fUI.show();
            //method end

            oscFrame = oUI.ui;
            filterFrame = fUI.ui;

            return new JPanel();
        } else if(button == addSynthButton) {
            JPanel additiveSynth = new AdditiveUIProvider(ac, frame).mainPane;
            return additiveSynth;
        } else if(button == amSynth) {
            return new JPanel();
        } else if(button == fmSynth) {
            return new JPanel();
        }
        return new JPanel();
    }

    public static void setupButtons() {
        oscButton = new BlankToggleButton("Oscillator");
        oscAndFilter = new BlankToggleButton("Osc. and Filter");
        addSynthButton = new BlankToggleButton("Additive Oscillator");
        amSynth = new BlankToggleButton("AM");
        fmSynth = new BlankToggleButton("FM");

        //start with standard synthesizer
        currSelected = oscButton;
        oscButton.toggle();

        //add action listeners to buttons
        oscButton.addActionListener(e -> {
            changeButton(oscButton);
        });

        addSynthButton.addActionListener(e -> {
            changeButton(addSynthButton);
        });

        oscAndFilter.addActionListener(e -> {
            changeButton(oscAndFilter);
        });

        amSynth.addActionListener(e -> {
            changeButton(amSynth);
        });

        fmSynth.addActionListener(e -> {
            changeButton(fmSynth);
        });
    }

    public static void setupPresetPanes() {
        oscPreset = new JPanel();
        currPresetPane = oscPreset;
        oscAndFilterPreset = new JPanel();
        amPreset = new JPanel();
        fmPreset = new JPanel();
    }
}
