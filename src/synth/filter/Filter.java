package synth.filter;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.core.UGenChain;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.Static;
import synth.filter.models.BiquadFilter;

/**
 * A Basic 2-channel filter wrapper implementation for Monoid.
 * Different filter types can be chosen from at creation.
 */
public class Filter extends UGenChain {

    /**
     * Quality factor of the filter
     */
    private float q;

    /**
     * UGen determining the Filters cutoff frequency
     */
    private UGen cutoff;
    /**
     * UGen determining the Filters output gain
     */
    private UGen gain;

    /**
     * Filter implementation UGen. Handles the math of filtering. Part of the Filter Chain
     */
    private BiquadFilter filterBackend;

    /**
     * Output UGen. Used as a post-filter leveling device. Part of the Filter Chain
     */
    private Gain output;
    /**
     * Audio Context
     */
    private AudioContext context;

    /**
     * Blank allpass filter
     * @param ac audio context
     */
    public Filter(AudioContext ac){
        this(ac, BiquadFilter.AP, 20000f, 24f, 1);
    }

    /**
     * Creates a filter with static cutoff frequency and static filter gain
     * @param ac audio context
     * @param filterType filter type
     * @param staticCutoff cutoff frequency
     * @param q filter q
     * @param staticGain filter gain
     */
    public Filter(AudioContext ac, BiquadFilter.Type filterType, float staticCutoff, float q, float staticGain){
        this(ac, filterType, new Static(ac, staticCutoff), q, new Static(ac, staticGain));
    }

    /**
     * Creates a filter with static cutoff frequency and dynamic filter gain
     * @param ac audio context
     * @param filterType filter type
     * @param staticCutoff cutoff frequency
     * @param q filter q
     * @param gain filter gain
     */
    public Filter(AudioContext ac, BiquadFilter.Type filterType, float staticCutoff, float q, UGen gain){
        this(ac, filterType, new Static(ac, staticCutoff), q, gain);
    }

    /**
     * Creates a filter with dynamic cutoff frequency and static filter gain
     * @param ac audio context
     * @param filterType filter type
     * @param cutoff cutoff frequency
     * @param q filter q
     * @param staticGain filter gain
     */
    public Filter(AudioContext ac, BiquadFilter.Type filterType, UGen cutoff, float q, float staticGain){
        this(ac, filterType, cutoff, q, new Static(ac, staticGain));
    }

    /**
     * Creates a filter with dynamic cutoff frequency and filter gain
     * @param ac audio context
     * @param filterType filter type
     * @param cutoff cutoff frequency
     * @param q filter q
     * @param gain filter gain
     */
    public Filter(AudioContext ac, BiquadFilter.Type filterType, UGen cutoff, float q, UGen gain){
        super(ac, 2, 2);
        this.context = ac;
        filterBackend = new BiquadFilter(ac, 2,filterType);
        output = new Gain(ac, 2, gain);
        this.setCutoff(cutoff);
        this.setQ(q);
        this.setGain(gain);
        this.drawFromChainInput(filterBackend);
        output.addInput(filterBackend);
        this.addToChainOutput(output);
    }

    /**
     * Gets the current output gain UGen
     * @return output UGen
     */
    public UGen getGain() {
        return gain;
    }

    /**
     * Gets the current cutoff UGen
     * @return cutoff UGen
     */
    public UGen getCutoff() {
        return cutoff;
    }

    /**
     * Sets the cutoff frequency of the filter by a static value
     * @param frequency cutoff frequency as float
     * @return Filter instance
     */
    public Filter setCutoff(float frequency){
        return this.setCutoff(new Static(context, frequency));
    }

    /**
     * Sets the cutoff frequency of the filter by a arbitrary UGen
     * @param frequency frequency determining UGen
     * @return LFO instance
     */
    public Filter setCutoff(UGen frequency){
        if(this.cutoff == null){
            this.cutoff = frequency;
        } else {
            this.cutoff.setValue(frequency.getValue());
        }
        filterBackend.setFrequency(this.cutoff.getValue());
        return this;
    }

    /**
     * Sets the constant quality factor of the filter
     * @param q quality factor
     */
    public void setQ(float q) {
        this.q = q;
        filterBackend.setQ(q);
    }

    /**
     * Sets the output gain of the filter by a constant value
     * @param staticGain output gain as float
     * @return Filter instance
     */
    public Filter setGain(float staticGain){
        return this.setGain(new Static(context, staticGain));
    }

    /**
     * Sets the output gain of the filter by an arbitrary UGen
     * @param gain output gain determining UGen
     * @return Filter instance
     */
    public Filter setGain(UGen gain){
        if(this.gain == null){
            this.gain = gain;
        } else {
            this.gain.setValue(gain.getValue());
        }
        filterBackend.setGain(this.gain);
        return this;
    }
}
