package synth.osc;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;

public class PolyphonyOscillator extends UGen {

    // TODO pass several frequencies to Oscillator
    // TODO pass buffer type to Oscillator
    public PolyphonyOscillator(AudioContext ac){
        super(ac);
    }

    @Override
    public void calculateBuffer(){
        // TODO calculate buffer by stacking frequencies
        // @see UnisonOscillator calculation
    }
}
