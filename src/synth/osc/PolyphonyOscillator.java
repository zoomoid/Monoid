package synth.osc;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.data.Buffer;

public class PolyphonyOscillator extends UGen {

    /**
     * Oscillator Frequencies
     */
    private float[] frequencies;
    /**
     * Oscillator Buffer
     */
    private Buffer buffer;

    /**
     * individual gains for each frequency
     */
    private float[] gains;

    /**
     * The wave length to increment the buffer
     */
    private double[] increment;

    /**
     * The state each voice is currently in
     */
    private float[] point;

    private static boolean randomPhaseOffset = true;

    /**
     * Creates a blank PolyphonyOscillator
     * @param ac the audio context
     */
    public PolyphonyOscillator(AudioContext ac){
        this(ac, new float[0], new float[0], Buffer.SINE);
    }

    /**
     * Creates a sine PolyphonyOscillator
     * @param ac the audio context
     * @param frequencies frequency array
     */
    public PolyphonyOscillator(AudioContext ac, float[] frequencies){
        this(ac, frequencies, new float[frequencies.length], Buffer.SINE);
    }

    /**
     * Creates a PolyphonyOscillator
     * @param ac the audio context
     * @param frequencies frequency array
     * @param buffer wave buffer
     */
    public PolyphonyOscillator(AudioContext ac, float[] frequencies, float[] gains, Buffer buffer){
        super(ac);
        this.frequencies = frequencies;
        this.buffer = buffer;
        this.gains = gains;
        this.increment = new double[frequencies.length];
        this.point = new float[frequencies.length];

    }

    /**
     * Gets the current wave buffer
     * @return Buffer type
     */
    public Buffer getBuffer() {
        return buffer;
    }

    /**
     * Gets the frequencies currently playing
     * @return frequency array
     */
    public float[] getFrequencies() {
        return frequencies;
    }

    public float[] getGains() {
        return gains;
    }

    /**
     * Sets a new wave buffer
     * @param buffer wave buffer
     */
    public void setBuffer(Buffer buffer) {
        this.buffer = buffer;
    }

    public void setGains(float[] gains) {
        this.gains = gains;
    }

    /**
     * Sets the frequencies
     * @param frequencies frequency array
     */
    public void setFrequencies(float[] frequencies) {
        this.frequencies = frequencies;
        for(int i = 0; i < frequencies.length; i++){
            increment[i] = frequencies[i] / context.getSampleRate();
            point[i] = (randomPhaseOffset ? (float)Math.random() : 0f);
        }
    }

    /**
     * Sets frequencies and gains. v[0] corresponds to the frequency, v[1] to the gain value for that voice
     * @param v two-dimensional float array containing frequencies and gains
     */
    public void setFrequenciesAndGains(float[][] v){
        this.setFrequencies(v[0]);
        this.setGains(v[1]);
    }

    /* (non-Javadoc)
     * @see com.olliebown.beads.core.UGen#calculateBuffer()
     */
    @Override
    public void calculateBuffer() {
        zeroOuts();
        for(int i = 0; i < bufferSize; i++) {
            for(int j = 0; j < frequencies.length; j++) {
                // step forward in phase (in [0,1])
                point[j] = (float)(point[j] + increment[j]) % 1f;
                bufOut[0][i] += gains[j] * buffer.getValueFraction(point[j]);
            }
            /*bufOut[0][i] *= gain;*/
        }
    }
}
