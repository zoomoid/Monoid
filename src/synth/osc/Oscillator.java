package synth.osc;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.data.Pitch;
import synth.modulation.Envelope;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.Glide;
import net.beadsproject.beads.ugens.Static;
import synth.auxilliary.Device;

import javax.sound.midi.*;

public abstract class Oscillator extends UGen implements Device {

    /**
     * The frequency the oscillation is happening at
     */
    protected UGen frequency;
    /**
     * MIDI note value
     */
    protected int midiNote;
    /**
     * Oscillator output device
     */
    protected Gain output;
    /**
     * The AudioContext the oscillator is working in
     */
    protected AudioContext ac;

    /**
     * Volume Envelope
     */
    protected Envelope volumeEnvelope;

    /**
     * Frequency Envelope
     */
    protected Envelope frequencyEnvelope;

    /**
     * Whether Oscillator is velocity sensitive or not
     */
    protected boolean isVelocitySensitive;
    /**
     * Factor by which the linear velocity falloff gets multiplied
     * By default this is 1 meaning: velocity / 127 * output.gain();
     */
    protected float velocityFactor;

    /**
     * Pause boolean. This flag gets set as soon as an Oscillator is paused
     */
    protected boolean isPaused;
    /**
     * Change boolean. This flag gets set by setters in child classes as soon as major changes occur
     */
    protected boolean hasChanged;
    /**
     * Initialisation boolean. This prevents setup from being called more than once on a certain oscillator
     */
    protected boolean isInitialised;

    /**
     * Creates an empty oscillator frame
     * @param ac AudioContext
     */
    public Oscillator(AudioContext ac){
        this(ac, 0f);
    }

    /**
     * Creates a new Oscillator frame
     * @param ac AudioContext
     * @param frequency Oscillator frequency
     */
    public Oscillator(AudioContext ac, float frequency){
        super(ac, 2, 2);
        this.ac = ac;
        this.output = new Gain(ac, 2, 1f);
        this.volumeEnvelope = new Envelope(this.ac, 5, 0, 1f, 20);
        this.output.setGain(this.volumeEnvelope);
        this.frequencyEnvelope = new Envelope(this.ac, 0, 0, 1f, 0);
        this.frequency = new Static(ac, frequency);
        velocityFactor = 1;
        isVelocitySensitive = false;
        midiNote = -1;
        this.outputInitializationRegime = OutputInitializationRegime.RETAIN;
        this.outputPauseRegime = OutputPauseRegime.ZERO;
        this.addInput(output);
        bufOut = bufIn;
    }

    /*
    KEY METHODS FOR EACH OSCILLATOR. THESE MUST BE PROVIDED.
     */

    /**
     * Creates the voice(s) of the oscillator
     */
    public abstract void createOscillator();

    /**
     * Sets the frequency / frequencies of the voice(s)
     */
    public abstract void updateFrequency();

    /**
     * Routes the sound to the output(s)
     */
    public abstract void patchOutput();

    /**
     * Returns the current frequency of the oscillation
     * @return frequency
     */
    public UGen getFrequency(){
        return this.frequency;
    }

    /**
     * Set the frequency of the oscillation
     * @param frequency frequency in Hz
     */
    public void setFrequency(UGen frequency){
        this.frequency = frequency;
    }

    public void setFrequency(float frequency){
        float old = this.frequency.getValue();
        Glide temp = new Glide(this.ac, old, 20);
        temp.setValue(frequency);
        this.setFrequency(temp);
    }

    /**
     * Set the (maximum) gain of the oscillator
     * This is achieved by setting the maximum value of the volume envelope
     * If not stated otherwise, this will be equal with the sustain or rather
     * adds another layer of control
     * @param gain relative gain in [0,1]
     */
    public void setOutput(float gain){
        if(gain <= 1f && gain >= 0f){
            this.volumeEnvelope.maximumGain(gain);
        }
    }

    public Envelope volumeEnvelope(){
        return this.volumeEnvelope;
    }

    public Envelope frequencyEnvelope(){
        return this.frequencyEnvelope;
    }

    /**
     * Returns the output of the Oscillator
     * @return output device
     */
    public Gain output(){
        return output;
    }


    @Override
    public void calculateBuffer(){
        for(int i = 0; i < outs; i++){
            bufOut[i] = output.getOutBuffer(i);
        }
    }


    /**
     * Sets the currently playing MIDI note
     * @param midiNote integer value of the MIDI note
     */
    public void setMidiNote(int midiNote) {
        this.midiNote = midiNote;
    }

    /**
     * Gets the current playing MIDI note
     * @return the currently playing MIDI note
     */
    public int getMidiNote() {
        return midiNote;
    }

     /*
        Velocity functions
     */

    /**
     * Enable or disable oscillator velocity sensitivity
     * meaning whether the key velocity by a MIDI event should
     * affect the volume of the oscillator by a constant factor (velocityFactor)
     * @param isSensitive boolean whether to enable or disable velocity sensitivity
     */
    public void setVelocitySensitivity(boolean isSensitive){
        this.isVelocitySensitive = isSensitive;
    }

    /**
     * Whether the oscillator is velocity sensitive
     * @return true, if sensitive, false otherwise
     */
    public boolean isVelocitySensitive() {
        return isVelocitySensitive;
    }

    /**
     * Sets the velocityFactor which is applied to the volume
     * Note: The new factor gets applied as soon as a new MIDI note on command is received
     *
     * Note: Clipping is to be prevented by the user, either by using a limiter
     *       (RangeLimiter Beads UGen, when implementing a chain) otherwise, for factors greateer
     *       than 1, clipping may occur, when velocity/127 * velocityFactor * gain is greater than 1
     * @param velocityFactor constant factor to be applid onto the volume
     */
    public void setVelocityFactor(float velocityFactor) {
        this.velocityFactor = velocityFactor;
    }

    /**
     * Gets the current velocity factor
     * @return constant velocity factor as float
     */
    public float getVelocityFactor() {
        return velocityFactor;
    }

    protected void noteOff(){
        this.volumeEnvelope.noteOff();
        this.frequencyEnvelope.noteOff();
    }

     void noteOn(){
        this.volumeEnvelope.noteOn();
        this.frequencyEnvelope.noteOn();
    }

    public abstract void send(ShortMessage m, long timeStamp);
}
