package synth.filter.models;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.ugens.IIRFilter;
import net.beadsproject.beads.ugens.Static;

public abstract class FilterModel extends IIRFilter {

    protected UGen frequency;
    protected UGen gain;
    protected UGen q;

    float tfreq, tq, tgain;

    /** Coefficients */
    protected float a0 = 1;
    protected float a1 = 0;
    protected float a2 = 0;
    protected float b0 = 0;
    protected float b1 = 0;
    protected float b2 = 0;

    /** Constants */
    protected float samplingfreq, two_pi_over_sf, pi_over_sf;
    public static final float SQRT2 = (float) Math.sqrt(2);
    protected ValCalculator vc;

    public enum Type {LPF, HPF, BPF}

    public FilterModel(AudioContext ac){
        super(ac, 2, 2);
        samplingfreq = ac.getSampleRate();
        pi_over_sf = (float)(Math.PI / samplingfreq);
        two_pi_over_sf = 2 * pi_over_sf;
        this.outputInitializationRegime = OutputInitializationRegime.ZERO;
        vc = new ValCalculator();
    }

    public abstract FilterModel setType(Type type);

    protected class ValCalculator {
        public void calcVals() {

        }
    }

    /**
     * Sets the filter frequency to a float value. This will remove the
     * frequency UGen, if there is one.
     * @param freq The frequency.
     * @return This FilterModel instance
     */
    public FilterModel setFrequency(float freq) {
        return this.setFrequency(new Static(context, freq));
    }

    /**
     * Sets a UGen to determine the filter frequency.
     * @param frequency The frequency UGen.
     * @return This FilterModel instance
     */
    public FilterModel setFrequency(UGen frequency) {
        if(frequency != null){
            this.frequency = frequency;
            this.tfreq = frequency.getValue();
        }
        vc.calcVals();
        return this;
    }

    /**
     * Sets the filter Q-value to a float. This will remove the Q UGen if there
     * is one.
     * @param q The Q-value.
     * @return This FilterModel instance.
     */
    public FilterModel setQ(float q) {
        return this.setQ(new Static(context, q));
    }

    /**
     * Sets a UGen to determine the filter Q-value.
     * @param q The Q-value UGen.
     * @return This FilterModel instance.
     */
    public FilterModel setQ(UGen q) {
        if (q != null) {
            this.q = q;
            this.tq = this.q.getValue();
        }
        vc.calcVals();
        return this;
    }

    /**
     * Sets the filter gain to a float. This will remove the gain UGen if there
     * is one.
     * @param gain The gain in decibels (0 means no gain).
     * @return This FilterModel instance.
     */
    public FilterModel setGain(float gain) {
        return this.setGain(new Static(context, gain));
    }

    /**
     * Sets a UGen to determine the filter Q-value.
     * @param gain The gain UGen, specifying the gain in decibels.
     * @return This FilterModel instance.
     */
    public FilterModel setGain(UGen gain) {
        if(gain != null){
            this.gain = gain;
            this.tgain = gain.getValue();
        }
        vc.calcVals();
        return this;
    }
}
