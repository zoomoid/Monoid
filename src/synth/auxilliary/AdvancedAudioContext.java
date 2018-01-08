package synth.auxilliary;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.AudioIO;
import net.beadsproject.beads.core.IOAudioFormat;
import net.beadsproject.beads.core.UGen;

public class AdvancedAudioContext extends AudioContext {

    public final Normaliser normaliser;

    public AdvancedAudioContext(AudioIO io, int bufferSize, IOAudioFormat audioFormat){
        super(io, bufferSize, audioFormat);
        normaliser = new Normaliser(this, true, 3);
        super.out.addInput(normaliser);
    }

    public UGen out(){
        return this.normaliser;
    }
}
