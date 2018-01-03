package synth.osc;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.data.Pitch;
import org.jetbrains.annotations.NotNull;
import synth.modulation.Modulatable;
import synth.modulation.Modulator;

public class SmartOscillator extends Oscillator implements UnisonOscillator, WavetableOscillator {

    private float spread;
    private int voices;
    private float blend;
    private float spreadFunction;

    private Buffer wave;

    private MultivoiceOscillator unison;
    private BasicOscillator center;

    /**
     * Blank SmartOscillator
     * @param ac audio context
     */
    public SmartOscillator(AudioContext ac) {
        this(ac, 0f, Buffer.SINE, 1, 0f, 0f);
    }

    /**
     * SmartOscillator with only the frequency given
     * This initializes the SmartOscillator to use a sine as waveform
     * @param ac audio context
     * @param frequency oscillator freaquency
     */
    public SmartOscillator(AudioContext ac, float frequency){
        this(ac, frequency, Buffer.SINE, 1, 0f, 0f);
    }

    /**
     * SmartOscillator starting at 0Hz
     * @param ac audio context
     * @param wave oscillator waveform
     */
    public SmartOscillator(AudioContext ac, Buffer wave){
        this(ac, 440f, wave, 1, 0f, 0f);
    }

    /**
     * SmartOscillator with only the center voice
     * @param ac audio context
     * @param frequency oscillator frequency
     * @param wave oscillator waveform
     */
    public SmartOscillator(AudioContext ac, float frequency, Buffer wave){
        this(ac, frequency, wave, 1, 0f, 0f);
    }

    /**
     * SmartOscillator with unison voices
     * @param ac audio context
     * @param frequency oscillator frequency
     * @param wave oscillator waveform
     * @param voices oscillator voices
     * @param spread unison pitch spread
     */
    public SmartOscillator(AudioContext ac, float frequency, Buffer wave, int voices, float spread){
        this(ac, frequency, wave, voices, spread, 1f);
    }

    /**
     * Fully blown SmartOscillator
     * @param ac audio context
     * @param frequency oscillator frequency
     * @param wave oscillator waveform
     * @param voices oscillator voices
     * @param spread pitch spread range
     * @param blend blend factor of unison to center voice
     */
    public SmartOscillator(AudioContext ac, float frequency, Buffer wave, int voices, float spread, float blend){
        super(ac, frequency);
        // wavetable specific initialization
        this.wave = wave;
        // unison specific initialization
        this.spread = spread;
        this.voices = voices;
        this.spreadFunction = 1;
        this.blend = blend;
        // submodule initialization
        unison = new MultivoiceOscillator(ac, this.wave, this.voices);
        center = new BasicOscillator(ac, frequency, this.wave);
        // output policy
        this.outputInitializationRegime = OutputInitializationRegime.ZERO;
    }

    /**
     * Gets the (serialized) buffer of wave form of this oscillator
     * @return waveform as float array
     */
    public Buffer getWave() {
        return wave;
    }

    /**
     * Sets the type label of the oscillator
     */
    public final void setType(){
        if(this.type == null){
            this.type = "SmartOscillator";
        }
    }

    /**
     * Gets the blend ratio with which the center voice and the unison voices gets blended together
     * @return the blend ratio of center and unison in [0,1]
     */
    public float getBlend() {
        return blend;
    }

    /**
     * Gets the number of voices of this oscillator
     * This is always 1 + unison voices
     * @return number of voices as integer
     */
    public int getVoices() {
        return voices;
    }

    /**
     * Gets the pitch spread range of the unison voices
     * @return pitch spread range in float
     */
    public float getSpread() {
        return spread;
    }

    /**
     * Gets the exponent of the monomial determining the spread function
     * @return exponent as float
     */
    public float getSpreadFunction() {
        return spreadFunction;
    }

    /**
     * Gets the side MultivoiceOscillator object
     * @return unison oscillator object
     */
    public MultivoiceOscillator getUnison() {
        return unison;
    }

    /**
     * Gets the center voice / oscillator
     * @return WavePlayer oscillator
     */
    public BasicOscillator getCenter() {
        return center;
    }

    /**
     * Sets the unison blend ratio
     * @param blend ratio in [0,1]
     */
    public SmartOscillator setBlend(float blend) {
        // no need for super.hasChanged here, since this is just adjusting volume of two existing outputs
        this.blend = Math.max(Math.min(blend, 2), 0);
        return this;
    }

