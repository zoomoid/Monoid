package synth.osc;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.IOAudioFormat;
import net.beadsproject.beads.data.Buffer;
import org.jaudiolibs.beads.AudioServerIO;
import net.beadsproject.beads.ugens.*;


public class OscillatorManager {

    // TODO allow more than one oscillator
    /**
     * SmartOscillator object
     */
    private SmartOscillator osc;

    /**
     * Master output of oscillator
     */
    private Gain master;

    private RangeLimiter limiter;

    /**
     * Public audio context for referencing wherever needed
     */
    public static AudioContext ac = new AudioContext(new AudioServerIO.JavaSound("Primärer Soundtreiber"), 4096, new IOAudioFormat(44100, 24, 0, 2));

    /**
     * Creates a new WaveOscillator Manager
     */
    public OscillatorManager(){
        osc = new SmartOscillator(ac, 0f, Buffer.SINE, 1, 0f, 1);
        limiter = new RangeLimiter(ac, 1);
        master = new Gain(ac, 1, 0.95f);
        master.addInput(limiter);
        ac.out.addInput(master);
        ac.start();
    }

    // TODO create method to pass SmartOscillator object to Oscillator Manager

    public void setup(float frequency){
        osc.setup();
        osc.setFrequency(frequency);
        limiter.addInput(osc.output());
    }

    /**
     * Get the master output of the oscillator
     * @return Master gain
     */
    public Gain getMaster(){
        return master;
    }

    /**
     * Get the oscilator
     * @return WaveOscillator
     */
    public SmartOscillator getOsc() {
        return osc;
    }
}
