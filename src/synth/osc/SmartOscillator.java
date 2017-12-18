package synth.osc;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.data.Pitch;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.WavePlayer;
import synth.auxilliary.MIDIUtils;

import javax.sound.midi.ShortMessage;

public class SmartOscillator extends Oscillator {

    private float spread;
    private int voices;
    private float blend;
    private float spreadFunction;

    private Buffer wave;

    private UnisonOscillator unison;
    private WavePlayer center;

    private float startPhase;

    private boolean isInitialised;

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
        this.spread = spread;
        this.voices = voices;
        this.wave = wave;
        this.spreadFunction = 1;
        this.blend = blend;
        this.startPhase = 0;
        unison = null;
        center = null;
        this.outputInitializationRegime = OutputInitializationRegime.RETAIN;
        this.isInitialised = false;
        this.createOscillator();
        this.updateFrequency();
        this.patchOutput();
    }

    /**
     * Gets the (serialized) buffer of wave form of this oscillator
     * @return
     */
    public Buffer getWave() {
        return wave;
    }

    /**
     * Gets the blend ratio with which the center voice and the unison voices gets blended together
     * @return
     */
    public float getBlend() {
        return blend;
    }

    /**
     * Gets the number of voices of this oscillator
     * This is always 1 + unison voices
     * @return
     */
    public int getVoices() {
        return voices;
    }

    /**
     * Gets the pitch spread range of the unison voices
     * @return
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
     * Gets the side UnisonOscillator object
     * @return unison oscillator object
     */
    public UnisonOscillator getUnison() {
        return unison;
    }

    /**
     * Gets the center voice / oscillator
     * @return WavePlayer oscillator
     */
    public WavePlayer getCenter() {
        return center;
    }

    /**
     * Sets the unison blend ratio
     * @param blend ratio in [0,1]
     */
    public SmartOscillator setBlend(float blend) {
        if(blend <= 1 && blend >= 0){
            // no need for super.hasChanged here, since this is just adjusting volume of two existing outputs
            this.blend = blend;
        }
        return this;
    }

    /**
     * Sets the unison pitch spread
     * @param spread center offset in Hz
     */
    public SmartOscillator setSpread(float spread) {
        this.spread = spread;
        this.needsRefresh();
        return this;
    }

    /**
     * Sets the number of voices
     * @param voices number of oscillator voices. 1 means there will be no unison voices
     */
    public SmartOscillator setVoices(int voices) {
        if(voices >= 1){
            this.voices = voices;
            this.needsRefresh();
        }
        return this;
    }

    /**
     * Sets the current {@link Buffer} for the oscillator
     * @param wave waveform as buffer
     */
    public SmartOscillator setWave(Buffer wave) {
        this.wave = wave;
        this.needsRefresh();
        return this;
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
    public SmartOscillator setFrequency(float frequency){
        super.setFrequency(frequency);
        this.updateFrequency();
        return this;
    }

    /**
     * Creates voices, either with unison or without
     */
    @Override
    public void createOscillator(){
        unison = new UnisonOscillator(super.ac, this.wave, this.voices - 1);
        unison.setPhase(this.startPhase);
        center = new WavePlayer(super.ac, super.frequency, this.wave);
        center.setPhase(this.startPhase);
        this.isInitialised = true;
    }

    /**
     * Updates the frequency of each voice
     */
    @Override
    public void updateFrequency() {
        if(isInitialised){
            this.unison.setFrequencies(this.calculateUnisonPitch());
            this.center.setFrequency(super.frequency);
        }
    }

    /**
     * Routes center voice to center output and unison voices to unison output.
     * Also calculates the blend ratio for the gain adjustments
     */
    @Override
    public void patchOutput() {
        if(isInitialised){
            // this is deprecated since output belongs to the superclass which implicates inter-class dependencies which
            // we try to minimize
            // rather add both output devices together in calculateBuffer
            //output.addInput(cGain);
            //if(voices > 1) {
            //    output.addInput(sGain);
            //}
        }
    }

    @Override
    public void calculateBuffer(){
        for(int i = 0; i < outs; i++){
            for(int j = 0; j < bufferSize; j++){
                bufOut[i][j] = (1-0.5f*this.blend) * center.getValue(i, j) + (0.5f*this.blend) * unison.getValue(i, j);
            }
        }
    }


    /**
     * Calculates the unison pitch spread of THIS oscillator
     * @return float array containing individual unison frequencies
     */
    private float[] calculateUnisonPitch(){
        return calculateUnisonPitch(this.voices, this.frequency, this.spreadFunction, this.spread);
    }

    /**
     * Calculates a unison pitch offset
     * @param voices
     * @param spreadFunction
     * @param spread
     * @return array containing absolute frequency values
     */
    public static float[] calculateUnisonPitch(int voices, UGen frequency, double spreadFunction, float spread){
        float[] r = new float[voices];
        // absolute starting point of the linear spread
        float x = frequency.getValue() - spread;
        // note: case "voices = 1" is covered by this, so no need for compensation as in WaveOscillator
        for(int i = 0; i < voices; i++){
            r[i] = (float)(x + (i/Math.pow(voices-1f, spreadFunction)) * 2 * spread);
        }
        return r;
    }

    /**
     * Kills the voices and pauses the output
     */
    @Override
    public void kill(){
        if(center != null){
            center.kill();
        }
        if(unison != null) {
            unison.kill();
        }
        output.pause(true);
        isPaused = true;
    }

    /**
     * Shortcut for pause(false)
     */
    public void start(){
        this.pause(false);
    }

    /**
     * Shortcut for pause(true)
     */
    public void pause(){
        this.pause(true);
    }

    /**
     * {@see UGen}
     * @param paused pause or play
     */
    @Override
    public void pause(boolean paused){
        if(isPaused != paused){
            if(center != null)
                center.pause(paused);
            if(unison != null)
                unison.pause(paused);
            output.pause(paused);
            isPaused = paused;
        }

    }

    private void needsRefresh(){
        this.pause(true);
        this.createOscillator();
        this.updateFrequency();
        this.patchOutput();
        this.pause(false);
    }
}
