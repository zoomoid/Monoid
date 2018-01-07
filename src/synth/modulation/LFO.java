package synth.modulation;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.data.Buffer;

import java.util.Objects;

/**
 * A LFO is a modulation device for parameters of a synthesizer. It inherits UGens, meaning it can be widely used in
 * parameters of other UGen implementations
 * This LFO allows frequencies up to 100Hz an sync-on-bpm modes up to 1/64 beats
 * Note, that the amplitude of the LFO not directly determines the amount of modulation to a given parameter but rather
 * is a factor by which the modulation amount gets multiplied
 */
public class LFO extends Modulator implements Modulatable {

    /** Audio Context */
    protected AudioContext context;
    /** Available types indicator */
    public enum Type {
        SINE, TRIANGLE, SAW, SQUARE, NOISE, CUSTOM_TYPE
    }

    public enum Mode {
        RETRIGGER, FREE
    }
    /** LFO waveform buffer */
    protected Buffer buffer;
    /** LFO waveform type indicator */
    protected Type type;
    /** LFO mode */
    protected Mode mode;
    /** LFO frequency */
    protected float frequency;
    /** LFO amplitude */
    protected float amplitude;
    /** The playback point in the Buffer, expressed as a fraction. double for more precision*/
    protected double phase;
    /** current sampling rate with which the device got initialized */
    protected float one_over_sr;

    protected float gate;

    public LFO(AudioContext ac){
        this(ac, Type.SINE, 1f, 1f);
    }

    public LFO(AudioContext ac, Type type, float frequency, float amplitude){
        this(ac, type, Mode.RETRIGGER, frequency, amplitude);
    }

    public LFO(AudioContext ac, Type type, Mode mode, float frequency, float amplitude){
        super(ac);
        this.context = ac;
        if(mode == Mode.FREE){
            this.gate = 1;
        } else {
            this.gate = 0;
        }
        this.one_over_sr = 1f / context.getSampleRate();
        this.setFrequency(frequency).setAmplitude(amplitude).setType(type).setMode(mode);
    }


    /**
     * Sets the frequency of the LFO in Hz
     * TODO: Build a system to quantize the rate of the LFO to the current BPM setting in SynthController
     *       Do this by calculating f_q = (frac) * compensation * 60 * bpm
     *       with frac being a fracture (N x N) of a bar, compensation being the factor for beats/bar and bpm being
     *       the hosts bpm
     * @param frequency frequency in Hz
     * @return LFO instance
     */
    public LFO setFrequency(float frequency) {
        if(frequency > 0f && frequency < 100f)
            this.frequency = frequency;
        return this;
    }

    /**
     * Sets the amplitude of the LFO
     * NOTE: This does not indicate the ratio at which the LFO is modulating another parameter
     * @param amplitude LFO amplitude
     * @return LFO instance
     */
    public LFO setAmplitude(float amplitude) {
        if(amplitude <= 1f && amplitude >= 0f)
            this.amplitude = amplitude;
        return this;
    }

    /**
     * Sets the LFO type
     * @param lfoType LFO type enum keyword
     * @return LFO instance
     */
    public LFO setType(Type lfoType){
        this.type = lfoType;
        switch(lfoType){
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
    public LFO setType(Type customType, Buffer customBuffer){
        switch(customType){
            case CUSTOM_TYPE : this.type = Type.CUSTOM_TYPE; this.buffer = customBuffer; break;
            default : this.setType(customType); break;
        }
        return this;
    }

    public Type type() {
        return type;
    }

    public Mode mode() {
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
    public LFO setMode(Mode mode){
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
        if(this.mode == Mode.RETRIGGER){
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

    public LFO clone(){
        LFO r = new LFO(this.ac, this.type, this.mode, this.frequency, this.amplitude);
        return (LFO)(r.setModulationStrength(this.modulationStrength).setModulationMode(this.modulationMode).setCenterValue(this.centerValue));
    }

    public NormLFO normalize(){
        return new NormLFO(ac, this.type, this.mode, this.frequency, this.amplitude);
    }
}
