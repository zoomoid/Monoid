package synth.auxilliary;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;

public class BufferDebugger extends UGen {
    public BufferDebugger(AudioContext context){
        super(context, 2, 2);
        outputInitializationRegime = OutputInitializationRegime.RETAIN;
        this.bufOut = this.bufIn;
    }

    @Override
    public void calculateBuffer(){
        bufOut = bufIn;
        assert true;
    }

}
