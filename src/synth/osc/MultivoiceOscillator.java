package synth.osc;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Mult;
import synth.modulation.Modulatable;

import synth.modulation.Static;

public class MultivoiceOscillator extends Oscillator {

    /** The array of frequencies of individual oscillators. */
    private BasicOscillator[] backend;

    private Modulatable[] frequency;
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
        this.backend = new BasicOscillator[0];
        this.frequency = new Modulatable[0];
        this.phase = new float[0];
        if(numOscillators > 0){
            setNumOscillators(numOscillators);
            this.gain.setValue(1f / (float)numOscillators);
        } else {
            this.gain.setValue(0f);
        }
    }


    MultivoiceOscillator(AudioContext context, SmartOscillator dependent, Buffer buffer, int numOscillators){
        this(context, buffer, numOscillators);
        this.dependent = dependent;
        this.addDependent(this.dependent);
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
        int p = this.backend.length;
        BasicOscillator[] old = this.backend;
        this.backend = new BasicOscillator[numOscillators];
        for(int i = 0; i < numOscillators; i++){
            if(i < p){
                this.backend[i] = old[i];
            } else {
                this.backend[i] = new BasicOscillator(ac, 0f, this.buffer);
            }
        }

        updateFrequency();
        updatePhase();
        // Update gain to evenly scale each oscillation to 1
        this.gain.setValue(1f/numOscillators);

        this.update();
    }

    private void updateFrequency() {
        Modulatable[] old = (this.frequency != null ? this.frequency : new Modulatable[numOscillators]);
        this.frequency = new Modulatable[numOscillators];
        for(int i = 0; i < numOscillators; i++) {
            if(i < old.length && old[i] != null){
                this.frequency[i] = old[i];
            } else {
                // new frequencies new voices is not yet set
                this.frequency[i] = new Static(this.context, 0f);
            }
        }
        for(int i = 0; i < numOscillators; i++){
            this.backend[i].setFrequency(this.frequency[i]);
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
        for(int i = 0; i < numOscillators; i++){
            this.backend[i].setPhase(this.phase[i]);
        }
    }

    /**
     * Sets the frequencies of all oscillators.
     *
     * @param frequency the new frequencies.
     */
    public void setFrequencies(Modulatable[] frequency) {
        for(int i = 0; i < numOscillators; i++) {
            if(i < frequency.length) {
                this.frequency[i] = frequency[i];
            } else {
                this.frequency[i].setValue(0f);
            }
        }
    }

    /**
     * Gets the array of frequencies.
     * @return array of frequencies.
     */
    public Modulatable[] getFrequencies() {
        return frequency;
    }

    /* (non-Javadoc)
     * @see com.olliebown.beads.core.UGen#calculateBuffer()
     */
    @Override
    public synchronized void calculateBuffer() {
        zeroOuts();
        this.gain.update();
        for (int n = 0; n < numOscillators; n++) {
            frequency[n].update();
            backend[n].update();
        }

        for(int j = 0; j < bufferSize; j++) {
            for(int k = 0; k < numOscillators; k++) {
                // step forward in phase (in [0,1])
                phase[k] = (float)(((phase[k] + frequency[k].getValue(0, j) * one_over_sr) % 1.0f) + 1.0f) % 1.0f;
                for(int i = 0; i < outs; i++) {
                    float sample = buffer.getValueFraction(phase[k]);
                    bufOut[i][j] += gain.getValue(i, j) * sample;
                }
            }
        }

    }


}