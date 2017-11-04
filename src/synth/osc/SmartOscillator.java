package synth.osc;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
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
        this.outputInitializationRegime = OutputInitializationRegime.RETAIN;
        bufOut = bufIn;
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
            cGain.setGain((float)(1-0.5*this.blend));
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
            hasChanged = true;
            this.voices = voices;
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
    public void createOscillator(){
        unison = new UnisonOscillator(super.ac, this.wave, this.voices - 1);
        center = new WavePlayer(super.ac, super.frequency, this.wave);
    }

    /**
     * Updates the frequency of each voice
     */
    @Override
    public void updateFrequency() {
        if(isInitialised()){
            this.unison.setFrequencies(this.calculateUnisonPitch());
            this.center.setFrequency(super.frequency);
        }
    }

    /**
     * Routes center voice to center output and unison voices to unison output.
     * Also calculates the blend ratio for the gain adjustments
     */
    @Override
    public void patchOutputs() {
        cGain = new Gain(super.ac,2,1f);
        sGain = new Gain(super.ac,2,1f);

        cGain.addInput(center);
        sGain.addInput(unison);

        this.setBlend(this.blend);

        output.addInput(cGain);
        if(voices > 1)
            output.addInput(sGain);
    }

    @Override
    public void calculateBuffer(){
        for(int i = 0; i < outs; i++){
            bufOut[i] = output.getOutBuffer(i);
        }
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
            cGain.kill();
        }
        if(unison != null) {
            unison.kill();
            sGain.kill();
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

    /**
     * Implements the MIDI Synthesizer method send, which is called by a transmitter (Sequencer) to
     * send MIDI data to a synthesizer
     * @param message MIDI data as ShortMessage type
     * @param timeStamp (currently not implemented) used to calculate offset for delay compensation
     *                  Since delay is an issue for the whole synthesizer, this is a minor problem
     *                  with which can be dealt later on
     */
    public void send(ShortMessage message, long timeStamp){
        if(message.getCommand() == ShortMessage.NOTE_OFF){
            this.pause();
            this.setFrequency(0);
            this.setMidiNote(-1);
        } else {
            // call for the static function translating the MIDI key to frequency range
            this.setFrequency(MIDIUtils.midi2frequency(message.getData1()));
            this.setMidiNote(message.getData1());
            // if oscillator is velocity sensitive, adjust volume
            if(this.isVelocitySensitive){
                this.output.setGain(message.getData2() / 127f * this.output.getGain());
            }
            this.start();
        }
    }

}
