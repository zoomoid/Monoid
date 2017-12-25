package synth.osc;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.data.Buffer;

public class MultivoiceOscillator extends UGen {

    /** The array of frequencies of individual oscillators. */
    private float[] frequency;

    /** The array of current positions of individual oscillators. */
    private float[] phase;
    /** The array of increment rates of individual oscillators, given their frequencies. */
    private double[] increment;

    /** The buffer used by all oscillators. */
    private Buffer buffer;

    /** The number of oscillators. */
    private int numOscillators;

    /** The sample rate and master gain of the OscillatorBank. */
    private float gain;

    private double phaseStart;

    private SmartOscillator dependent;

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
        if(numOscillators > 0){
            setNumOscillators(numOscillators);
            gain = 1f / (float)numOscillators;
        } else {
            gain = 0f;
        }
    }


    MultivoiceOscillator(AudioContext context, SmartOscillator dependent, Buffer buffer, int numOscillators){
        this(context, buffer, numOscillators);
        this.dependent = dependent;
        this.addDependent(this.dependent);
    }

    public void setPhase(double phase) {
        if (phase == -1) {
            this.phaseStart = -1;
        } else {
            this.phaseStart = phase % 1.0f;
        }
        this.setNumOscillators(numOscillators);
    }

    public void setPhase(UGen phase){
        this.setPhase(phase.getValue());
    }

    /**
     * Sets the number of oscillators.
     * @param numOscillators the new number of oscillators.
     */
    public void setNumOscillators(int numOscillators) {
        this.numOscillators = numOscillators;
        float[] old = frequency;
        frequency = new float[numOscillators];
        increment = new double[numOscillators];
        int min = 0;
        if(old != null){
            min = Math.min(frequency.length, old.length);
        }
        for(int i = 0; i < min; i++) {
            frequency[i] = old[i];
            increment[i] = frequency[i] / context.getSampleRate();
        }
        for(int i = min; i < frequency.length; i++) {
            frequency[i] = 0f;
            increment[i] = frequency[i] / context.getSampleRate();
        }
        old = phase;
        phase = new float[numOscillators];
        for(int i = 0; i < min; i++) {
            if(this.phaseStart == -1){
                // -1 as phase is defined to be random phase
                float randomPhase = (float)(Math.random() * 2 * Math.PI);
                phase[i] = randomPhase;
            } else {
                phase[i] = old[i];
            }
        }
        for(int i = min; i < phase.length; i++){
            if(this.phaseStart == -1){
                // -1 as phase is defined to be random phase
                float randomPhase = (float)(Math.random() * 2 * Math.PI);
                phase[i] = randomPhase;
            } else {
                phase[i] = (float)phaseStart;
            }
        }

    }

    /**
     * Sets the frequencies of all oscillators.
     *
     * @param frequencies the new frequencies.
     */
    public void setFrequencies(float[] frequencies) {
        for(int i = 0; i < numOscillators; i++) {
            if(i < frequencies.length) {
                frequency[i] = Math.abs(frequencies[i]);
            } else {
                frequency[i] = 0f;
            }
            increment[i] = frequency[i] / context.getSampleRate();
        }
    }

    /**
     * Gets the array of frequencies.
     * @return array of frequencies.
     */
    public float[] getFrequencies() {
        return frequency;
    }

    /**
     * Gets the array of gains.
     * @return array of gains.
     */
    public float getGain() {
        return gain;
    }

    /* (non-Javadoc)
     * @see com.olliebown.beads.core.UGen#calculateBuffer()
     */
    @Override
    public void calculateBuffer() {
        zeroOuts();
        for(int i = 0; i < numOscillators; i++) {
            for(int j = 0; j < bufferSize; j++) {
                // step forward in phase (in [0,1])
                phase[i] = (float)(((phase[i] + increment[i]) % 1.0f) + 1.0f) % 1.0f;

                float sample = buffer.getValueFraction(phase[i]);

                bufOut[0][j] += gain * sample;
                if(bufOut[0][j] > 1){
                    bufOut[0][j] = 1;
                }
                bufOut[1][j] += gain * sample;
                if(bufOut[1][j] > 1){
                    bufOut[1][j] = 1;
                }
            }
        }
    }


}