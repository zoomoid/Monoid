package synth.ui;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.RangeLimiter;
import synth.osc.BasicOscillator;
import synth.osc.Oscillator;
import synth.ui.components.swing.BlankPanel;

import java.awt.*;
import java.util.ArrayList;

public class AdditiveUI {
    /**The Audio Context */
    AudioContext ac;

    /**Preset fields*/
    public static final int DEFAULT = 0;
    public static final int SAW = 1;
    public static final int SQUARE = 2;
    public static final int TRIANGLE = 3;

    public static int numberOfOscillators = 0;

    /**List of active oscillators*/
    ArrayList<Oscillator> activeOscillators = new ArrayList<Oscillator>();

    Gain compensator;
    RangeLimiter limiter;

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
    public AdditiveUI(int numberOfOscillators, AudioContext ac, int param) {
        limiter = new RangeLimiter(ac, 2);
        compensator = new Gain(ac, numberOfOscillators, (float) 1 / (float) numberOfOscillators);

        this.numberOfOscillators = numberOfOscillators;
        contentPane = new BlankPanel();
        this.ac = ac;

        setupOscillators(220f, numberOfOscillators, param);

        this.ac.out.addInput(limiter);

        grid = new GridLayout(numberOfOscillators, 1, 5, 5);
        contentPane.setLayout(grid);
        contentPane.updateUI();
    }

    /**
     * Basic Constructor with basic frequency on top
     * @param numberOfOscillators
     * @param ac
     * @param param
     * @param basicFreq
     */
    public AdditiveUI(int numberOfOscillators, AudioContext ac, int param, float basicFreq) {
        limiter = new RangeLimiter(ac, 2);
        compensator = new Gain(ac, numberOfOscillators, (float) 1 / (float) numberOfOscillators);

        this.numberOfOscillators = numberOfOscillators;
        contentPane = new BlankPanel();
        this.ac = ac;

        setupOscillators(basicFreq, numberOfOscillators, param);

        this.ac.out.addInput(limiter);
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
    private void setupOscillators(float basicFreq, int numberOfOscillators, int param) {
        for(int i = 0; i < numberOfOscillators; i++) {
            BasicOscillator bscOsc = new BasicOscillator(this.ac, basicFreq, Buffer.SINE);
            activeOscillators.add(bscOsc);

            switch(param) {
                default:
                case DEFAULT:
                    if(i == 0) {
                        bscOsc.setGain(0.2f);
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
                    bscOsc.setGain((float) 1 / ((float) (i * 2 + 1f) * (i * 2 + 1f)) );
                    bscOsc.setFrequency(basicFreq * (i * 2 + 1f));
                    break;
            }

            OscController oscCon = new OscController(bscOsc);
            //add graphic
            contentPane.add(oscCon.panel);
            //ad limiter and compensator to whole sound
            compensator.addInput(bscOsc);
            limiter.addInput(compensator);
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
