package synth.osc;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.IOAudioFormat;
import net.beadsproject.beads.data.Buffer;
import org.jaudiolibs.beads.AudioServerIO;
import net.beadsproject.beads.ugens.*;
import synth.SynthController;

import java.util.ArrayList;


public class OscillatorController {
    /**
     * SmartOscillator object
     */
    private ArrayList<Oscillator> oscillators;

    /**
     * Master output of oscillator
     */
    private Gain master;

    private RangeLimiter limiter;

    /**
     * Public audio context for referencing wherever needed
     */
    public final AudioContext ac;

    public final SynthController context;

    /**
     * Creates a new WaveOscillator Manager
     */
    public OscillatorController(SynthController context){
        this.context = context;
        this.ac = SynthController.ac;
        oscillators = new ArrayList<>();
        oscillators.add(new SmartOscillator(ac, 0f, Buffer.SINE, 1, 0f, 1));
        limiter = new RangeLimiter(ac, 1);
        master = new Gain(ac, 1, 0.95f);
        master.addInput(limiter);
        ac.out.addInput(master);
        ac.start();
    }

    public void setup(float frequency){
        for(Oscillator o : oscillators){
            o.setFrequency(frequency);
            limiter.addInput(o.output());
        }
    }

    public Oscillator addOscillator(Oscillator osc){
        if(osc != null){
            this.oscillators.add(osc);
            return osc;
        } else {
            return null;
        }
    }

    /**
     * Get the oscilator
     * @return WaveOscillator
     */
    public Oscillator getOsc(int id){
        return oscillators.get(id);
    }

    public SynthController getContext() {
        return context;
    }

    public ArrayList<Oscillator> getOscillators() {
        return oscillators;
    }

    /**
     * Get the master output of the oscillator
     * @return Master gain
     */
    public Gain getMaster(){
        return master;
    }

    public void setLimiterThreshold(float threshold){
        if(threshold <= 1 && threshold >= 0){
            this.limiter.setValue(threshold);
        }
    }
}
