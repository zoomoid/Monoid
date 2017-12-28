package synth.ui.experimental;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import synth.osc.BasicOscillator;
import synth.osc.Oscillator;
import synth.ui.OscillatorPanel;
import synth.ui.components.swing.BlankPanel;

import java.awt.*;
import java.util.ArrayList;

public class AdditiveUI {
    AudioContext ac;
    public static final int DEFAULT = 0;
    public static final int SAW = 1;
    public static final int SQUARE = 2;
    public static final int TRIANGLE = 3;

    public static int numberOfOscillators = 0;
    ArrayList<Oscillator> activeOscillators = new ArrayList<Oscillator>();

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

    public AdditiveUI(int numberOfOscillators, AudioContext ac, int param) {
        this.numberOfOscillators = numberOfOscillators;
        contentPane = new BlankPanel();
        this.ac = ac;

        setupOscillators(220f, numberOfOscillators, param);

        grid = new GridLayout(numberOfOscillators, 1, 5, 5);
        contentPane.setLayout(grid);
        contentPane.updateUI();
    }

    private void setupOscillators(float basicFreq, int numberOfOscillators, int param) {
        for(int i = 1; i <= numberOfOscillators; i++) {
            BasicOscillator bscOsc = new BasicOscillator(this.ac, basicFreq, Buffer.SINE);
            activeOscillators.add(bscOsc);

            switch(param) {
                default:
                case DEFAULT:
                    if(i == 1) {
                        bscOsc.setGain(0.2f);
                    } else {
                        bscOsc.setGain(0f);
                    }
                    break;
                case SAW:
                    bscOsc.setGain((float) 1 / (float) i);
                    bscOsc.setFrequency(basicFreq * (float) i);
                    break;
                case SQUARE:
                    bscOsc.setGain((float) 1 / (i * 2 + 1f));
                    bscOsc.setFrequency(basicFreq * (i * 2 + 1f));
                    break;
                case TRIANGLE:
                    bscOsc.setGain((float) 1 / (float) (i * 2 + 1f) * (i * 2 + 1f));
                    bscOsc.setFrequency(basicFreq * (i * 2 + 1f) * (i * 2 + 1f));
                    break;
            }

            OscController oscCon = new OscController(bscOsc);

            contentPane.add(oscCon.panel);
            this.ac.out.addInput(bscOsc);
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
