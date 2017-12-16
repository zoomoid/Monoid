package synth.bus;

import net.beadsproject.beads.core.AudioContext;
import synth.SynthController;

import java.util.ArrayList;

public class BusController {
    public final AudioContext ac;
    public final SynthController context;

    private ArrayList<Bus> busses;

    public BusController(SynthController context){
        this.context = context;
        this.ac = SynthController.ac;
        busses = new ArrayList<>();
    }

    public SynthController getContext() {
        return context;
    }

    public ArrayList<Bus> busses() {
        return busses;
    }


}
