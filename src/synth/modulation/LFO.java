package synth.modulation;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.data.Buffer;


/**
 * A LFO is a modulation device for parameters of a synthesizer. It inherits UGens, meaning it can be widely used in
 * parameters of other UGen implementations
 * This LFO allows frequencies up to 100Hz an sync-on-bpm modes up to 1/64 beats
 * Note, that the amplitude of the LFO not directly determines the amount of modulation to a given parameter but rather
 * is a factor by which the modulation amount gets multiplied
 */
public class LFO extends UGen {

    /**
     * Audio Context
     */
    private AudioContext context;

    /**
     * Available type indicator
     */
    public enum Type {
        SINE, TRIANGLE, SAW, SQUARE, NOISE, CUSTOM_TYPE
    }

    /**
     * LFO waveform buffer
     */
    private Buffer buffer;
    /**
     * LFO waveform type indicator
     */
    private Type type;
    /**
     * LFO frequency
     */
    private float frequency;
    /**
     * LFO amplitude
     */
    private float amplitude;


    public LFO(AudioContext ac){
        this(ac, Type.SINE, 1f, 1f);
    }

    public LFO(AudioContext ac, Type lfoType, float frequency, float amplitude){
        super(ac, 0, 1);
        this.context = ac;
        this.setFrequency(frequency).setAmplitude(amplitude).setType(lfoType);
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
     * @return LFO instance
     */
    public LFO setType(Type customType, Buffer customBuffer){
        switch(customType){
            case CUSTOM_TYPE : this.type = Type.CUSTOM_TYPE; this.buffer = customBuffer; break;
            default : this.setType(customType); break;
        }
        return this;
    }

    @Override
    public void calculateBuffer(){

    }

}
