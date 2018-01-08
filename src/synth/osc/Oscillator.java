package synth.osc;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.data.Pitch;
import synth.modulation.*;
import synth.container.Device;

import javax.sound.midi.*;

public abstract class Oscillator extends UGen implements Device {

    protected String type;

    /** The frequency the oscillation is happening at  */
    protected Modulatable frequency;
    /** Oscillator output gain */
    protected Modulatable gain;
    /** Phase UGen */
    //protected UGen phase;

    /** MIDI note value */
    protected int midiNote;

    /** The AudioContext the oscillator is working in */
    protected AudioContext ac;

    // TODO create function to add together Envelope and LFO to create one coherent modulatable parameter
    // TODO this can probably done by modifying the Modulatable interface / Modulator to wrap around Static (for static knob parameters), Envelopes and LFOs

    /** Volume Envelope */
    protected Envelope gainEnvelope;

    /** Frequency Envelope */
    protected Envelope frequencyEnvelope;

    /** Gain LFO */
    protected LFO gainLFO;

    /** Frequency LFO */
    protected LFO frequencyLFO;

    /** Gain Static */
    protected Static gainStatic;

    /** Frequency Static */
    protected Static frequencyStatic;

    /** Whether Oscillator is velocity sensitive or not */
    protected boolean isVelocitySensitive;
    /**
     * Factor by which the linear velocity falloff gets multiplied
     * By default this is 1 meaning: velocity / 127 * output.gain();
     */
    protected float velocityFactor;

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
        this(ac, new Static(ac, frequency));
    }

    public Oscillator(AudioContext ac, Modulatable frequency){
        super(ac, 2, 2);
        this.ac = ac;
        this.gainEnvelope = new Envelope(this.ac, 5, 0, 1f, 20);
        this.frequencyEnvelope = new Envelope(this.ac, 0, 0, 1f, 0);
        this.frequencyLFO = new LFO(this.ac, LFO.Type.SINE, 0f, 1f);
        this.gainLFO = new LFO(this.ac, LFO.Type.SINE, 0f, 1f);
        this.gainStatic = new Static(this.ac, 1f);
        this.frequencyStatic = new Static(this.ac, frequency.getValue());

        this.frequency = new Sum(ac, frequencyStatic, frequencyEnvelope, frequencyLFO);
        this.gain = new Sum(ac, gainStatic, gainEnvelope, gainLFO);

        velocityFactor = 1;
        isVelocitySensitive = false;
        midiNote = -1;
        this.outputInitializationRegime = OutputInitializationRegime.ZERO;
        this.outputPauseRegime = OutputPauseRegime.ZERO;
        this.setType();
    }

    public String getType(){
        return this.type;
    }

    public abstract void setType();

    /**
     * Returns the current frequency of oscillation
     * @return frequency UGen
     */
    public Modulatable getFrequency(){
        return this.frequency;
    }

    /**
     * Returns the current oscillator output gain
     * @return gain UGen
     */
    public Modulatable getGain(){
        return this.gain;
    }

    /**
     * Returns the current phase offset
     * @return phase UGen
     */
    /*public UGen getPhase(){
        return this.phase;
    }*/

    /**
     * Sets the frequency of oscillation
     * NOTE This REPLACES the frequency UGen with a new one
     * @param frequency static oscillation frequency
     * @return this oscillator instance
     */
    public Oscillator setFrequency(LFO frequency){
        if(frequency != null){
            this.frequencyLFO = frequency;
            ((Sum)this.frequency).setLFO(frequency);
        }
        return this;
    }

    /**
     * Sets the frequency of oscillation
     * NOTE This REPLACES the frequency UGen with a new one
     * @param frequency static oscillation frequency
     * @return this oscillator instance
     */
    public Oscillator setFrequency(Envelope frequency){
        if(frequency != null){
            this.frequencyEnvelope = frequency;
            ((Sum)this.frequency).setEnvelope(frequency);
        }
        return this;
    }

    /**
     * Sets the frequency of oscillation
     * NOTE This only SETS the value of the current UGen (which might be a modulator)
     *      to the new static float value. To replace the UGen, rather use {@see Oscillator.setFrequency(UGen frequency)}
     * @param frequency static oscillation frequency
     * @return this oscillator instance
     */
    public Oscillator setFrequency(float frequency){
        this.frequencyStatic = new Static(ac, frequency);
        this.frequency.setValue(frequency);
        return this;
    }

    public Oscillator setFrequency(Sum frequency){
        if(frequency != null){
            this.frequency = frequency;
            this.frequencyStatic = frequency.getStatic();
            this.frequencyEnvelope = frequency.getEnvelope();
            this.frequencyLFO = frequency.getLFO();
        }
        return this;
    }

    public Oscillator setFrequency(ModulationOscillator frequencyOsc){
        if(frequencyOsc != null){
            this.frequency = frequencyOsc;
            this.frequencyStatic = null;
            this.frequencyEnvelope = null;
            this.frequencyLFO = null;
        }
        return this;
    }

    /**
     * Sets the gain of the oscillator
     * NOTE This REPLACES the gain UGen with a new one
     * @param gain gain UGen
     * @return this oscillator instance
     */
    public Oscillator setGain(LFO gain){
        if(gain != null){
            this.gainLFO = gain;
            ((Sum)this.gain).setLFO(gain);
        }
        return this;
    }

    /**
     * Sets the gain of the oscillator
     * NOTE This REPLACES the gain UGen with a new one
     * @param gain gain UGen
     * @return this oscillator instance
     */
    public Oscillator setGain(Envelope gain){
        if(gain != null){
            this.gainEnvelope = gain;
            ((Sum)this.gain).setEnvelope(gain);
        }
        return this;
    }

    /**
     * Sets the gain of the oscillator
     * NOTE This only SETS the value of the current UGen (which might be a modulator)
     *      to the new static float value. To replace the UGen, rather use {@see Oscillator.setGain(UGen gain)}
     * @param gain static gain value as float
     * @return this oscillator instance
     */
    public Oscillator setGain(float gain){
        this.gainStatic = new Static(ac, gain);
        this.gain.setValue(gain);
        return this;
    }

    public Oscillator setGain(Sum gain){
        if(gain != null){
            this.gain = gain;
            this.gainStatic = gain.getStatic();
            this.gainEnvelope = gain.getEnvelope();
            this.gainLFO = gain.getLFO();
        }
        return this;
    }

    public Oscillator setGain(ModulationOscillator gainOsc){
        if(gainOsc != null){
            this.gain = gainOsc;
            this.gainStatic = null;
            this.gainEnvelope = null;
            this.gainLFO = null;
        }
        return this;
    }

    @Override
    public void calculateBuffer(){

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
     * @param velocityFactor constant factor to be applied onto the volume
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

    public void noteOff(){
        this.gain.noteOff();
        this.frequency.noteOff();
    }

    public void noteOn(){
        this.gain.noteOn();
        this.frequency.noteOn();
    }

    public void send(ShortMessage message, long timeStamp){
        if(message.getCommand() == ShortMessage.NOTE_OFF){
            this.noteOff();
        } else {
            // call for the static function translating the MIDI key to frequency range
            this.setFrequency(Pitch.mtof(message.getData1()));
            this.setMidiNote(message.getData1());
            // if oscillator is velocity sensitive, adjust volume
            if(this.isVelocitySensitive){
                this.setGain(message.getData2() / 127f * this.gain.getValue());
            }
            this.noteOn();
        }
    }

    public Envelope gainEnvelope() {
        return gainEnvelope;
    }

    public Envelope frequencyEnvelope() {
        return frequencyEnvelope;
    }

    public LFO gainLFO() {
        return gainLFO;
    }

    public LFO frequencyLFO() {
        return frequencyLFO;
    }

    public Static gainStatic() {
        return gainStatic;
    }

    public Static frequencyStatic() {
        return frequencyStatic;
    }
}
