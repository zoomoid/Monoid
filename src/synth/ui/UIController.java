package synth.ui;

import net.beadsproject.beads.core.AudioContext;
import synth.SynthController;

public class UIController {
    public final AudioContext ac;
    public final SynthController context;


    public UIController(SynthController context){
        this.context = context;
        this.ac = SynthController.ac;
    }

    public SynthController getContext() {
        return context;
    }
}
