package synth.modulation;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;

/**
 * A basic envelope wrapping class.
 */
public class Envelope extends UGen {

    /**
     * attack time in milliseconds
     */
    private int attack;

    /**
     * maximum gain attack rises to
     * this gets determined by the owning object
     */
    private float maximumGain;
    /**
     * decay time in milliseconds
     */
    private int decay;

    /**
     * sustain level in [0,1]
     */
    private float sustain;

    /**
     * release time in milliseconds
     */
    private int release;

    /**
     * Audio context
     */
    private AudioContext context;

    /**
     * Backend Envelope UGen
     */
    private net.beadsproject.beads.ugens.Envelope current;

    /**
     * Default envelope
     */
    public Envelope(AudioContext ac){
        this(ac,5, 0, 1f, 20);
    }

    /**
     * AR-Envelope
     * @param attack attack time
     * @param release release time
     */
    public Envelope(AudioContext ac, int attack, int release){
        this(ac, attack, 0, 1f, release);
    }

    /**
     * ADSR-Envelope
     * @param attack attack time
     * @param decay decay time
     * @param sustain sustain level
     * @param release release time
     */
    public Envelope(AudioContext ac, int attack, int decay, float sustain, int release){
        super(ac, 0, 1);
        this.context = ac;
        this.attack = attack;
        this.decay = decay;
        this.sustain = sustain;
        this.release = release;
        this.maximumGain = 1f;
        current = new net.beadsproject.beads.ugens.Envelope(this.context);
        this.outputInitializationRegime = OutputInitializationRegime.ZERO;
        this.outputPauseRegime = OutputPauseRegime.ZERO;
    }

    /**
     * Gets the attack time in [ms]
     * @return attack time
     */
    public int attack() {
        return attack;
    }

    /**
     * Sets the attack time
     * @param attack attack time in ms
     */
    public void attack(int attack) {
        this.attack = attack;
    }

    /**
     * Gets the maximum value for the envelope to rise to
     * @return maximum gain
     */
    public float maximumGain() {
        return maximumGain;
    }

    /**
     * Sets the maximum value for the envelope to rise to
     * @param maximumGain maximum gain
     */
    public void maximumGain(float maximumGain) {
        this.maximumGain = maximumGain;
    }

    /**
     * Gets the decay time in [ms]
     * @return decay time
     */
    public int decay() {
        return decay;
    }
    /**
     * Sets the decay time in [ms]
     * @param decay decay time
     */
    public void decay(int decay) {
        this.decay = decay;
    }

    /**
     * Gets the sustain level
     * @return sustain level
     */
    public float sustain() {
        return sustain;
    }
    /**
     * Sets the sustain level
     * @param sustain decay time
     */
    public void sustain(float sustain) {
        this.sustain = sustain;
    }
    /**
     * Gets the release time in [ms]
     * @return release time
     */
    public int release() {
        return release;
    }
    /**
     * Sets the release time in [ms]
     * @param release release time
     */
    public void release(int release) {
        this.release = release;
    }

    @Override
    public synchronized void calculateBuffer(){
        bufOut[0] = current.getOutBuffer(0);
    }

    /**
     * Method for {@link synth.auxilliary.Device} when a send for MIDI data with noteOn command appears to happen
     */
    public void noteOn(){
        this.current.setValue(0);
        this.current.clear();
        this.current.addSegment(this.maximumGain, this.attack);
        this.current.addSegment(this.sustain, this.decay);
    }
    /**
     * Method for {@link synth.auxilliary.Device} when a send for MIDI data with noteOff command appears to happen
     */
    public void noteOff(){
        this.current.addSegment(0f, this.release);
    }

}
