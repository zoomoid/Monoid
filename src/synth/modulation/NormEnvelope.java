package synth.modulation;

import net.beadsproject.beads.core.AudioContext;

/**
 * NormEnvelope does not use shift / center value for usage in {@link Sum}
 */
public class NormEnvelope extends Envelope {

    /** Default envelope */
    public NormEnvelope(AudioContext ac){
        this(ac,5, 0, 1f, 20);
    }

    /**
     * AR-Envelope
     * @param attack attack time
     * @param release release time
     */
    public NormEnvelope(AudioContext ac, int attack, int release){
        this(ac, attack, 0, 1f, release);
    }

    /**
     * ADSR-Envelope
     * @param attack attack time
     * @param decay decay time
     * @param sustain sustain level
     * @param release release time
     */
    public NormEnvelope(AudioContext ac, int attack, int decay, float sustain, int release){
        super(ac, attack, decay, sustain, release);
    }

    @Override
    public void calculateBuffer(){
        current.update();
        for(int i = 0; i < bufferSize; i++){
            bufOut[0][i] = modulationStrength * current.getValue(0, i);
        }
    }

}
