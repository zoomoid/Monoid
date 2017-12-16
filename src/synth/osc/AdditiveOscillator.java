package synth.osc;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.BeadArray;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.data.Buffer;

/**
 * A class where you can synthesize sounds via manual or preset driven additive synthesis. Uses as many Oscilaltors
 * using only sines to generate a sound
 */
public class AdditiveOscillator extends UGen{
    /** The AudioContext the oscillator is working in */
    protected AudioContext ac;

    /**
     * A Bead Array that holds all Oscillators used for additive synthesis
     */
    BeadArray oscillators = new BeadArray();

    /**
     * The number of oscillators used for additive synthesis
     */
    int numberOfOscs;

    /**Frequency of basic oscillator */
    float basicFreq;

    /**Basic constructor */
    public AdditiveOscillator(AudioContext ac) {super(ac);}


    /**Main constructor */
    public AdditiveOscillator(AudioContext ac, float basicFreq, int numberOfOscs) {
        super(ac);
        this.numberOfOscs = numberOfOscs;
        this.basicFreq = basicFreq;
        for(int i = 0; i < numberOfOscs; i++) {
            BasicOscillator curr = new BasicOscillator(ac, basicFreq, Buffer.SINE);
            this.oscillators.add(curr);
            if(i == 0) {
                curr.start();
            }
        }
    }

    public AdditiveOscillator(AudioContext ac, float basicFreq, int numberOfOscs, Buffer wave) {
        super(ac);
        this.numberOfOscs = numberOfOscs;
        this.basicFreq = basicFreq;
        for(int i = 0; i < numberOfOscs; i++) {
            BasicOscillator curr = new BasicOscillator(ac, basicFreq, wave);
            this.oscillators.add(curr);
            if(i == 0) {
                curr.start();
            }
        }
    }

    @Override
    public void calculateBuffer() {
        bufOut = bufIn;
    }

    public BeadArray getOscillators() {
        return oscillators;
    }

    public float getBasicFreq() {
        return basicFreq;
    }
}