    /**
     * Sets the unison pitch spread
     * @param spread center offset in [0,1]
     */
    public SmartOscillator setSpread(float spread) {
        this.spread = Math.max(Math.min(spread, 1), 0);
        this.refresh();
        return this;
    }

    /**
     * Sets the number of voices
     * @param voices number of oscillator voices. 1 means there will be no unison voices
     */
    public SmartOscillator setVoices(int voices) {
        if(voices != this.voices){
            this.voices = Math.max(1, Math.min(voices, UnisonOscillator.MAX_NUM_VOICES));
            this.unison.setNumOscillators(voices);
            this.refresh();
        }
        return this;
    }

    /**
     * Sets the current {@link Buffer} for the oscillator
     * @param wave waveform as buffer
     */
    public SmartOscillator setWave(Buffer wave) {
        if(wave != null){
            this.wave = wave;
            this.center.setWave(wave);
            this.unison.setWave(wave);
            this.refresh();
        }
        return this;
    }

    /**
     * Sets the unison spread function in form of an exponent
     * @param spreadFunction exponent for pitch offset calculation
     */
    public SmartOscillator setSpreadFunction(float spreadFunction) {
        // no need for super.hasChanged since simply the unison frequencies get recalculated
        this.spreadFunction = spreadFunction;
        this.unison.setFrequencies(this.calculateUnisonPitch());
        return this;
    }

    /**
     * Overrides {@link Oscillator}'s setFrequency since for this Oscillator, there is no need to recreate the voices
     * @param frequency frequency in Hz
     */
    @Override
    public SmartOscillator setFrequency(float frequency){
        super.setFrequency(frequency);
        this.updateFrequency();
        return this;
    }

    /**
     * Creates voices, either with unison or without
     */
    public void updateOscillator(){}

    /**
     * Updates the frequency of each voice
     */
    public void updateFrequency() {
        if(unison != null && center != null){
            Modulatable[] nF = this.calculateUnisonPitch();
            this.unison.setFrequencies(nF);
            this.center.setFrequency(this.frequency);
        }
    }

    @Override
    public void calculateBuffer(){
        this.gain.update();
        this.frequency.update();
        this.unison.update();
        this.center.update();
        for(int i = 0; i < outs; i++){
            for(int j = 0; j < bufferSize; j++){
                float centerSample = this.center.getValue(i, j);
                float unisonSample = this.unison.getValue(i, j);
                float gainSample = this.gain.getValue(0, j);
                bufOut[i][j] = gainSample * (((1-0.5f*this.blend) * centerSample) + ((0.5f*this.blend) * unisonSample));
            }
        }
    }


    /**
     * Calculates the unison pitch spread of THIS oscillator
     * @return float array containing individual unison frequencies
     */
    private Modulatable[] calculateUnisonPitch(){
        Modulatable[] r = new Modulatable[this.voices];
        // calculate absolute frequency range
        float mtStart = Pitch.ftom(this.frequency.getValue()) - spread;
        // note: case "voices = 1" is covered by this, so no need for compensation as in WaveOscillator
        for(int i = 0; i < this.voices; i++){
            r[i] = this.frequency.clone();
            r[i].setValue(Pitch.mtof((float)(mtStart + (i/Math.pow(this.voices-1, this.spreadFunction)) * 2 * this.spread)));
        }
        return r;
    }

    /**
     * Calculates a unison pitch offset
     * @param voices number of unison voices
     * @param spreadFunction exponent of spread function monomial
     * @param spread spread range (Given in [0,1] * half tone step)
     * @return array containing absolute frequency values
     */
    public static Modulatable[] calculateUnisonPitch(int voices, Modulatable frequency, double spreadFunction, float spread){
        Modulatable[] r = new Modulatable[voices];
        // calculate absolute frequency range
        float mtStart = Pitch.ftom(frequency.getValue()) - spread;
        // note: case "voices = 1" is covered by this, so no need for compensation as in WaveOscillator
        for(int i = 0; i < voices; i++){
            r[i] = frequency.clone();
            r[i].setValue(Pitch.mtof((float)(mtStart + (i/Math.pow(voices-1, spreadFunction)) * 2 * spread)));
        }
        return r;
    }

    private void refresh(){
        this.pause(true);
        this.updateOscillator();
        this.updateFrequency();
        this.pause(false);
    }

    public void setPhase(int phase) {
        this.unison.setPhase(phase);
    }
}
