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
    private net.beadsproject.beads.ugens.Envelope backend;

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
    }


    @Override
    public void update(){
        backend.clear();
        // set attack
        // TODO add note trigger on Bead
        backend.addSegment(1f, this.attack);
        // set decay
        backend.addSegment(this.sustain, this.decay);
        // set release
        // TODO add note trigger off Bead
        backend.addSegment(0f, this.release);
    }

    @Override
    public void calculateBuffer(){

    }

}
