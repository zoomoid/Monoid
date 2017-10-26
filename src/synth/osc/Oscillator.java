package synth.osc;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.ugens.Envelope;
import net.beadsproject.beads.ugens.Gain;
import synth.auxilliary.MIDIUtils;
import synth.midi.MidiInput;

import javax.sound.midi.*;

public abstract class Oscillator extends UGen {

    /**
     * The frequency the oscillation is happening at
     */
    protected float frequency;
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
     *
     */
    public boolean UNISON_OSCILLATOR = false;

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
        super(ac);
        this.ac = ac;
        this.output = new Gain(ac, 1, 1f);
        this.frequency = frequency;
        pause();
        isPaused = true;
        hasChanged = false;
        isInitialised = false;
        velocityFactor = 1;
        isVelocitySensitive = false;
        midiNote = -1;
    }

    /*
    KEY METHODS FOR EACH OSCILLATOR. THESE MUST BE PROVIDED.
     */

    /**
     * Creates the voice(s) of the oscillator
     */
    abstract void createOscillator();

    /**
     * Sets the frequency / frequencies of the voice(s)
     */
    abstract void updateFrequency();

    /**
     * Routes the sound to the output(s)
     */
    abstract void patchOutputs();

    /**
     * Returns the current frequency of the oscillation
     * @return frequency
     */
    public float getFrequency(){
        return this.frequency;
    }

    /**
     * Set the frequency of the oscillation
     * @param frequency frequency in Hz
     */
    public void setFrequency(float frequency){
        this.hasChanged = true;
        this.frequency = frequency;
        this.changed();
    }

    /**
     * Set the gain of the oscillator output
     * @param gain relative gain in [0,1]
     */
    public void setOutput(float gain){
        if(gain <= 1f && gain >= 0f)
            this.output.setGain(gain);
    }

    /**
     * Returns the output of the Oscillator
     * @return output device
     */
    public Gain output(){
        return output;
    }

    /**
     * Whether the oscillator is paused or not
     * @return isPaused
     */
    public boolean isPaused(){
        return isPaused;
    }

    public boolean isInitialised() {
        return isInitialised;
    }

    /**
     * Setup handler
     * To be called when a new oscillator shall start. Creates voices and routes them to the output
     */
    public void setup(){
        if(!isInitialised){
            createOscillator();
            updateFrequency();
            patchOutputs();
            start();
            isInitialised = true;
        }
    }

    /**
     * Change handler
     * To be called whether a major change happends to the Oscillator like switching the buffer or
     * (not for {@link SmartOscillator}) change of the unison voice amount. Kills the currently running oscillator
     * voices and creates fresh ones.
     */
    public void changed(){
        if(hasChanged){
            kill();
            createOscillator();
            updateFrequency();
            patchOutputs();
            start();
        }
    }

    /**
     * Starts the oscillator
     */
    public abstract void start();

    /**
     * Pauses the oscillator while keeping its state
     */
    public abstract void pause();

    /**
     * Kills the oscillator voices
     */
    public abstract void kill();

    @Override
    public void calculateBuffer(){
        zeroOuts();
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