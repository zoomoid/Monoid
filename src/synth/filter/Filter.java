package synth.filter;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.ugens.Envelope;

public abstract class Filter extends UGen {

    private float cutoff;

    private float q;

    private float resonance;

    private Envelope cutoffEnvelope;

    private Envelope resonanceEnvelope;

    public Filter(AudioContext ac){
        super(ac);
    }

}
