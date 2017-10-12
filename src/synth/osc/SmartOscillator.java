package synth.osc;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.OscillatorBank;
import net.beadsproject.beads.ugens.WavePlayer;

public class SmartOscillator extends Oscillator {

    private float spread;
    private int voices;
    private float blend;
    private float spreadFunction;

    private float unisonGainCompensation;

    private Buffer wave;

    private UnisonOscillator unison;
    private WavePlayer center;

    private final boolean RANDOM_PHASE_OFFSET = true;

    private Gain cGain, sGain;

    public SmartOscillator(AudioContext ac){
        this(ac, 0f, Buffer.SINE, 1, 0f, 0f);
    }

    public SmartOscillator(AudioContext ac, float frequency){
        this(ac, frequency, Buffer.SINE, 1, 0f, 0f);
    }

    public SmartOscillator(AudioContext ac, Buffer wave){
        this(ac, 440f, wave, 1, 0f, 0f);
    }

    public SmartOscillator(AudioContext ac, float frequency, Buffer wave){
        this(ac, frequency, wave, 1, 0f, 0f);
    }

    public SmartOscillator(AudioContext ac, float frequency, Buffer wave, int voices, float spread){
        this(ac, frequency, wave, voices, spread, 1f);
    }

    public SmartOscillator(AudioContext ac, float frequency, Buffer wave, int voices, float spread, float blend){
        super(ac, frequency);
        this.spread = spread;
        this.voices = voices;
        this.wave = wave;
        this.spreadFunction = 1;
        this.blend = blend;
        unison = null;
        center = null;
        this.unisonGainCompensation = 1 / (float)voices;
    }


    public Buffer getWave() {
        return wave;
    }

    public float getBlend() {
        return blend;
    }

    public int getVoices() {
        return voices;
    }

    public float getSpread() {
        return spread;
    }

    public float getSpreadFunction() {
        return spreadFunction;
    }

    public UnisonOscillator getUnison() {
        return unison;
    }

    public WavePlayer getCenter() {
        return center;
    }

    /**
     * Sets the unison blend ratio
     * @param blend ratio in [0,1]
     */
    public void setBlend(float blend) {
        if(blend <= 1 && blend >= 0){
            // no need for super.hasChanged here, since it is just adjusting volume of two existing outputs
            this.blend = blend;
            cGain.setGain((float)(1-0.5*this.blend) * this.unisonGainCompensation);
            sGain.setGain((float)(0.5*this.blend));
        }
    }

    /**
     * Sets the unison pitch spread
     * @param spread center offset in Hz
     */
    public void setSpread(float spread) {
        // no need for super.hasChanged, since OscillatorBank provides setter for frequencies of unison voices
        this.spread = spread;
        this.unison.setFrequencies(this.calculateUnisonPitch());
    }

    /**
     * Sets the number of voices
     * @param voices number of oscillator voices. 1 means there will be no unison voices
     */
    public void setVoices(int voices) {
        if(voices >= 1){
            // this needs super.hasChanged since oscillators need to be recreated.
            hasChanged = true;
            this.voices = voices;
            this.unisonGainCompensation = 1 / (float)voices;
            super.changed();
        }
    }

    /**
     * Sets the current {@link Buffer} for the oscillator
     * @param wave waveform as buffer
     */
    public void setWave(Buffer wave) {
        // this needs super.hasChanged since oscillators need to be recreated.
        hasChanged = true;
        this.wave = wave;
        super.changed();
    }

    /**
     * Sets the unison spread function in form of an exponent
     * @param spreadFunction exponent for pitch offset calculation
     */
    public void setSpreadFunction(float spreadFunction) {
        // no need for super.hasChanged since simply the unison frequencies get recalculated
        this.spreadFunction = spreadFunction;
        this.unison.setFrequencies(this.calculateUnisonPitch());
    }

    /**
     * Overrides {@link Oscillator}'s setFrequency since for this Oscillator, there is no need to recreate the voices
     * @param frequency frequency in Hz
     */
    @Override
    public void setFrequency(float frequency){
        this.frequency = frequency;
        this.updateFrequency();
    }

    /**
     * Creates voices, either with unison or without
     */
    @Override
    void createOscillator(){
        unison = new UnisonOscillator(super.ac, this.wave, this.voices - 1);
        center = new WavePlayer(super.ac, super.frequency, this.wave);
    }

    /**
     * Updates the frequency of each voice
     */
    @Override
    void updateFrequency() {
        this.unison.setFrequencies(this.calculateUnisonPitch());
        this.center.setFrequency(super.frequency);
    }

    /**
     * Routes center voice to center output and unison voices to unison output.
     * Also calculates the blend ratio for the gain adjustments
     */
    @Override
    void patchOutputs() {
        cGain = new Gain(super.ac,2,1f);
        sGain = new Gain(super.ac,2,1f);

        cGain.addInput(center);
        sGain.addInput(unison);

        this.setBlend(this.blend);
        super.output.addInput(cGain);
        if(voices > 1)
            super.output.addInput(sGain);
    }


    /**
     * Calculates the unison pitch offset
     * @return array containing absolute frequency values
     */
    private float[] calculateUnisonPitch(){
        float[] r = new float[voices];
        // absolute starting point of the linear spread
        float x = super.frequency - this.spread;
        // note: case "voices = 1" is covered by this, so no need for compensation as in WaveOscillator
        for(int i = 0; i < voices; i++){
            r[i] = (float)(x + (i/Math.pow(voices-1f, spreadFunction)) * 2 * spread);
            System.out.print(r[i] + " Hz\t");
        }
        System.out.print("\n");
        return r;
    }

    public void kill(){
        if(center != null){
            center.kill();
            cGain.kill();
        }
        if(unison != null) {
            unison.kill();
            sGain.kill();
        }
        output.pause(true);
        isPaused = true;
    }

    public void start(){
        this.pause(false);
    }

    public void pause(){
        this.pause(true);
    }

    private void pause(boolean paused){
        if(isPaused != paused){
            if(center != null)
                center.pause(paused);
            if(unison != null)
                unison.pause(paused);
            output.pause(paused);
            isPaused = paused;
        }

    }
}
