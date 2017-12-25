package synth.filter;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.ugens.Static;
import synth.filter.models.BiquadFilter;
import synth.filter.models.FilterFactory;
import synth.filter.models.FilterModel;
import synth.filter.models.MonoMoog;

/**
 * A Basic 2-channel filter wrapper implementation for Monoid.
 * Different filter types can be chosen from at creation.
 */
public class Filter<M extends FilterModel> extends UGen {

    /** Quality factor of the filter */
    private float q;

    /** UGen determining the Filters cutoff frequency */
    private UGen cutoff;
    /** UGen determining the Filters output gain */
    private UGen gain;

    /** Filter implementation UGen. Handles the math of filtering. Part of the Filter Chain */
    private M filterBackend;

    /** Audio Context */
    private AudioContext context;

    /**
     * Blank allpass filter
     * @param ac audio context
     */
    public Filter(AudioContext ac){
        this(ac, M.Type.LPF, 20000f, 24f, 1);
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
    public Filter(AudioContext ac, M.Type filterType, UGen cutoff, float q, UGen gain){
        super(ac, 2, 2);
        this.context = ac;

        filterBackend = (ac, 2, filterType);
        if(cutoff != null){
            this.setCutoff(cutoff);
        } else {
            this.setCutoff(new Static(ac, 0f));
        }
        if(gain != null){
            this.setGain(gain);
        } else {
            this.setGain(new Static(ac, 1f));
        }
        this.setQ(q);
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
     * @param cutoff cutoff frequency as float
     * @return Filter instance
     */
    public Filter setCutoff(float cutoff){
        return this.setCutoff(new Static(context, cutoff));
    }

    /**
     * Sets the cutoff frequency of the filter by a arbitrary UGen
     * @param cutoff cutoff frequency determining UGen
     * @return LFO instance
     */
    public Filter setCutoff(UGen cutoff){
        if(cutoff != null){
            this.cutoff = cutoff;
            filterBackend.setFrequency(this.cutoff);
        }
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
     * @param gain output gain as float
     * @return Filter instance
     */
    public Filter setGain(float gain){
        return this.setGain(new Static(context, gain));
    }

    /**
     * Sets the output gain of the filter by an arbitrary UGen
     * @param gain output gain determining UGen
     * @return Filter instance
     */
    public Filter setGain(UGen gain){
        if(gain != null){
            this.gain = gain;
        }
        //filterBackend.setGain(this.gain);
        return this;
    }

    public void setFilterType(BiquadFilter.Type newFilterType){
        if(newFilterType != null){
            this.filterBackend.setType(newFilterType);
        }
    }

    public void calculateBuffer(){
        this.cutoff.update();
        this.filterBackend.update();
        this.gain.update();
        for(int k = 0; k < outs; k++){
            for(int i = 0; i < bufferSize; i++){
                bufOut[k][i] = gain.getValue(0, i) * filterBackend.getValue(k, i);
            }
        }
    }
}
