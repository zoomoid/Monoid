package synth.ui.experimental;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import synth.osc.BasicOscillator;
import synth.ui.OscillatorPanel;
import synth.ui.components.swing.BlankPanel;

import java.awt.*;

public class AdditiveUI {
    AudioContext ac;
    public static final int DEFAULT = 0;
    public static final int SAW = 1;
    public static final int SQUARE = 2;
    public static final int TRIANGLE = 3;

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
        contentPane = new BlankPanel();
        this.ac = ac;

        setupOscillators(220f, numberOfOscillators, param);

        grid = new GridLayout(numberOfOscillators, 1, 5, 5);
        contentPane.setLayout(grid);
        contentPane.updateUI();
    }

    private void setupOscillators(float basicFreq, int numberOfOscillators, int param) {
        switch(param) {
            default:
            case DEFAULT:
                for(int i = 0; i < numberOfOscillators; i++) {
                    BasicOscillator bscOsc = new BasicOscillator(this.ac, basicFreq, Buffer.SINE);
                    if(i == 0) {
                        bscOsc.setGain(0.2f);
                    } else {
                        bscOsc.setGain(0f);
                    }
                    OscillatorPanel oscPanel = new OscillatorPanel(bscOsc);
                    contentPane.add(oscPanel);
                    this.ac.out.addInput(bscOsc);
                }
                break;
            case SAW:
                for(int i = 0; i < numberOfOscillators; i++) {
                    BasicOscillator bscOsc = new BasicOscillator(this.ac, basicFreq * (float) i, Buffer.SINE);

                    bscOsc.setGain((float) 1 / (float) i);

                    OscillatorPanel oscPanel = new OscillatorPanel(bscOsc);
                    contentPane.add(oscPanel);
                    this.ac.out.addInput(bscOsc);
                }
                break;
            case SQUARE:
                for(int i = 0; i < numberOfOscillators; i++) {
                    BasicOscillator bscOsc = new BasicOscillator(this.ac, basicFreq * (float) i * 2, Buffer.SINE);

                    bscOsc.setGain((float) 1 / (float) i * 2);

                    OscillatorPanel oscPanel = new OscillatorPanel(bscOsc);
                    contentPane.add(oscPanel);
                    this.ac.out.addInput(bscOsc);
                }
                break;
            case TRIANGLE:
                for(int i = 0; i < numberOfOscillators; i++) {
                    BasicOscillator bscOsc = new BasicOscillator(this.ac, basicFreq * (float) i * 2, Buffer.SINE);

                    bscOsc.setGain((float) 1 / (float) (i * 2) * (i * 2));

                    OscillatorPanel oscPanel = new OscillatorPanel(bscOsc);
                    contentPane.add(oscPanel);
                    this.ac.out.addInput(bscOsc);
                }
                break;
        }
    }
}
