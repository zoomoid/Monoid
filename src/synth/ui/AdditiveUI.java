package synth.ui;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.Panner;
import net.beadsproject.beads.ugens.RangeLimiter;
import synth.auxilliary.BufferDebugger;
import synth.osc.BasicOscillator;
import synth.osc.Oscillator;
import synth.ui.components.swing.BlankPanel;
import synth.ui.composition.OscillatorPanel;

import java.awt.*;
import java.util.ArrayList;

public class AdditiveUI {
    /**The Audio Context */
    AudioContext ac;

    public enum Preset {
        DEFAULT, SAW, SQUARE, TRIANGLE
    }

    /**Preset fields*/
    public static final Preset DEFAULT = Preset.DEFAULT;
    public static final Preset SAW = Preset.SAW;
    public static final Preset SQUARE = Preset.SQUARE;
    public static final Preset TRIANGLE = Preset.TRIANGLE;

    public int numberOfOscillators;

    /**List of active oscillators*/
    ArrayList<Oscillator> activeOscillators = new ArrayList<Oscillator>();

    Gain compensator;
    RangeLimiter limiter;

    BufferDebugger beforeGain, afterGain;

    /**structure to combine oscillator and oscillatorPanel*/
    class OscController {
        BasicOscillator osc;
        OscillatorPanel panel;

        public OscController(BasicOscillator osc) {
            this.osc = osc;
            panel = new OscillatorPanel(osc);
        }

        public void setOsc(BasicOscillator osc) {
            this.osc = osc;
            panel = new OscillatorPanel(osc);
        }

        public BasicOscillator getOscillator() {
            return this.osc;
        }
    }

    public BlankPanel contentPane;
    private GridLayout grid;

    /**
     * Basic Constructor for AdditiveUI Element
     * @param numberOfOscillators number of oscillators to be used
     * @param ac the audio context
     * @param param which type of preset
     */
    public AdditiveUI(int numberOfOscillators, AudioContext ac, Preset param) {
        this(numberOfOscillators, ac, param, 220f);
    }

    /**
     * Basic Constructor with basic frequency on top
     * @param numberOfOscillators
     * @param ac
     * @param param
     * @param basicFreq
     */
    public AdditiveUI(int numberOfOscillators, AudioContext ac, Preset param, float basicFreq) {
        limiter = new RangeLimiter(ac, 2);
        compensator = new Gain(ac, 2, (1f / (float) numberOfOscillators));

        this.numberOfOscillators = numberOfOscillators;
        contentPane = new BlankPanel();
        this.ac = ac;

        beforeGain = new BufferDebugger(ac);
        beforeGain.setName("Before");
        compensator.addInput(beforeGain);
        afterGain = new BufferDebugger(ac);
        afterGain.addInput(compensator);
        afterGain.setName("After");
        Panner panner = new Panner(ac);
        panner.addInput(afterGain);
        this.ac.out.addInput(panner);

        setupOscillators(basicFreq, numberOfOscillators, param);

        grid = new GridLayout(numberOfOscillators, 1, 5, 5);
        contentPane.setLayout(grid);
        contentPane.updateUI();
    }

    /**
     * Sets up a set of numberOfOscillators oscillators
     * @param basicFreq basic frequency
     * @param numberOfOscillators number of oscillators
     * @param param the chosen preset
     */
    private void setupOscillators(float basicFreq, int numberOfOscillators, Preset param) {
        for(int i = 0; i < numberOfOscillators; i++) {
            BasicOscillator bscOsc = new BasicOscillator(this.ac, basicFreq, Buffer.SINE);
            activeOscillators.add(bscOsc);
            switch(param) {
                default: case DEFAULT:
                    if(i == 0) {
                        bscOsc.setGain(0.5f);
                        bscOsc.setFrequency(basicFreq);
                    } else {
                        bscOsc.setGain(0f);
                        bscOsc.setFrequency(basicFreq);
                    }
                    break;
                case SAW:
                    bscOsc.setGain((float) 1 / (float) (i + 1));
                    bscOsc.setFrequency(basicFreq * (float) (i + 1));
                    break;
                case SQUARE:
                    bscOsc.setGain((float) 1 / (i * 2 + 1f));
                    bscOsc.setFrequency(basicFreq * (i * 2 + 1f));
                    break;
                case TRIANGLE:
                    bscOsc.setGain((float) 1 / ((i * 2 + 1f) * (i * 2 + 1f)) );
                    bscOsc.setFrequency(basicFreq * (i * 2 + 1f));
                    break;
            }

            OscController oscCon = new OscController(bscOsc);
            //add graphic
            contentPane.add(oscCon.panel);
            // chain oscillator to gain compensator
            beforeGain.addInput(bscOsc);
        }

    }

    public int getNumberOfOscillators() {
        return numberOfOscillators;
    }

    public void killAllOscillators() {
        for(Oscillator osc : activeOscillators) {
            osc.kill();
        }
    }
}
