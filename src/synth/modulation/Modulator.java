package synth.modulation;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import synth.container.MidiDevice;

/**
 *
 */
public abstract class Modulator extends UGen implements MidiDevice {

    AudioContext ac;
    /** Modulation center value */
    protected float centerValue;

    /** Modulation strength parameter */
    protected float modulationStrength;

    /**
     * Modulation Direction Enum
     */
    public enum Mode {
        BIDIRECTIONAL, UNIDIRECTIONAL_UP, UNIDIRECTIONAL_DOWN
    }

    private class Bounds {
        public final float max;
        public final float min;
        public Bounds(float min, float max){
            this.min = min;
            this.max = max;
        }
    }

    /** modulation mode */
    private Mode modulationMode;

    /** maximum and minimum value for the modulated source */
    private float maxValue;
    private float minValue;

    /**
     *
     * @param ac
     */
    public Modulator(AudioContext ac){
        super(ac, 0 ,1);
        this.ac = ac;
        this.modulationMode = Mode.BIDIRECTIONAL;
        this.modulationStrength = 1;
        this.centerValue = 1;
    }

    /**
     *
     * @param min
     * @param max
     */
    public void setBounds(float min, float max) {
        if (min >= 0) {
            this.minValue = min;
        }
        if (max >= 0) {
            this.maxValue = max;
        }
    }

    /**
     *
     * @return
     */
    public Bounds getBounds(){
        return new Bounds(this.minValue, this.maxValue);
    }

    /**
     * Sets the modulation mode / direction. This is especially interesting for LFOs
     * @param mode Mode identifier
     * @return this Modulator
     */
    public Modulator setModulationMode(Mode mode){
        this.modulationMode = mode;
        return this;
    }

    /**
     * Returns the current modulation mode / direction
     * @return modulation mode
     */
    public Mode getModulationMode(){
        return this.modulationMode;
    }

    /**
     * Sets the range of the modulation
     * @param modulationStrength range of the modulation
     * @return this Modulator
     */
    public Modulator setModulationStrength(float modulationStrength){
        this.modulationStrength = modulationStrength;
        return this;
    }

    /**
     * Returns the range of the modulation
     * @return the modulation range
     */
    public float getModulationStrength(){
        return this.modulationStrength;
    }

    /**
     * Sets the center value of the modulation
     * TODO in the future, to allow infinite modulation, this will also accept UGens
     * @param centerValue centerValue
     * @return this Modulator
     */
    public Modulator setCenterValue(float centerValue){
        this.centerValue = centerValue;
        return this;
    }



    @Override
    public void calculateBuffer(){
        /*for(int i = 0; i < bufferSize; i++){
            bufOut[0][i] = modulationStrength * bufOut[0][i];
        }*/
    }

    @Override
    public void setValue(float value){
        this.centerValue = value;
    }

    public abstract Modulator clone();
}
