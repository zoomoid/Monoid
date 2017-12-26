package synth.osc;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Static;

public class BasicOscillator extends Oscillator implements WavetableOscillator {

    /** The phase envelope. */
    private UGen phaseEnvelope;
    /* Phase sampler */
    private float currentPhase;
    /** The Buffer. */
    private Buffer wave;
    /** To store the inverse of the sampling frequency. */
    private float one_over_sr;

    private SmartOscillator dependent;

    public BasicOscillator(AudioContext ac){
        this(ac, 0f, Buffer.SINE);
    }


    BasicOscillator(AudioContext ac, SmartOscillator dependent, UGen frequency, Buffer wave){
        this(ac, frequency, wave);
        this.dependent = dependent;
        this.addDependent(this.dependent);
        this.currentPhase = super.phase.getValue();
    }

    public BasicOscillator(AudioContext ac, UGen frequency, Buffer wave){
        super(ac, frequency);
        if(wave != null){
            this.wave = wave;
        } else {
            this.wave = Buffer.SINE;
        }
        this.outputInitializationRegime = OutputInitializationRegime.RETAIN;
        this.phase = new Static(ac, -1);
        this.one_over_sr = 1f / context.getSampleRate();
    }

    public BasicOscillator(AudioContext ac, float frequency, Buffer wave){
        this(ac, new Static(ac, frequency), wave);
    }

    BasicOscillator(AudioContext ac, SmartOscillator dependent, float frequency, Buffer wave){
        this(ac, frequency, wave);
        this.dependent = dependent;
        this.addDependent(this.dependent);
        this.isUnisonOscillator = false;
    }

    public Buffer getWave(){
        return this.wave;
    }

    public BasicOscillator setWave(Buffer wave){
        if(wave != null){
            this.wave = wave;
        }
        return this;
    }

    public final void setType(){
        if(this.type == null){
            this.type = "BasicOscillator";
        }
    }

    @Override
    public void calculateBuffer(){
        this.frequency.update();
        this.gain.update();
        this.phase.update();
        float prevPhase = currentPhase;
        for(int i = 0; i < outs; i++){
            currentPhase = prevPhase;
            for(int j = 0; j < bufferSize; j++){
                currentPhase = (((currentPhase + frequency.getValue(i, j) * one_over_sr) % 1.f) + 1.f) % 1.f;
                bufOut[i][j] = this.wave.getValueFraction(currentPhase);
            }
        }
    }

    public void createOscillator(){

    }

    public void updateFrequency(){

    }
}
