package synth.filter;

import net.beadsproject.beads.core.AudioContext;
import synth.SynthController;

import java.util.ArrayList;

public class FilterController {

    private ArrayList<Filter> filters;

    public final AudioContext ac;
    public final SynthController context;

    public FilterController(SynthController context){
        this.context = context;
        this.ac = SynthController.ac;
        this.filters = new ArrayList<>();
    }

    public SynthController getContext() {
        return context;
    }
}
