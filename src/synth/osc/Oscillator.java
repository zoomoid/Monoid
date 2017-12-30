package synth.osc;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.data.Pitch;
import synth.modulation.Envelope;
import synth.container.Device;
import synth.modulation.Modulatable;
import synth.modulation.Modulator;
import synth.modulation.Static;

import javax.sound.midi.*;

public abstract class Oscillator extends UGen implements Device {

    protected String type;

    /** The frequency the oscillation is happening at  */
    protected Modulatable frequency;
    /** Oscillator output gain */
    protected Modulatable gain;
    /** Phase UGen */
    protected UGen phase;

    /** MIDI note value */
    protected int midiNote;

    /** The AudioContext the oscillator is working in */
    protected AudioContext ac;

    protected boolean isUnisonOscillator;

    /** Volume Envelope */
    protected Envelope volumeEnvelope;

    /** Frequency Envelope */
    protected Envelope frequencyEnvelope;

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
        this.volumeEnvelope = new Envelope(this.ac, 5, 0, 1f, 20);
        this.frequencyEnvelope = new Envelope(this.ac, 0, 0, 1f, 0);

        this.frequency = frequency;
        this.gain = new Static(ac, 1f);
        this.phase = new Static(ac, -1f);

        velocityFactor = 1;
        isVelocitySensitive = false;
        midiNote = -1;
        this.outputInitializationRegime = OutputInitializationRegime.RETAIN;
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
    public UGen getPhase(){
        return this.phase;
    }

    /**
     * Sets the frequency of oscillation
     * NOTE This REPLACES the frequency UGen with a new one
     * @param frequency static oscillation frequency
     * @return this oscillator instance
     */
    public Oscillator setFrequency(Modulatable frequency){
        if(frequency != null){
            this.frequency = frequency;
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
        if(this.frequency != null){
            this.frequency.setValue(frequency);
            return this;
        } else {
            return this.setFrequency(new Static(ac, frequency));
        }

    }

    /**
     * Sets the gain of the oscillator
     * NOTE This REPLACES the gain UGen with a new one
     * @param gain gain UGen
     * @return this oscillator instance
     */
    public Oscillator setGain(Modulatable gain){
        if(gain != null){
            this.gain = gain;
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
        if(this.gain != null){
            this.gain.setValue(gain);
            return this;
        } else {
            return this.setGain(new Static(ac, gain));
        }
    }

    /**
     * Sets a constant phase offset for the oscillation
     * @param phase phase offset in [-1, 1]. NOTE that -1 is to be used for random phase offset since -1 and 1 are equivalent in effect on the oscillation
     * @return this oscillator instance
     */
    public Oscillator setPhase(float phase){
        if(this.phase != null){
            this.phase.setValue(phase);
            return this;
        } else {
            return this.setPhase(new Static(ac, phase));
        }
    }

    /**
     * Sets a variable phase offset for the oscillation
     * @param phase variable phase offset UGen
     * @return this oscillator instance
     */
    public Oscillator setPhase(UGen phase){
        if(phase != null){
            this.phase = phase;
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

    protected void noteOff(){
        this.gain.noteOff();
        this.frequency.noteOff();
    }

     void noteOn(){
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
}
