package synth.modulation;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;

public class FMOscillator extends Modulator implements Modulatable {
    /** Audio Context */
    private AudioContext context;
    /** Available types indicator */
    public enum Type {
        SINE, TRIANGLE, SAW, SQUARE, NOISE, CUSTOM_TYPE
    }

    public enum Mode {
        RETRIGGER, FREE
    }
    /** FMOscillator waveform buffer */
    private Buffer buffer;
    /** FMOscillator waveform type indicator */
    private FMOscillator.Type type;
    /** FMOscillator mode */
    private FMOscillator.Mode mode;
    /** FMOscillator frequency */
    private float frequency;
    /** FMOscillator amplitude */
    private float amplitude;
    /** The playback point in the Buffer, expressed as a fraction. double for more precision*/
    private double phase;
    /** current sampling rate with which the device got initialized */
    private float one_over_sr;

    private float gate;

    public FMOscillator(AudioContext ac){
        this(ac, FMOscillator.Type.SINE, 1f, 1f);
    }

    public FMOscillator(AudioContext ac, FMOscillator.Type mode, float frequency, float amplitude){
        this(ac, mode, FMOscillator.Mode.RETRIGGER, frequency, amplitude);
    }

    public FMOscillator(AudioContext ac, FMOscillator.Type lfoType, FMOscillator.Mode mode, float frequency, float amplitude){
        super(ac);
        this.context = ac;
        if(mode == FMOscillator.Mode.FREE){
            this.gate = 1;
        } else {
            this.gate = 0;
        }
        this.one_over_sr = 1f / context.getSampleRate();
        this.setFrequency(frequency).setAmplitude(amplitude).setType(lfoType).setMode(mode);
    }


    /**
     * Sets the frequency of the FMOscillator in Hz
     * @param frequency frequency in Hz
     * @return FMOscillator instance
     */
    public FMOscillator setFrequency(float frequency) {
        this.frequency = Math.abs(frequency);
        return this;
    }

    /**
     * Sets the amplitude of the FMOscillator
     * NOTE: This does not indicate the ratio at which the FMOscillator is modulating another parameter
     * @param amplitude FMOscillator amplitude
     * @return this FMOscillator instance
     */
    public FMOscillator setAmplitude(float amplitude) {
        if(amplitude <= 1f && amplitude >= 0f)
            this.amplitude = amplitude;
        return this;
    }

    /**
     * Sets the FMOscillator type
     * @param type FMOscillator type enum keyword
     * @return this FMOscillator instance
     */
    public FMOscillator setType(FMOscillator.Type type){
        this.type = type;
        switch(type){
            case SINE : this.buffer = Buffer.SINE; break;
            case TRIANGLE : this.buffer = Buffer.TRIANGLE; break;
            case SAW : this.buffer = Buffer.SAW; break;
            case SQUARE : this.buffer = Buffer.SQUARE; break;
            case NOISE : this.buffer = Buffer.NOISE; break;
            default : this.buffer = Buffer.SINE; break;
        }
        return this;
    }

    /**
     * Custom type setter for LFO type
     * @param customType Custom type enum keyword as indicator for custom buffer
     * @param customBuffer custom buffer for the LFO
     * @return this LFO instance
     */
    public FMOscillator setType(FMOscillator.Type customType, Buffer customBuffer){
        switch(customType){
            case CUSTOM_TYPE : this.type = FMOscillator.Type.CUSTOM_TYPE; this.buffer = customBuffer; break;
            default : this.setType(customType); break;
        }
        return this;
    }

    public FMOscillator.Type type() {
        return type;
    }

    public FMOscillator.Mode mode() {
        return mode;
    }

    public float frequency() {
        return frequency;
    }

    public float amplitude() {
        return amplitude;
    }

    /**
     * Sets the mode of the LFO
     * @param mode Retrigger or Free-running mode
     * @return this LFO instance
     */
    public FMOscillator setMode(FMOscillator.Mode mode){
        this.mode = mode;
        return this;
    }

    @Override
    public void setValue(float value){
        this.centerValue = value;
    }

    /**
     * TODO test implementation
     */
    @Override
    public void calculateBuffer(){
        // since frequency is (pseudo) static, we can save up calculations by doing this once
        double increment = frequency * one_over_sr;
        for (int i = 0; i < bufferSize; i++) {
            // current phase cache
            phase = (((phase + increment) % 1.0f) + 1.0f) % 1.0f;
            // since the lfo only has one output channel
            bufOut[0][i] = gate * modulationStrength * buffer.getValueFraction((float) phase) + centerValue;
        }
    }


    @Override
    public float getValue(){
        return this.centerValue;
    }

    public void noteOn(){
        // switch on gate
        this.gate = 1;
        if(this.mode == FMOscillator.Mode.RETRIGGER){
            // reset current phase to 0 to reset oscillation to start
            this.phase = 0;
        } else {
            // no-op, keep running the oscillation
        }
        this.calculateBuffer();
    }

    public void noteOff(){
        // reset buffer to 0
        this.gate = 0;
        this.calculateBuffer();
    }

    public FMOscillator clone(){
        return new FMOscillator(this.ac, this.type, this.mode, this.frequency, this.amplitude);
    }
}
