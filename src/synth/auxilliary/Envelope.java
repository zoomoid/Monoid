package synth.auxilliary;

public class Envelope {

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
     * Default envelope
     */
    public Envelope(){
        this(5, 0, 1f, 20);
    }

    /**
     * AR-Envelope
     * @param attack attack time
     * @param release release time
     */
    public Envelope(int attack, int release){
        this(attack, 0, 1f, release);
    }

    /**
     * ADSR-Envelope
     * @param attack attack time
     * @param decay decay time
     * @param sustain sustain level
     * @param release release time
     */
    public Envelope(int attack, int decay, float sustain, int release){
        this.attack = attack;
        this.decay = decay;
        this.sustain = sustain;
        this.release = release;
    }

    /* Getters */

    public int attack(){
        return attack;
    }

    public int decay(){
        return decay;
    }

    public float sustain(){
        return sustain;
    }

    public int release(){
        return release;
    }

    /* Setters */

    public void attack(int attack){
        this.attack = attack;
    }
    public void decay(int decay){
        this.decay = decay;
    }
    public void sustain(float sustain){
        this.sustain = sustain;
    }
    public void release(int release){
        this.release = release;
    }
}
