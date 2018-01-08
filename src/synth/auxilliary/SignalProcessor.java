package synth.auxilliary;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import synth.ui.components.Canvas;

public class SignalProcessor extends UGen {

    Canvas canvas;

    public SignalProcessor(AudioContext ac){
        super(ac, 2, 2);
        this.outputInitializationRegime = OutputInitializationRegime.RETAIN;
        bufOut = bufIn;
    }

    public void bind(Canvas c){
        if(c != null)
            this.canvas = c;
    }

    public void calculateBuffer(){
        this.bufOut = this.bufIn;
        if(canvas != null){
            canvas.update(bufIn);
        }
    }
}
