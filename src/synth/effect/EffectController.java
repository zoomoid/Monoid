package synth.effect;

import net.beadsproject.beads.core.AudioContext;
import synth.SynthController;

public class EffectController {

    public final AudioContext ac;
    public final SynthController context;

    public EffectController(SynthController context){
        this.context = context;
        this.ac = SynthController.ac;
    }

    public SynthController getContext() {
        return context;
    }
}
