package synth.osc;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Mult;
import synth.modulation.Modulatable;

import synth.modulation.Static;

import java.util.stream.IntStream;

public class MultivoiceOscillator extends Oscillator {

    /** The array of frequencies of individual oscillators. */
    private Modulatable[] frequencies;
    private float[] phase;
    /** The buffer used by all oscillators. */
    private Buffer buffer;

    /** The number of oscillators. */
    private int numOscillators;

    private double phaseStart;

    private SmartOscillator dependent;

    private double one_over_sr;
    /**
     * Instantiates a new MultivoiceOscillator.
     *
     * @param context the AudioContext.
     * @param buffer the buffer used as a lookup table by the oscillators.
     * @param numOscillators the number of oscillators.
     */
    public MultivoiceOscillator(AudioContext context, Buffer buffer, int numOscillators) {
        super(context, 2);
        this.buffer = buffer;
        this.one_over_sr = 1f / context.getSampleRate();
        this.frequencies = new Modulatable[0];
        this.phase = new float[0];
        if(numOscillators > 0){
            setNumOscillators(numOscillators);
            this.gain.setValue(1f / (float)numOscillators);
        } else {
            this.gain.setValue(0f);
        }
    }

    public MultivoiceOscillator setPhase(float phase) {
        if (phase == -1) {
            this.phaseStart = -1;
        } else {
            this.phaseStart = phase % 1.0f;
        }
        this.updatePhase();
        return this;
    }

    public void setType(){
        this.type = "MultivoiceOscillator";
    }

    public MultivoiceOscillator setPhase(UGen phase){
        this.setPhase(phase.getValue());
        return this;
    }

    public void setWave(Buffer wave){
        if(wave != null){
            this.buffer = wave;
        }
    }

    /**
     * Sets the number of oscillators.
     * @param numOscillators the new number of oscillators.
     */
    public void setNumOscillators(int numOscillators) {
        this.numOscillators = numOscillators;
        updateFrequency();
        updatePhase();
        // Update gain to evenly scale each oscillation to 1
        this.gain.setValue(1f/numOscillators);
        this.update();
    }

    private void updateFrequency() {
        Modulatable[] old = (this.frequencies != null ? this.frequencies : new Modulatable[numOscillators]);
        this.frequencies = new Modulatable[numOscillators];
        for(int i = 0; i < numOscillators; i++) {
            if(i < old.length && old[i] != null){
                this.frequencies[i] = old[i];
            } else {
                // new frequencies new voices is not yet set
                this.frequencies[i] = new Static(this.context, 0f);
            }
        }
    }

    private void updatePhase() {
        // fill up phase array / shrink phase array
        float[] old = (this.phase != null ? this.phase : new float[numOscillators]);
        this.phase = new float[this.numOscillators];
        for(int i = 0; i < numOscillators; i++) {
            if (this.phaseStart == -1) {
                this.phase[i] = (float) (Math.random());
            } else {
                this.phase[i] = (float) phaseStart;
            }
        }
        /*for(int i = 0; i < numOscillators; i++){
            this.backend[i].setPhase(this.phase[i]);
        }*/
    }

    /**
     * Sets the frequencies of all oscillators.
     *
     * @param frequency the new frequencies.
     */
    public void setFrequencies(Modulatable[] frequency) {
        for(int i = 0; i < numOscillators; i++) {
            if(i < frequency.length) {
                this.frequencies[i] = frequency[i];
            } else {
                this.frequencies[i].setValue(0f);
            }
        }
    }

    /**
     * Gets the array of frequencies.
     * @return array of frequencies.
     */
    public Modulatable[] getFrequencies() {
        return frequencies;
    }

    /* (non-Javadoc)
     * @see com.olliebown.beads.core.UGen#calculateBuffer()
     */
    @Override
    public void calculateBuffer() {
        this.gain.update();
        for (int n = 0; n < numOscillators; n++) {
            frequencies[n].update();
        }
        for(int j = 0; j < bufferSize; j++) {
            this.bufOut[0][j] = 0;
            this.bufOut[1][j] = 0;
            for(int k = 0; k < numOscillators; k++) {
                // step forward in phase (in [0,1])
                float frequencySample = this.frequencies[k].getValue(0, j);
                this.phase[k] = (float)(((phase[k] + frequencySample * one_over_sr) % 1.0f) + 1.0f) % 1.0f;
                float sample = this.buffer.getValueFraction(this.phase[k]);
                float gainSample = this.gain.getValue(0,j);
                for(int i = 0; i < outs; i++) {
                    this.bufOut[i][j] += gainSample * sample;
                }
            }
        }

    }

    @Override
    public void noteOn(){
        super.noteOn();
        for (Modulatable m : this.frequencies) {
            m.noteOn();
        }
    }

    @Override
    public void noteOff(){
        super.noteOff();
        for (Modulatable m : this.frequencies) {
            m.noteOff();
        }
    }

}