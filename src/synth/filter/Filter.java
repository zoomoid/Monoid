package synth.filter;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.ugens.BiquadFilter;
import net.beadsproject.beads.ugens.Envelope;
import net.beadsproject.beads.ugens.Gain;

public class Filter extends UGen {

    private float staticCutoff;
    private float staticGain;
    private float q;

    private UGen ugenCutoff;
    private UGen ugenGain;

    private boolean isStaticCutoff, isStaticGain;

    private BiquadFilter filterBackend;

    private Gain output;

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
        super(ac);
        this.staticCutoff = staticCutoff;
        this.q = q;
        this.staticGain = staticGain;
        filterBackend = new BiquadFilter(ac, filterType, staticCutoff, q);
        filterBackend.setGain(staticGain);
        this.isStaticCutoff = true;
        this.isStaticGain = false;
        this.output = new Gain(ac, 1, staticGain);
        this.output.addInput(filterBackend);
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
        super(ac);
        this.staticCutoff = staticCutoff;
        this.q = q;
        this.ugenGain = gain;
        filterBackend = new BiquadFilter(ac, filterType, staticCutoff, q);
        filterBackend.setGain(gain);
        this.isStaticCutoff = true;
        this.isStaticGain = false;
        this.output = new Gain(ac, 1, gain);
        this.output.addInput(filterBackend);
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
        super(ac);
        this.ugenCutoff = cutoff;
        this.q = q;
        this.staticGain = staticGain;
        filterBackend = new BiquadFilter(ac, filterType, cutoff, q);
        filterBackend.setGain(staticGain);
        this.isStaticCutoff = true;
        this.isStaticGain = false;
        this.output = new Gain(ac, 1, staticGain);
        this.output.addInput(filterBackend);
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
        super(ac);
        this.ugenCutoff = cutoff;
        this.q = q;
        this.ugenGain = gain;
        filterBackend = new BiquadFilter(ac, filterType, cutoff, q);
        filterBackend.setGain(gain);
        this.isStaticCutoff = true;
        this.isStaticGain = false;
        this.output = new Gain(ac, 1, gain);
        this.output.addInput(filterBackend);
    }

    public void setCutoff(float frequency){
        this.staticCutoff = frequency;
        this.isStaticCutoff = true;
        filterBackend.setFrequency(frequency);
    }

    public void setCutoff(UGen frequency){
        this.ugenCutoff = frequency;
        this.isStaticCutoff = false;
        filterBackend.setFrequency(frequency);
    }

    public void setQ(float q) {
        this.q = q;
        filterBackend.setQ(q);
    }

    public void setGain(float staticGain){
        this.staticGain = staticGain;
        this.isStaticGain = true;
        output.setGain(staticGain);
    }

    public void setGain(UGen gain){
        this.ugenGain = gain;
        this.isStaticGain = false;
        output.setGain(gain);
    }

    @Override
    public void calculateBuffer() {
        zeroOuts();
        for(int i = 0; i < outs; i++){
            bufOut[i] = filterBackend.getOutBuffer(i);
        }
    }

    @Override
    public void addInput(UGen i){
        filterBackend.addInput(i);
    }

    public UGen output(){
        return output;
    }
}
