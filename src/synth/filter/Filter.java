package synth.filter;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.ugens.Static;
import synth.container.Device;
import synth.filter.models.BiquadFilter;
import synth.filter.models.FilterFactory;
import synth.filter.models.FilterModel;

/**
 * A Basic 2-channel filter wrapper implementation for Monoid.
 * Different filter types can be chosen from at creation.
 */
public class Filter extends UGen implements Device {

    /** Quality factor of the filter */
    private UGen q;
    /** UGen determining the Filters cutoff frequency */
    private UGen cutoff;
    /** UGen determining the Filters output gain */
    private UGen gain;

    /** Filter implementation UGen. Handles the math of filtering. Part of the Filter Chain */
    private FilterModel filterBackend;

    /** Audio Context */
    private AudioContext context;

    public enum Type {BiquadFilter, MonoMoog}

    private Type type;

    /**
     * Blank LPF filter
     * @param ac audio context
     */
    public Filter(AudioContext ac){
        this(ac, Type.BiquadFilter, FilterModel.Type.LPF, 20000f, 1f, 1);
    }

    /**
     * Creates a filter with static cutoff frequency and static filter gain
     * @param ac audio context
     * @param type filter type
     * @param filterModel filter type model
     * @param staticCutoff cutoff frequency
     * @param q filter q
     * @param staticGain filter gain
     */
    public Filter(AudioContext ac, Filter.Type type, BiquadFilter.Type filterModel, float staticCutoff, float q, float staticGain){
        this(ac, type, filterModel, new Static(ac, staticCutoff), new Static(ac, q), new Static(ac, staticGain));
    }

    /**
     * Creates a filter with dynamic cutoff frequency and filter gain
     * @param ac audio context
     * @param type filter type
     * @param cutoff cutoff frequency
     * @param q filter q
     * @param gain filter gain
     */
    public Filter(AudioContext ac, Filter.Type type, FilterModel.Type filterModel, UGen cutoff, UGen q, UGen gain){
        super(ac, 2, 2);
        this.context = ac;

        switch(type){
            case BiquadFilter:
                this.filterBackend = FilterFactory.createBiquadFilter(ac, filterModel);
                break;
            case MonoMoog:
                this.filterBackend = FilterFactory.createMonoMoog(ac, filterModel);
                break;
            default:
                break;
        }

        this.type = type;
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
        if(q != null){
            this.setQ(q);
        } else {
            this.setQ(new Static(ac, 1f));
        }
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
     * Gets the current Q UGen
     * @return Q UGen
     */
    public UGen getQ(){
        return q;
    }

    /**
     * Gets the type of the filter
     * @return type enum element
     */
    public Type getType(){
        return this.type;
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

    public Filter setQ(float q){
        return this.setQ(new Static(context, q));
    }

    /**
     * Sets the constant quality factor of the filter
     * @param q quality factor
     */
    public Filter setQ(UGen q) {
        this.q = q;
        filterBackend.setQ(q);
        return this;
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
